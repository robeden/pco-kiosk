package net.stratfordpark.pco;

import java.util.Date;
import java.util.List;

/**
 *
 */
class Data {
	private final String org_name;

	private final Date this_week_date;
	private final List<ServiceData> this_week_services;

	private final Date next_week_date;
	private final List<ServiceData> next_week_services;

	Data( String org_name, Date this_week_date,
		List<ServiceData> this_week_services, Date next_week_date,
		List<ServiceData> next_week_services ) {
		this.org_name = org_name;
		this.this_week_date = this_week_date;
		this.this_week_services = this_week_services;
		this.next_week_date = next_week_date;
		this.next_week_services = next_week_services;
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
}
