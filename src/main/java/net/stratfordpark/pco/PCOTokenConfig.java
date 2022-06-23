package net.stratfordpark.pco;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;


@ConfigurationProperties( "pco.tokens" )
public class PCOTokenConfig {
	@NotBlank
	private String key;

	@NotBlank
	private String secret;


	public String getKey() {
		return key;
	}


	public void setKey( String key ) {
		this.key = key;
	}


	public String getSecret() {
		return secret;
	}


	public void setSecret( String secret ) {
		this.secret = secret;
	}
}
