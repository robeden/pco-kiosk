package net.stratfordpark.pco;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;
import freemarker.template.Configuration;
import freemarker.template.Version;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;


public class Main {
	private static final DateFormat WEEK_DATE_FORMAT = new SimpleDateFormat( "MMM d" );
	private static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat( "h:mm" );

	private static final long IN_SERVICE_TIME_BUFFER = TimeUnit.MINUTES.toMillis( 30 );

	private static final AtomicBoolean INVERT_COLORS = new AtomicBoolean( false );


	public static void main(String[] args) {
		if ( args.length < 2 ) {
			System.out.println(
				"Usage: <api_key> <api_secret>" );
			System.exit( -1 );
			return;
		}


		// TODO: real CLI parser. Will also want: refresh time, etc.
		String api_key = args[ 0 ];
		String api_secret = args[ 1 ];
		final long update_period = TimeUnit.MINUTES.toMillis( 15 );

		final AtomicReference<Data> data_slot = new AtomicReference<>();
		final AtomicReference<String> fetch_status = new AtomicReference<>( "" );
		final AtomicLong last_fetch = new AtomicLong( 0 );
		final AtomicLong last_invert = new AtomicLong( 0 );

		staticFiles.location( "/css" );
//		Spark.staticFileLocation( "/css" );
		port( 8888 );


		OkHttpClient http_client = new OkHttpClient.Builder()
			.authenticator( ( route, response ) -> {

				if ( response.request().header( "Authorization" ) != null ) {
					return null; // Give up, we've already attempted to authenticate.
				}

				String credential = Credentials.basic( api_key, api_secret );
				return response.request().newBuilder()
					.header( "Authorization", credential )
					.build();
			} )
			.build();


		Moshi moshi = new Moshi.Builder()
			.add( EnvelopeJsonAdapter.FACTORY )
			.add( Date.class, new Rfc3339DateJsonAdapter() )
			.build();


		final DataFetcher fetcher = new DataFetcher( http_client, moshi );

		boolean inline_fetch = System.getProperty( "inline_fetch" ) != null;
		if ( inline_fetch ) {
			System.out.println( "*** INLINE FETCH ENABLED ***" );
		}

		get( "/", ( request, response ) -> {
			Data data;
			if ( inline_fetch ) {
				data = fetcher.fetchData();
			}
			else {
				data = data_slot.get();
				if ( data == null ) {
					return createLoadingPage();
				}
			}

//			System.out.println( "Data: " + data );

			try {
				return buildMainPage( data );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
				throw ex;
			}
		} );

		Configuration freemarker_config = new Configuration( new Version( 2, 3, 23 ) );
		freemarker_config.setClassForTemplateLoading( Main.class, "" );
		get( "/new", ( req, res ) -> {
			Data data;
			if ( inline_fetch ) {
				data = fetcher.fetchData();
			}
			else {
				data = data_slot.get();
//				if ( data == null ) {
//					return createLoadingPage();
//				}
			}

            Map<String, Object> attributes = new HashMap<>();
			attributes.put( "data", data );

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView( attributes, "main.ftl" );
		}, new FreeMarkerEngine( freemarker_config ) );

		get( "/debug", ( request, response ) -> {
			response.type( "text/plain" );

			StringBuilder buf = new StringBuilder();
			buf.append( "Fetch status: " ).append( fetch_status.get() );
			buf.append( "\nNext fetch: " ).append(
				TimeUnit.MILLISECONDS.toSeconds(
					( last_fetch.get() + update_period ) -
						System.currentTimeMillis() ) )
				.append( " s" );
			buf.append( "\nInverted: " ).append( INVERT_COLORS.get() ).append(
				" (switch in " ).append(
				TimeUnit.MILLISECONDS.toSeconds( last_invert.get() +
					TimeUnit.MINUTES.toMillis( 10 ) ) ).append( " s)" );

			Data data = data_slot.get();
			if ( data != null ) {
				long time = System.currentTimeMillis();
				boolean inside_service_times =
					insideService( data.getThisWeekServices(), time );

				buf.append( "\nInside service times: " ).append( inside_service_times );

				buf.append( "\nLast fetch duration: " )
					.append( data.getFetchDuration() )
					.append( " ms" );

				buf.append( "\nData:\n" );
//				buf.append( gson.toJson( data ) );
			}


			return buf.toString();
		} );



//		OAuthService service = new ServiceBuilder()
//			.provider( PlanningCenterOnlineApi.class )
//			.apiKey( api_key )
//			.apiSecret( api_secret )
//			.build();
//
//		Token access_token = new Token( access_key, access_secret );

		Timer timer = new Timer( "Fetch timer" );
		timer.schedule( new TimerTask() {
			@Override
			public void run() {
				last_fetch.set( System.currentTimeMillis() );

				try {
					fetch_status.set( "Starting fetch..." );
					Data data = fetcher.fetchData();
					fetch_status.set( "Complete. Waiting for next." );
					if ( data != null ) {
						data_slot.set( data );
					}
				} catch ( Exception ex ) {
					ex.printStackTrace();
					fetch_status.set( "Error: " + ex.toString() );
				}
			}
		}, 0, update_period );

		// Color inverter
		timer.schedule( new TimerTask() {
			@Override
			public void run() {
				last_invert.set( System.currentTimeMillis() );

				INVERT_COLORS.set( !INVERT_COLORS.get() );
			}
		}, TimeUnit.MINUTES.toMillis( 10 ), TimeUnit.MINUTES.toMillis( 10 ) );
	}


