package net.stratfordpark.pco.api;

/**
 *
 */
public class Plan {
	private final long id;
	private final Attributes attributes;
	private final Links links;

	public Plan( long id, Attributes attributes, Links links ) {

		this.id = id;
		this.attributes = attributes;
		this.links = links;
	}


	public long getId() {
		return id;
	}

	public String getPlanTitle() {
		return attributes.title;
	}

	public String getSeriesTitle() {
		return attributes.series_title;
	}

	public String getDates() {
		return attributes.dates;
	}


	public String getNeededPositionsLink() {
		return links.needed_positions;
	}

	public String getPlanTimesLink() {
		return links.plan_times;
	}

	public String getTeamMembersLink() {
		return links.team_members;
	}



	private static class Attributes {
		private String title;
		private String series_title;
		private String dates;
	}

	private static class Links {
		private String needed_positions;
		private String plan_times;
		private String team_members;
	}
}
