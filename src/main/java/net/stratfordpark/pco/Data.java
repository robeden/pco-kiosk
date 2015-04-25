package net.stratfordpark.pco;

import java.util.Date;
import java.util.List;

/**
 *
 */
class Data {
	private final long fetch_duration;

	private final String org_name;

	private final Date this_week_date;
	private final List<ServiceData> this_week_services;

	private final Date next_week_date;
	private final List<ServiceData> next_week_services;

	private final Date two_weeks_date;
	private final List<ServiceData> two_weeks_services;

	Data( long fetch_duration, String org_name,
		Date this_week_date, List<ServiceData> this_week_services,
		Date next_week_date, List<ServiceData> next_week_services,
		Date two_weeks_date, List<ServiceData> two_weeks_services ) {

		this.fetch_duration = fetch_duration;
		this.org_name = org_name;
		this.this_week_date = this_week_date;
		this.this_week_services = this_week_services;
		this.next_week_date = next_week_date;
		this.next_week_services = next_week_services;
		this.two_weeks_date = two_weeks_date;
		this.two_weeks_services = two_weeks_services;
	}


	public String getOrgName() {
		return org_name;
	}

	public Date getThisWeekDate() {
		return this_week_date;
	}

	public List<ServiceData> getThisWeekServices() {
		return this_week_services;
	}

	public Date getNextWeekDate() {
		return next_week_date;
	}

	public List<ServiceData> getNextWeekServices() {
		return next_week_services;
	}

	public Date getTwoWeeksDate() {
		return two_weeks_date;
	}

	public List<ServiceData> getTwoWeeksServices() {
		return two_weeks_services;
	}

	public long getFetchDuration() {
		return fetch_duration;
	}
}
