package net.stratfordpark.pco.api;

/**
 *
 */
public class Series {
	private final long id;
	private final String name;
	private final String artwork_for_plan;
	private final String artwork_for_mobile;
	private final String artwork_for_dashboard;
	private final String artwork;

	public Series( long id, String name, String artwork_for_plan,
		String artwork_for_mobile, String artwork_for_dashboard, String artwork ) {

		this.id = id;
		this.name = name;
		this.artwork_for_plan = artwork_for_plan;
		this.artwork_for_mobile = artwork_for_mobile;
		this.artwork_for_dashboard = artwork_for_dashboard;
		this.artwork = artwork;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getArtworkForPlan() {
		return artwork_for_plan;
	}

	public String getArtworkForMobile() {
		return artwork_for_mobile;
	}

	public String getArtworkForDashboard() {
		return artwork_for_dashboard;
	}

	public String getArtwork() {
		return artwork;
	}
}
