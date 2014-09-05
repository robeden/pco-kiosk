package net.stratfordpark.pco;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import spark.Request;
import spark.Route;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.get;
import static spark.SparkBase.setPort;
import static spark.SparkBase.staticFileLocation;


public class Main {
	private static final DateFormat WEEK_DATE_FORMAT = new SimpleDateFormat( "MMMM d" );
	private static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat( "h:mm" );


	private static final AtomicBoolean invert_colors = new AtomicBoolean( true );


	public static void main(String[] args) throws Exception {
		if ( args.length < 4 ) {
			System.out.println(
				"Usage: <api_key> <api_secret> <access_key> <access_secret>" );
			System.exit( -1 );
			return;
		}


		// TODO: real CLI parser. Will also want: refresh time, etc.
		String api_key = args[ 0 ];
		String api_secret = args[ 1 ];
		String access_key = args[ 2 ];
		String access_secret = args[ 3 ];
		long update_period = TimeUnit.MINUTES.toMillis( 15 );
		int port = 8888;

		final AtomicReference<Data> data_slot = new AtomicReference<>();

		setPort( port );
		staticFileLocation( "/css" );

		get( "/", new Route() {
			@Override
			public Object handle( Request request, spark.Response response ) {
				Data data = data_slot.get();

				if ( data == null ) {
					return createLoadingPage();
				}

				return buildMainPage( data );
			}
		} );

		OAuthService service = new ServiceBuilder()
			.provider( PlanningCenterOnlineApi.class )
			.apiKey( api_key )
			.apiSecret( api_secret )
			.build();

		Token access_token = new Token( access_key, access_secret );

		final DataFetcher fetcher = new DataFetcher( service, access_token );

		Timer timer = new Timer( "Fetch timer" );
		timer.schedule( new TimerTask() {
			@Override
			public void run() {
				Data data = fetcher.fetchData();
				if ( data != null ) {
					data_slot.set( data );
				}
			}
		}, 0, update_period );

		// Color inverter
		timer.schedule( new TimerTask() {
			@Override
			public void run() {
				invert_colors.set( !invert_colors.get() );
			}
		}, TimeUnit.MINUTES.toMillis( 30 ), TimeUnit.MINUTES.toMillis( 30 ) );
	}


	private static String buildMainPage( Data data ) {
		StringWriter string_writer = new StringWriter();
		PrintWriter writer = new PrintWriter( string_writer );

		writer.println( "<!DOCTYPE html>" );
		writer.println( "<html>" );
		writer.println( "  <head>" );
		writer.println( "    <title>Volunteer Schedules</title>" );
		writer.println( "    <link rel=\"stylesheet\" type=\"text/css\" " +
			"href=\"bootstrap.min.css\"/>" );
		writer.println( "    <meta http-equiv=\"refresh\" content=\"120\">" );
		writer.println( "    <style>" );
		if ( invert_colors.get() ) {
			writer.println( "    body {" );
			writer.println( "      color: #FFF !important;" );
			writer.println( "      background-color: #000 !important;" );
			writer.println( "    }" );

			writer.println( "    .warning {" );
			writer.println( "      background-color: #3A3100 !important;" );
			writer.println( "    }" );
		}
		///////
//		writer.println( "      body {" );
//		writer.println( "        background-image: url('logo.png');" );
//		writer.println( "        background-position: center;" );
//		writer.println( "        background-repeat: no-repeat;" );
//		writer.println( "      }" );
		///////
		writer.println( "    </style>" );
		writer.println( "</head>" );

		writer.println( "  <body>" );
		writer.println( "    <div class=\"container-fluid\">" );
		writer.println( "    <div class=\"page-header\">" );
		writer.println( "      <h1>Volunteer Schedules <small>" + data.getOrgName() +
			"</small></h1>" );
		writer.println( "    </div>" );

		writer.println( "    <h2>This Week <small>" +
			WEEK_DATE_FORMAT.format( data.getThisWeekDate() ) + "</small></h2>" );
		writer.println( "    <div class=\"week row\">" );

		int columns = determineGridColumns( data.getThisWeekServices().size() );
		for( ServiceData service_data : data.getThisWeekServices() ) {
			buildServiceBlock( service_data, columns, writer );
		}
		writer.println( "    </div>" );
		writer.println( "    <hr>" );


		writer.println( "    <h2>Next Week <small>" +
			WEEK_DATE_FORMAT.format( data.getNextWeekDate() ) + "</small></h2>" );
		writer.println( "    <div class=\"week row\">" );

		columns = determineGridColumns( data.getNextWeekServices().size() );
		for( ServiceData service_data : data.getNextWeekServices() ) {
			buildServiceBlock( service_data, columns, writer );
		}
		writer.println( "    </div>" );
		writer.println( "    <hr>" );


		writer.println( "    <h2>Two Weeks <small>" +
			WEEK_DATE_FORMAT.format( data.getTwoWeeksDate() ) + "</small></h2>" );
		writer.println( "    <div class=\"week row\">" );

		columns = determineGridColumns( data.getTwoWeeksServices().size() );
		for( ServiceData service_data : data.getTwoWeeksServices() ) {
			buildServiceBlock( service_data, columns, writer );
		}
		writer.println( "    </div>" );



		writer.println( "    </div>" );
		writer.println( "  </body>" );
		writer.println( "</html>" );

		return string_writer.toString();
	}


