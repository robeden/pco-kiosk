package net.stratfordpark.pco;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


@Controller()
public class RootController implements AutoCloseable {
	private final DateFormat WEEK_DATE_FORMAT = new SimpleDateFormat( "MMM d" );
	private final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat( "h:mm" );

	private final long IN_SERVICE_TIME_BUFFER = TimeUnit.MINUTES.toMillis( 30 );

	private final AtomicBoolean INVERT_COLORS = new AtomicBoolean( false );


	private final long update_period = TimeUnit.MINUTES.toMillis( 15 );

	private final AtomicReference<Data> data_slot = new AtomicReference<>();
	private final AtomicReference<String> fetch_status = new AtomicReference<>( "" );
	private final AtomicLong last_fetch = new AtomicLong( 0 );
	private final AtomicLong last_invert = new AtomicLong( 0 );


	private final Timer timer = new Timer( "Fetch timer" );
	private final OkHttpClient http_client;
	private final Moshi moshi;

	public RootController( PCOTokenConfig tokens ) {
		http_client = new OkHttpClient.Builder()
			.authenticator( ( route, response ) -> {

				if ( response.request().header( "Authorization" ) != null ) {
					return null; // Give up, we've already attempted to authenticate.
				}

				String credential = Credentials.basic( tokens.getKey(), tokens.getSecret() );
				return response.request().newBuilder()
					.header( "Authorization", credential )
					.build();
			} )
			.build();

		moshi = new Moshi.Builder()
			.add( EnvelopeJsonAdapter.FACTORY )
			.add( Date.class, new Rfc3339DateJsonAdapter() )
			.build();
	}

	@PostConstruct
	public void initialize() {
		final DataFetcher fetcher = new DataFetcher( http_client, moshi );
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
					fetch_status.set( "Error: " + ex );
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

	@PreDestroy
	@Override
	public void close() {
		timer.cancel();
	}


	@Get(produces = MediaType.TEXT_HTML)
    public String index() {

		Data data = data_slot.get();
		if ( data == null ) {
			return createLoadingPage();
		}

//		System.out.println( "Data: " + data );

		try {
			return buildMainPage( data );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			throw ex;
		}
    }



	private String buildMainPage( Data data ) {
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
	private List<String> pickServiceNames( List<ServiceData>... data_lists ) {
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
				if ( nov instanceof ServiceData.Need ) {
					needs_warning = true;
					break;
				}
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
		return """
			<!DOCTYPE html>
			<html>
			\t<head>
			\t\t<link rel="stylesheet" type="text/css" href="bootstrap.min.css"/>
			\t\t<meta http-equiv="refresh" content="2">
			\t</head>

			\t<body>
			\t\t<div class="container-fluid">
			\t\t\t<!-- <div class="page-header">
			\t\t\t\t<h1>PCO Kiosk</h1>
			\t\t\t</div> -->
			\t\t\t<h1>PCO Kiosk</h1>
			\t\t\t<h2><small>Loading...</small></h2>
			\t\t</div>
			\t</body>
			</html>""";
	}


	/**
	 * Returns true if the current time is within the given service time, taking
	 * the IN_SERVICE_TIME_BUFFER into account.
	 */
	private boolean insideService( List<ServiceData> this_week_services,
		long current_time ) {

		for( ServiceData service : this_week_services ) {

			long[] start_times = service.getStartTimes();
			long[] end_times = service.getEndTimes();

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