	static AtomicBoolean invert = new AtomicBoolean( true );
	private static String buildMainPage( Data data ) {
		long time = System.currentTimeMillis();
		boolean inside_service_times = insideService( data.getThisWeekServices(), time );


		StringWriter string_writer = new StringWriter();
		PrintWriter writer = new PrintWriter( string_writer );

		writer.println( "<!DOCTYPE html>" );
		writer.println( "<html>" );
		writer.println( "  <head>" );
		writer.println( "    <title>Volunteer Schedules</title>" );
		writer.println( "    <link rel=\"stylesheet\" type=\"text/css\" " +
			"href=\"style.css\"/>" );
		writer.println( "    <link rel=\"stylesheet\" type=\"text/css\" " +
			"href=\"bootstrap.min.css\"/>" );
		writer.println( "    <meta http-equiv=\"refresh\" content=\"120\">" );
		writer.println( "</head>" );

//		boolean invert = Main.invert.get();
//		Main.invert.set( !invert );
//		if ( invert ) {
		if ( !inside_service_times && INVERT_COLORS.get() ) {
			writer.println( "  <body class=\"inverted\">" );
		}
		else writer.println( "  <body>" );


		writer.println( "    <div class=\"container-fluid\">" );
		writer.println( "    <div class=\"page-header\">" );
		writer.println( "      <h1>Volunteer Schedules<small>" + data.getOrgName() +
			"</small></h1>" );
		writer.println( "    </div>" );


		final int columns = 4;

		List<ServiceData> this_week = data.getThisWeekServices();
		List<ServiceData> next_week = data.getNextWeekServices();
		List<ServiceData> two_week = data.getTwoWeeksServices();

		// TODO: this is assuming the same services are used every week. It won't explode
		//       if there's a different number, but things might not line up correctly


//<div id="textbox">
//<p class="alignleft">1/10</p>
//<!--<p class="aligncenter">02:27</p>-->
//<p class="alignright">100%</p>
//</div>
//<div style="clear: both;"></div>

		writer.println( "    <div class=\"row\">" );
		writer.println( "      <div class=\"col-md-" + columns + " week-column leftrightbox\">" );
		writer.println( "       <div><h2>This Week</h2></div><div><h2><small>" +
			WEEK_DATE_FORMAT.format( data.getThisWeekDate() ) + "</small></h2></div>" );
		writer.println( "      </div>" );
		writer.println( "      <div class=\"col-md-" + columns + " week-column leftrightbox\">" );
		writer.println( "      <div><h2>Next Week</h2></div><div><h2><small>" +
			WEEK_DATE_FORMAT.format( data.getNextWeekDate() ) + "</small></h2></div>" );
		writer.println( "      </div>" );
		writer.println( "      <div class=\"col-md-" + columns + " week-column leftrightbox\">" );
		writer.println( "      <div><h2>Two Weeks</h2></div><div><h2><small>" +
			WEEK_DATE_FORMAT.format( data.getTwoWeeksDate() ) + "</small></h2></div>" );
		writer.println( "      </div>" );
		writer.println( "    </div>" );

		writer.println( "<div></div>" );

		List<String> service_names = pickServiceNames( this_week, next_week, two_week );
		for( String service_name : service_names ) {
			writer.println( "    <div class=\"row\">" );

			// This week
			writer.println( "      <div class=\"col-md-" + columns + " week-column leftrightbox\">" );
			ServiceData service_data = findDataForName( this_week, service_name );
			if ( service_data != null ) {
				writer.println( "   <div><h3>" + service_data.getName() + "</h3></div><div><h3><small>" +
					TIME_DATE_FORMAT.format( service_data.getStartDate() ) + "</small></h3></div>" );
			}
			writer.println( "      </div>" );

			// Next week
			writer.println( "      <div class=\"col-md-" + columns + " week-column leftrightbox\">" );
			service_data = findDataForName( next_week, service_name );
			if ( service_data != null ) {
				writer.println( "   <div><h3>" + service_data.getName() + "</h3></div><div><h3><small>" +
					TIME_DATE_FORMAT.format( service_data.getStartDate() ) + "</small></h3></div>" );
			}
			writer.println( "      </div>" );

			// Two weeks
			writer.println( "      <div class=\"col-md-" + columns + " week-column leftrightbox\">" );
			service_data = findDataForName( two_week, service_name );
			if ( service_data != null ) {
				writer.println( "   <div><h3>" + service_data.getName() + "</h3></div><div><h3><small>" +
					TIME_DATE_FORMAT.format( service_data.getStartDate() ) + "</small></h3></div>" );
			}
			writer.println( "      </div>" );

			writer.println( "    </div>" );     // /row


			writer.println( "    <div class=\"row\">" );

			// This week
			writer.println( "      <div class=\"col-md-" + columns + " week-column\">" );
			service_data = findDataForName( this_week, service_name );
			if ( service_data != null ) {
				buildServiceBlock( service_data, columns, writer );
			}
			writer.println( "      </div>" );

			// Next week
			writer.println( "      <div class=\"col-md-" + columns + " week-column\">" );
			service_data = findDataForName( next_week, service_name );
			if ( service_data != null ) {
				buildServiceBlock( service_data, columns, writer );
			}
			writer.println( "      </div>" );

			// Two weeks
			writer.println( "      <div class=\"col-md-" + columns + " week-column\">" );
			service_data = findDataForName( two_week, service_name );
			if ( service_data != null ) {
				buildServiceBlock( service_data, columns, writer );
			}
			writer.println( "      </div>" );

			writer.println( "    </div>" );
		}

		writer.println( "  </body>" );
		writer.println( "</html>" );

		return string_writer.toString();
	}


