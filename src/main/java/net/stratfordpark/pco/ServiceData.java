package net.stratfordpark.pco;

import java.util.*;

/**
 *
 */
class ServiceData {
	private final String name;
	private final Date start_date;
	private final SortedMap<String,List<NeedOrVolunteer>> volunteer_map;    // position, names
	private final String plan_title;
	private final String series_title;
	private final long[] start_times;
	private final long[] end_times;

	ServiceData( String name, Date start_date,
		SortedMap<String,List<NeedOrVolunteer>> volunteer_map, String plan_title,
		String series_title, long[] start_times, long[] end_times ) {

		this.name = name;
		this.start_date = start_date;
		this.volunteer_map = volunteer_map;
		this.plan_title = plan_title;
		this.series_title = series_title;
		this.start_times = start_times;
		this.end_times = end_times;
	}

	public String getName() {
		return name;
	}

	public Date getStartDate() {
		return start_date;
	}

	public SortedMap<String,List<NeedOrVolunteer>> getVolunteerMap() {
		return volunteer_map;
	}

	public String getPlanTitle() {
		return plan_title;
	}

	public String getSeriesTitle() {
		return series_title;
	}

	public long[] getEndTimes() {
		return end_times;
	}

	public long[] getStartTimes() {
		return start_times;
	}




	public interface NeedOrVolunteer {}

	public static class Volunteer implements NeedOrVolunteer {
		private final String name;

		public Volunteer( String name ) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static class Need implements NeedOrVolunteer {
		private final int count;

		public Need( int count ) {
			this.count = count;
		}

		@Override
		public String toString() {
			return count + " Needed";
		}
	}
}
