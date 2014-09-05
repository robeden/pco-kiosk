package net.stratfordpark.pco;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.stratfordpark.pco.api.Organization;
import net.stratfordpark.pco.api.Plan;
import net.stratfordpark.pco.api.PlanPerson;
import net.stratfordpark.pco.api.ServiceType;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Encapsulates all the logic of fetching and organizing data.
 */
class DataFetcher {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat( "MMMM d, yyyy" );
	private static final Comparator<ServiceData> SERVICE_COMPARATOR =
		new ServiceDataComparator();


	private final OAuthService service;
	private final Token access_token;

	private final Gson gson;

	private final Calendar calendar = Calendar.getInstance();


	DataFetcher( OAuthService service, Token access_token ) {
		this.service = service;
		this.access_token = access_token;


		gson = new GsonBuilder()
			.setDateFormat( "yyyy/MM/dd HH:mm:ss Z" )      // "2012/12/28 18:00:00 -0800"
			.create();
	}



	Data fetchData() {
		try {
			Organization org =
				fetch( "https://www.planningcenteronline.com/organization.json",
				Organization.class );

			Date this_sunday = computeSundayDate( true );
			String this_sunday_string = DATE_FORMAT.format( this_sunday );

			Date next_sunday = computeSundayDate( false );
			String next_sunday_string = DATE_FORMAT.format( next_sunday );

			List<ServiceData> this_week_services = new ArrayList<>();
			List<ServiceData> next_week_services = new ArrayList<>();

			for( ServiceType service_type : org.getServiceTypes() ) {
				Type collection_type = new TypeToken<List<Plan>>(){}.getType();
				List<Plan> plans =
					fetch( "https://www.planningcenteronline.com/service_types/" +
					service_type.getId() + "/plans.json", collection_type );

				for( Plan plan : plans ) {
					boolean matches_this_week = this_sunday_string.equals( plan.getDates() );
					boolean matches_next_week = next_sunday_string.equals( plan.getDates() );

					if ( !matches_this_week && !matches_next_week ) continue;

					// We get more information when making a specific request for the plan
					plan = fetch( "https://www.planningcenteronline.com/plans/" +
						plan.getId() + ".json", Plan.class );

					Date start_time;
					if ( plan.getServiceTimes() != null &&
						!plan.getServiceTimes().isEmpty() ) {

						start_time = plan.getServiceTimes().get( 0 ).getStartsAt();
					}
					else if ( matches_this_week ) start_time = this_sunday;
					else start_time = next_sunday;


					SortedMap<String, List<ServiceData.NeedOrVolunteer>> volunteer_map =
						new TreeMap<>();


					for( PlanPerson person : plan.getPlanPeople() ) {
						// Only want confirmed or unconfirmed (not declined)
						if ( !person.getStatus().startsWith( "C" ) &&
							!person.getStatus().startsWith( "U" ) ) continue;

						List<ServiceData.NeedOrVolunteer> list =
							volunteer_map.get( person.getPosition() );
						if ( list == null ) {
							list = new LinkedList<>();
							volunteer_map.put( person.getPosition(), list );
						}

						list.add( new ServiceData.Volunteer( person.getPersonName() ) );
					}


					ServiceData service_data = new ServiceData( service_type.getName(),
						start_time, volunteer_map );

					if ( matches_this_week ) this_week_services.add( service_data );
					else next_week_services.add( service_data );
				}
			}

			Collections.sort( this_week_services, SERVICE_COMPARATOR );
			Collections.sort( next_week_services, SERVICE_COMPARATOR );


			return new Data( org.getName(), this_sunday, this_week_services,
				next_sunday, next_week_services );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return null;
		}
	}


	/**
	 * Return the set of date string for the desired week (this or next) in the form
	 * to match the format of {@link net.stratfordpark.pco.api.Plan#dates}
	 */
	private Date computeSundayDate( boolean this_week ) {
		calendar.setTimeInMillis( System.currentTimeMillis() );

		while( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY ) {
			calendar.add( Calendar.DAY_OF_WEEK, 1 );
		}

		if ( !this_week ) {
			calendar.add( Calendar.WEEK_OF_YEAR, 1 );
		}

		return calendar.getTime();
	}





	private <T> T fetch( String url, Class<T> type ) throws IOException {
		OAuthRequest request = new OAuthRequest( Verb.GET, url );
		service.signRequest( access_token, request );

		Response response = request.send();
		if ( response.getCode() != 200 ) throw new IOException( response.getMessage() );

		try {
			return gson.fromJson( response.getBody(), type );
		}
		catch( Exception ex ) {
			System.err.println( "Error parsing " + type + " from: " + response.getBody() );
			ex.printStackTrace();
			throw new IOException( ex );
		}
	}



	private <T> T fetch( String url, Type type ) throws IOException {
		OAuthRequest request = new OAuthRequest( Verb.GET, url );
		service.signRequest( access_token, request );

		Response response = request.send();
		if ( response.getCode() != 200 ) throw new IOException( response.getMessage() );

		try {
			return gson.fromJson( response.getBody(), type );
		}
		catch( Exception ex ) {
			System.err.println( "Error parsing " + type + " from: " + response.getBody() );
			ex.printStackTrace();
			throw new IOException( ex );
		}
	}
}
