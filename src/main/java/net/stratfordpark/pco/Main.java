package net.stratfordpark.pco;

import com.google.gson.Gson;
import net.stratfordpark.pco.api.Organization;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import spark.Request;
import spark.Route;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.get;
import static spark.SparkBase.staticFileLocation;


public class Main {
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

		final AtomicReference<Organization> info_slot = new AtomicReference<>();


		staticFileLocation( "/css" );

		get( "/", new Route() {
			@Override
			public Object handle( Request request, spark.Response response ) {
				Organization info = info_slot.get();  // TODO: load full info

				if ( info == null ) {
					return createLoadingPage();
				}


				return "Org: " + info.getName();
			}
		} );



		OAuthService service = new ServiceBuilder()
			.provider( PlanningCenterOnlineApi.class )
			.apiKey( api_key )
			.apiSecret( api_secret )
			.build();

		Token access_token = new Token( access_key, access_secret );

		Gson gson = new Gson();

		Thread.sleep( 5000 );

		Organization org = loadOrganizationInfo( service, access_token, gson );
		info_slot.set( org );

		System.out.println( "Loaded organization: " + org );
	}


	private static Organization loadOrganizationInfo( OAuthService service,
		Token access_token, Gson gson ) throws IOException {

		OAuthRequest request = new OAuthRequest( Verb.GET,
			"https://www.planningcenteronline.com/organization.json" );
		service.signRequest( access_token, request );

		Response response = request.send();

		if ( response.getCode() != 200 ) {
			throw new IOException( "Error loading organization info (status " +
				response.getCode() + "): " + response.getMessage() );
		}

		return gson.fromJson( response.getBody(), Organization.class );
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
}
