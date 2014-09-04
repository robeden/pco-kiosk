package net.stratfordpark.pco;

import org.scribe.model.Token;

/**
 *
 */
public class PlanningCenterOnlineApi extends org.scribe.builder.api.DefaultApi10a {
	private static final String BASE = "https://planningcenteronline.com/oauth/";


	@Override
	public String getRequestTokenEndpoint() {
		return BASE + "request_token";
	}

	@Override
	public String getAccessTokenEndpoint() {
		return BASE + "access_token";
	}

	@Override
	public String getAuthorizationUrl( Token token ) {
		return BASE + "authorize";
	}
}