	private static void buildServiceBlock( ServiceData data, int columns,
		PrintWriter writer ) {

		writer.println( "      <div class=\"col-md-" + columns + "\">" );
		writer.println( "        <h3>" + data.getName() + " <small>" +
			TIME_DATE_FORMAT.format( data.getStartDate() ) + "</small></h3>" );
		writer.println( "        <table class=\"table\">" );
		for( Map.Entry<String,List<ServiceData.NeedOrVolunteer>> entry :
			data.getVolunteerMap().entrySet() ) {

			writer.print( "        <tr><td>" + entry.getKey() + "</td>" );


			boolean needs_warning = false;
			for( ServiceData.NeedOrVolunteer nov : entry.getValue() ) {
				if ( nov instanceof ServiceData.Need ) needs_warning = true;
			}

			if ( needs_warning ) writer.print( "<td class=\"warning\">" );
			else writer.print( "<td>" );

			boolean first = true;
			for( ServiceData.NeedOrVolunteer nov : entry.getValue() ) {
				if ( first ) first = false;
				else writer.print( "<br>" );

				writer.print( nov );
			}
			writer.println( "</td></tr>" );
		}

		writer.println( "        </table>" );
		writer.println( "      </div>" );
	}


	private static String createLoadingPage() {
		return "<!DOCTYPE html>\n" +
			"<html>\n" +
			"\t<head>\n" +
			"\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/>\n" +
			"\t\t<meta http-equiv=\"refresh\" content=\"2\">\n" +
			"\t</head>\n" +
			"\n" +
			"\t<body>\n" +
			"\t\t<div class=\"container-fluid\">\n" +
			"\t\t\t<!-- <div class=\"page-header\">\n" +
			"\t\t\t\t<h1>PCO Kiosk</h1>\n" +
			"\t\t\t</div> -->\n" +
			"\t\t\t<h1>PCO Kiosk</h1>\n" +
			"\t\t\t<h2><small>Loading...</small></h2>\n" +
			"\t\t</div>\n" +
			"\t</body>\n" +
			"</html>";
	}


	private static int determineGridColumns( int num_services ) {
		switch( num_services ) {
			case 1:
				return 12;
			case 2:
				return 6;
			case 3:
				return 4;
			case 4:
				return 3;
			case 5:
			case 6:
				return 2;
			default:
				return 1;
		}
	}
}
