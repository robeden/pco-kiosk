package net.stratfordpark.pco;

import java.util.*;

/**
 *
 */
class ServiceData {
	private final String name;
	private final Date start_date;
	private final SortedMap<String,List<NeedOrVolunteer>> volunteer_map;    // position, names

	ServiceData( String name, Date start_date,
		SortedMap<String,List<NeedOrVolunteer>> volunteer_map ) {

		this.name = name;
		this.start_date = start_date;
		this.volunteer_map = volunteer_map;
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



	public static interface NeedOrVolunteer {}

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
