package net.stratfordpark.pco.api;

import java.util.Date;


/**
 *
 */
public class PlanTime {
	private final Attributes attributes;



	public PlanTime( Attributes attributes ) {
		this.attributes = attributes;
	}


	public Date getStartsAt() {
		return attributes.starts_at;
	}

	public Date getEndsAt() {
		return attributes.ends_at;
	}

	public String getName() {
		return attributes.name;
	}

	public String getType() {
		return attributes.time_type;
	}




	private static class Attributes {
		private Date starts_at;
		private Date ends_at;
		private String name;
		private String time_type;       // rehearsal, service, other
	}
}
