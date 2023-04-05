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
import java.util.function.BiConsumer;

import static java.util.Collections.emptySortedMap;


@Controller()
public class RootController implements AutoCloseable {
	private final DateFormat WEEK_DATE_FORMAT = new SimpleDateFormat( "MMMM d" );
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
		boolean inside_service_times = data == null || insideService( data.getThisWeekServices(), time );
		boolean inverted = !inside_service_times && INVERT_COLORS.get();

		StringWriter string_writer = new StringWriter();
		PrintWriter writer = new PrintWriter( string_writer );

		writer.println( """
            <!DOCTYPE html>
            <html>
                <head>
                    <title>Volunteer Schedules</title>
                    <link rel="stylesheet" type="text/css" href="style.css"/>
                    <meta http-equiv="refresh" content="%d"/>
                </head>
            """.formatted( data == null ? 2 : 120 ));

		if ( inverted ) {
			writer.println( "  <body class=\"inverted\">" );
		}
		else writer.println( "  <body>" );

		writer.println( "<div class=\"header\"><h1>Volunteer Schedules</h1></div>");

		if ( data == null ) {
			writer.println("<div class=\"loading\">Loading...</div>");
		}
		else {
			LinkedHashMap<String,ServiceData> this_week = data.getThisWeekServices();
			LinkedHashMap<String,ServiceData> next_week = data.getNextWeekServices();
			LinkedHashMap<String,ServiceData> two_week = data.getTwoWeeksServices();

			writer.println( "<div class=\"main-schedules\"><table><thead><tr class=\"weeks\">" );
			BiConsumer<String,Date> header_printer = ( words, date ) -> {
				writer.print( "<th colspan=\"2\"><span class=\"words\">" );
				writer.print( words );
				writer.print( "</span><span class=\"date\">" );
				writer.print( WEEK_DATE_FORMAT.format( date ) );
				writer.println( "</span></th>" );
			};
			header_printer.accept( "This Week", data.getThisWeekDate() );
			header_printer.accept( "Next Week", data.getNextWeekDate() );
			header_printer.accept( "Two Weeks", data.getTwoWeeksDate() );
			writer.println( "</tr></thead><tbody>" );

			List<String> service_names =
				pickServiceNames( this_week.keySet(), next_week.keySet(), two_week.keySet() );
			for ( String service_name : service_names ) {
				ServiceData this_sd = this_week.get( service_name );
				if ( this_sd == null ) continue;

				String this_formatted_start_date = TIME_DATE_FORMAT.format( this_sd.getStartDate() );
				ServiceData next_sd = next_week.get( service_name );
				String next_formatted_start_date = next_sd ==
					null ? this_formatted_start_date : TIME_DATE_FORMAT.format( next_sd.getStartDate() );
				ServiceData two_sd = two_week.get( service_name );
				String two_formatted_start_date =
					two_sd == null ? next_formatted_start_date :
						TIME_DATE_FORMAT.format( two_sd.getStartDate() );

				writer.println( "<tr class=\"service-header\">" );
				if ( this_formatted_start_date.equals( next_formatted_start_date ) ) {
					if ( next_formatted_start_date.equals( two_formatted_start_date ) ) {
						// All the same
						printServiceHeader( service_name, this_formatted_start_date, 6, writer );
					}
					else {
						// Only first two are the same
						printServiceHeader( service_name, this_formatted_start_date, 4, writer );
						printServiceHeader( service_name, two_formatted_start_date, 2, writer );
					}
				}
				else if ( next_formatted_start_date.equals( two_formatted_start_date ) ) {
					// Only last two are the same
					printServiceHeader( service_name, this_formatted_start_date, 2, writer );
					printServiceHeader( service_name, two_formatted_start_date, 4, writer );
				}
				else {
					// All different?!? Okay...
					printServiceHeader( service_name, this_formatted_start_date, 2, writer );
					printServiceHeader( service_name, next_formatted_start_date, 2, writer );
					printServiceHeader( service_name, two_formatted_start_date, 2, writer );
				}
				writer.println( "</tr>" );

				var this_vol_list = toVolunteerList( this_sd.getVolunteerMap() );
				var next_vol_list =
					toVolunteerList( next_sd == null ? emptySortedMap() : next_sd.getVolunteerMap() );
				var two_vol_list =
					toVolunteerList( two_sd == null ? emptySortedMap() : two_sd.getVolunteerMap() );
				int num_slots = Math.max( Math.max( this_vol_list.size(), next_vol_list.size() ),
					two_vol_list.size() );
				for ( int i = 0; i < num_slots; i++ ) {
					writer.print( "<tr>" );
					printVolCells( this_vol_list, i, writer );
					printVolCells( next_vol_list, i, writer );
					printVolCells( two_vol_list, i, writer );
					writer.println( "</tr>" );
				}
			}

			writer.println( "</tbody></table></div>" );
		}
		if ( inverted ) {
			writer.println( "<div class=\"footer\"><img src=\"logo-inverted.svg\"></div>" );
		}
		else {
			writer.println( "<div class=\"footer\"><img src=\"logo.svg\"></div>" );
		}

