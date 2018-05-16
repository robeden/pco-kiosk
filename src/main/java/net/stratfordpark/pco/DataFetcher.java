package net.stratfordpark.pco;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import net.stratfordpark.pco.api.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;


/**
 * Encapsulates all the logic of fetching and organizing data.
 */
class DataFetcher {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat( "MMMM d, yyyy" );
	private static final Comparator<ServiceData> SERVICE_COMPARATOR =
		new ServiceDataComparator();


	private final OkHttpClient ok_client;
	private final Moshi moshi;

//	private final OAuthService service;
//	private final Token access_token;
//
//	private final Gson gson;

	private final Calendar calendar = Calendar.getInstance();



	private final JsonAdapter<List<ServiceType>> service_types_adapter;
	private final JsonAdapter<List<Plan>> plans_adapter;
	private final JsonAdapter<List<PlanTime>> plan_times_adapter;
	private final JsonAdapter<List<PlanPerson>> plan_person_adapter;
	private final JsonAdapter<List<NeededPosition>> needed_position_adapter;



	DataFetcher( OkHttpClient ok_client, Moshi moshi ) {
		this.ok_client = requireNonNull( ok_client );
		this.moshi = requireNonNull( moshi );


		service_types_adapter = moshi.adapter(
			Types.newParameterizedType( List.class, ServiceType.class ),
			EnvelopeJsonAdapter.Enveloped.class );

		plans_adapter = moshi.adapter(
			Types.newParameterizedType( List.class, Plan.class ),
			EnvelopeJsonAdapter.Enveloped.class );

		plan_times_adapter = moshi.adapter(
			Types.newParameterizedType( List.class, PlanTime.class ),
			EnvelopeJsonAdapter.Enveloped.class );

		plan_person_adapter = moshi.adapter(
			Types.newParameterizedType( List.class, PlanPerson.class ),
			EnvelopeJsonAdapter.Enveloped.class );

		needed_position_adapter = moshi.adapter(
			Types.newParameterizedType( List.class, NeededPosition.class ),
			EnvelopeJsonAdapter.Enveloped.class );
	}