	private static ServiceData findDataForName( List<ServiceData> data_list, String name ) {
		for( ServiceData data : data_list ) {
			if ( name.equals( data.getName() ) ) return data;
		}

		return null;
	}



	/**
	 * Generates a list of service names based off the longest service data list.
	 * This assumes that are some point a particular service is skipped and allows lining
	 * up the other services. Example: A, B, C -> A, C
	 *
	 * This will have trouble in the case where one type of service is exchanged for
	 * another. Example: A, B, C -> A, D, C
	 */
	private static List<String> pickServiceNames( List<ServiceData>... data_lists ) {
		// Two passes:
		//   1) Pick the longest list
		//   2) Make sure all services are included

		List<String> to_return = Collections.emptyList();

		// TODO: Java 8 could make this much prettier
		for( List<ServiceData> data_list : data_lists ) {
			if ( data_list.size() > to_return.size() ) {
				to_return = new ArrayList<>( data_list.size() );
				for( ServiceData data : data_list ) {
					to_return.add( data.getName() );
				}
			}
		}

		// This is a fail-safe for the bad case to ensure all services are displayed
		for( List<ServiceData> data_list : data_lists ) {
			for( ServiceData data : data_list ) {
				String name = data.getName();
				if ( !to_return.contains( name ) ) {
					to_return.add( name );
				}
			}
		}

		return to_return;
	}


	private static void buildServiceBlock( ServiceData data, int columns,
		PrintWriter writer ) {

//		writer.println( "        <h3 style=\"margin-top: 2em\">" + data.getName() + " <small>" +
//			TIME_DATE_FORMAT.format( data.getStartDate() ) + "</small></h3>" );
//		if ( data.getPlanTitle() != null ) {
//			writer.println( "        <p class=\"lead\">" + data.getPlanTitle() + "</p>" );
//		}
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


	/**
	 * Returns true if the current time is within the given service time, taking
	 * the IN_SERVICE_TIME_BUFFER into account.
	 */
	private static boolean insideService( List<ServiceData> this_week_services,
		long current_time ) {

		for( ServiceData service : this_week_services ) {

			long start_times[] = service.getStartTimes();
			long end_times[] = service.getEndTimes();

			for( int i = 0; i < start_times.length; i++ ) {
				long start_with_buffer = start_times[ i ] - IN_SERVICE_TIME_BUFFER;
				long end_with_buffer = end_times[ i ] + IN_SERVICE_TIME_BUFFER;

				if ( current_time > start_with_buffer && current_time < end_with_buffer ) {
					return true;
				}
			}
		}

		return false;
	}
}