		writer.println( "  </body>" );
		writer.println( "</html>" );

		return string_writer.toString();
	}

	private static void printServiceHeader( String service_name, String formatted_time, int colspan,
		PrintWriter writer ) {

		writer.print("<td class=\"service-title\" colspan=\"");
		writer.print(colspan);
		writer.print("\"><div><span class=\"service-name\">");
		writer.print(service_name);
		writer.print("</span><span class=\"service-time\">");
		writer.print( formatted_time );
		writer.print("</div></td>");
	}


	private static void printVolCells( List<Map.Entry<String,List<ServiceData.NeedOrVolunteer>>> list,
		int index, PrintWriter writer ) {

		if ( index >= list.size() ) {
			writer.print("<td class=\"position\"></td><td class=\"name\"></td>");
			return;
		}

		Map.Entry<String,List<ServiceData.NeedOrVolunteer>> entry = list.get(index);
		List<ServiceData.Volunteer> vol_list = entry.getValue().stream()
			.filter( nov -> nov instanceof ServiceData.Volunteer )
			.map( nov -> ( ServiceData.Volunteer ) nov )
			.toList();

		String fill_class = vol_list.isEmpty() ? "unfilled" : "filled";
		writer.print( "<td class=\"position " + fill_class + "\">" );
		writer.print(entry.getKey());
		writer.print("</td><td class=\"name " + fill_class + "\">");
		writer.print(textForVolunteerList( vol_list ));
		writer.print("</td>");
	}

	private static String textForVolunteerList(List<ServiceData.Volunteer> list) {
		String str = String.join( "<br>", list.stream().map( Object::toString ).toList() );
		if ( str.isEmpty() ) {
			return "-";
		}
		else return str;
	}



	private List<String> pickServiceNames( Set<String> one, Set<String> two, Set<String> three ) {
		LinkedHashSet<String> name_set = new LinkedHashSet<>();
		if ( one.size() >= three.size() ) {
			name_set.addAll( one );
		}
		else name_set.addAll( three );

		name_set.addAll( one );
		name_set.addAll( two );
		name_set.addAll( three );
		return List.copyOf( name_set );
	}


	private static List<Map.Entry<String,List<ServiceData.NeedOrVolunteer>>> toVolunteerList(
		SortedMap<String,List<ServiceData.NeedOrVolunteer>> map ) {

		return new ArrayList<>( map.entrySet() );
	}


	/**
	 * Returns true if the current time is within the given service time, taking
	 * the IN_SERVICE_TIME_BUFFER into account.
	 */
	private boolean insideService( LinkedHashMap<String,ServiceData> this_week_services,
		long current_time ) {

		for( ServiceData service : this_week_services.values() ) {
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