	Data fetchData() throws Exception {
		long fetch_start = System.currentTimeMillis();

		List<ServiceType> service_types =
			fetch( "https://api.planningcenteronline.com/services/v2/service_types",
			service_types_adapter );

		long fetch_duration = System.currentTimeMillis() - fetch_start;
		System.out.println( "Fetch done in " + fetch_duration );



		Date this_sunday = computeSundayDate( 0 );
		String this_sunday_string = DATE_FORMAT.format( this_sunday );

		Date next_sunday = computeSundayDate( 1 );
		String next_sunday_string = DATE_FORMAT.format( next_sunday );

		Date two_weeks_sunday = computeSundayDate( 2 );
		String two_weeks_sunday_string = DATE_FORMAT.format( two_weeks_sunday );

		List<ServiceData> this_week_services = new ArrayList<>();
		List<ServiceData> next_week_services = new ArrayList<>();
		List<ServiceData> two_weeks_services = new ArrayList<>();

		for( ServiceType service_type : service_types ) {
			List<Plan> plans = fetch(
				"https://api.planningcenteronline.com/services/v2/service_types/" +
				service_type.getId() + "/plans?filter=future&order=sort_date&per_page=3",
				plans_adapter );

			for( Plan plan : plans ) {
				Date matching_sunday = null;
				List<ServiceData> service_list = null;

				if ( this_sunday_string.equals( plan.getDates() ) ) {
					matching_sunday = this_sunday;
					service_list = this_week_services;
				}
				else if ( next_sunday_string.equals( plan.getDates() ) ) {
					matching_sunday = next_sunday;
					service_list = next_week_services;
				}
				else if ( two_weeks_sunday_string.equals( plan.getDates() ) ) {
					matching_sunday = two_weeks_sunday;
					service_list = two_weeks_services;
				}

				if ( matching_sunday == null ) continue;

				String series_title = plan.getSeriesTitle();
				String plan_title = plan.getPlanTitle();


				List<PlanTime> times = fetch(
					"https://api.planningcenteronline.com/services/v2/service_types/" +
					service_type.getId() + "/plans/" + plan.getId() + "/plan_times",
					plan_times_adapter );

				Date start_time;
//				if ( plan.getServiceTimes() != null &&
//					!plan.getServiceTimes().isEmpty() ) {
//
//					start_time = plan.getServiceTimes().get( 0 ).getStartsAt();
//				}
				/*else*/ start_time = matching_sunday;


				SortedMap<String, List<ServiceData.NeedOrVolunteer>> volunteer_map =
					new TreeMap<>();


				// People
				List<PlanPerson> people = fetch(
					"https://api.planningcenteronline.com/services/v2/service_types/" +
					service_type.getId() + "/plans/" + plan.getId() + "/team_members",
					plan_person_adapter );
				for( PlanPerson person : people ) {
					// Only want confirmed or unconfirmed (not declined)
					if ( !person.getStatus().startsWith( "C" ) &&
						!person.getStatus().startsWith( "U" ) ) continue;

					List<ServiceData.NeedOrVolunteer> list = volunteer_map
						.computeIfAbsent( person.getPosition(), p -> new LinkedList<>() );
					list.add( new ServiceData.Volunteer( person.getPersonName() ) );
				}

				// Open positions
				List<NeededPosition> needed = fetch(
					"https://api.planningcenteronline.com/services/v2/service_types/" +
					service_type.getId() + "/plans/" + plan.getId() + "/needed_positions",
					needed_position_adapter );
				for ( NeededPosition position : needed ) {
					List<ServiceData.NeedOrVolunteer> list =
						volunteer_map.get( position.getPositionName() );
					if ( list == null ) {
						list = new LinkedList<>();
						volunteer_map.put( position.getPositionName(), list );
					}

					list.add( new ServiceData.Need( position.getQuantity() ) );
				}

				List<PlanTime> service_times = times.stream()
					.filter( time -> time.getType().equals( "service" ) )
					.collect( Collectors.toList() );

				long[] start_times = new long[ service_times.size() ];
				long[] end_times = new long[ service_times.size() ];
				for( int i = 0; i < service_times.size(); i++ ) {
					PlanTime service_time = service_times.get( i );

					if ( i == 0 ) start_time = service_time.getStartsAt();

					start_times[ i ] = service_time.getStartsAt().getTime();
					end_times[ i ] = service_time.getStartsAt().getTime();
				}


				ServiceData service_data = new ServiceData( service_type.getName(),
					start_time, volunteer_map, plan_title, series_title,
					start_times, end_times );

				service_list.add( service_data );
			}
		}

		this_week_services.sort( SERVICE_COMPARATOR );
		next_week_services.sort( SERVICE_COMPARATOR );
		two_weeks_services.sort( SERVICE_COMPARATOR );


		return new Data( fetch_duration, "",//org.getName(),        // TODO
			this_sunday, this_week_services,
			next_sunday, next_week_services,
			two_weeks_sunday, two_weeks_services );
	}


	/**
	 * Return the set of date string for the desired week (this or next) in the form
	 * to match the format of {@link net.stratfordpark.pco.api.Plan#dates}
	 */
	private Date computeSundayDate( int weeks_away ) {
		calendar.setTimeInMillis( System.currentTimeMillis() );

		while( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY ) {
			calendar.add( Calendar.DAY_OF_WEEK, 1 );
		}

		if ( weeks_away != 0 ) {
			calendar.add( Calendar.WEEK_OF_YEAR, weeks_away );
		}

		return calendar.getTime();
	}



	private <T> T fetch( String url, JsonAdapter<T> adapter ) throws IOException {
		Request request = new Request.Builder()
			.url( url )
			.build();

		try ( Response response = ok_client.newCall( request ).execute() ) {
			if ( !response.isSuccessful() ) {
				throw new IOException( "Unexpected code " + response );
			}
			return adapter.fromJson( response.body().string() );
		}
	}
}
