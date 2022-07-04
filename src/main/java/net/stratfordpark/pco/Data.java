package net.stratfordpark.pco;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 */
public class Data {
	private final long fetch_duration;

	private final String org_name;

	private final Date this_week_date;
	private final LinkedHashMap<String,ServiceData> this_week_services;

	private final Date next_week_date;
	private final LinkedHashMap<String,ServiceData> next_week_services;

	private final Date two_weeks_date;
	private final LinkedHashMap<String,ServiceData> two_weeks_services;

	Data( long fetch_duration, String org_name,
		Date this_week_date, List<ServiceData> this_week_services,
		Date next_week_date, List<ServiceData> next_week_services,
		Date two_weeks_date, List<ServiceData> two_weeks_services ) {

		this.fetch_duration = fetch_duration;
		this.org_name = org_name;
		this.this_week_date = this_week_date;
		this.this_week_services = listToMap( this_week_services );
		this.next_week_date = next_week_date;
		this.next_week_services = listToMap( next_week_services );
		this.two_weeks_date = two_weeks_date;
		this.two_weeks_services = listToMap( two_weeks_services );
	}


	public String getOrgName() {
		return org_name;
	}

	public Date getThisWeekDate() {
		return this_week_date;
	}

	public LinkedHashMap<String,ServiceData> getThisWeekServices() {
		return this_week_services;
	}

	public Date getNextWeekDate() {
		return next_week_date;
	}

	public LinkedHashMap<String,ServiceData> getNextWeekServices() {
		return next_week_services;
	}

	public Date getTwoWeeksDate() {
		return two_weeks_date;
	}

	public LinkedHashMap<String,ServiceData> getTwoWeeksServices() {
		return two_weeks_services;
	}

	public long getFetchDuration() {
		return fetch_duration;
	}



	@Override public String toString() {
		return "Data{" +
			"fetch_duration=" + fetch_duration +
			", org_name='" + org_name + '\'' +
			", this_week_date=" + this_week_date +
			", this_week_services=" + this_week_services +
			", next_week_date=" + next_week_date +
			", next_week_services=" + next_week_services +
			", two_weeks_date=" + two_weeks_date +
			", two_weeks_services=" + two_weeks_services +
			'}';
	}


	private static LinkedHashMap<String,ServiceData> listToMap(List<ServiceData> list) {
		LinkedHashMap<String,ServiceData> to_return = new LinkedHashMap<>();
		for ( ServiceData data : list ) {
			to_return.put( data.getName(), data );
		}
		return to_return;
	}
}
