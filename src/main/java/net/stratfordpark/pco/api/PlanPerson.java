package net.stratfordpark.pco.api;

import com.squareup.moshi.Json;


/**
 *
 */
public class PlanPerson {
	private final long id;
	private final Attributes attributes;


	public PlanPerson( long id, Attributes attributes ) {
		this.id = id;
		this.attributes = attributes;
	}

	public long getId() {
		return id;
	}

	public String getPosition() {
		return attributes.position;
	}

	public String getStatus() {
		return attributes.status;
	}

	public String getPersonName() {
		return attributes.name;
	}

	private static class Attributes {
		private String name;
		private String status;
		@Json( name = "team_position_name" )
		private String position;
	}
}
