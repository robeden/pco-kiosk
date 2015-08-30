package net.stratfordpark.pco.api;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class Plan {
	private final long id;
	private final String plan_title;
	private final String series_title;
	private final int service_type_id;
	private final String dates;
	private final Series series;
	@SerializedName("public") private final boolean is_public;
	private final Date updated_at;
	private final Date created_at;
	private final String type;
	private final String permissions;
	private final ServiceType service_type;
	private final int total_length;
	private final String total_length_formatted;
	private final String comma_separated_attachment_type_ids;
//	private final List<String> plan_notes;
	private final List<PlanPerson> plan_people;
	private final List<PlanItem> items;
	@SerializedName( "positions" ) private final List<Position> open_positions;
	private final List<ServiceTime> service_times;
	private final List<ServiceTime> rehearsal_times;
	private final List<ServiceTime> other_times;

	public Plan( long id, String plan_title, String series_title, int service_type_id,
		String dates, Series series, boolean is_public, Date updated_at,
		Date created_at, String type, String permissions,
		ServiceType service_type, int total_length, String total_length_formatted,
		String comma_separated_attachment_type_ids,
		List<String> plan_notes, List<PlanPerson> plan_people,
		List<PlanItem> items, List<Position> open_positions,
		List<ServiceTime> service_times, List<ServiceTime> rehearsal_times,
		List<ServiceTime> other_times ) {

		this.id = id;
		this.plan_title = plan_title;
		this.series_title = series_title;
		this.service_type_id = service_type_id;
		this.dates = dates;
		this.series = series;
		this.is_public = is_public;
		this.updated_at = updated_at;
		this.created_at = created_at;
		this.type = type;
		this.permissions = permissions;
		this.service_type = service_type;
		this.total_length = total_length;
		this.total_length_formatted = total_length_formatted;
		this.comma_separated_attachment_type_ids = comma_separated_attachment_type_ids;
//		this.plan_notes = plan_notes;
		this.plan_people = plan_people;
		this.items = items;
		this.open_positions = open_positions;
		this.service_times = service_times;
		this.rehearsal_times = rehearsal_times;
		this.other_times = other_times;
	}


	public long getId() {
		return id;
	}

	public String getPlanTitle() {
		return plan_title;
	}

	public String getSeriesTitle() {
		return series_title;
	}

	public int getServiceTypeId() {
		return service_type_id;
	}

	public String getDates() {
		return dates;
	}

	public Series getSeries() {
		return series;
	}

	public boolean isPublic() {
		return is_public;
	}

	public Date getUpdatedAt() {
		return updated_at;
	}

	public Date getCreatedAt() {
		return created_at;
	}

	public String getType() {
		return type;
	}

	public String getPermissions() {
		return permissions;
	}

	public ServiceType getServiceType() {
		return service_type;
	}

	public int getTotalLength() {
		return total_length;
	}

	public String getTotalLengthFormatted() {
		return total_length_formatted;
	}

	public String getCommaSeparatedAttachmentTypeIds() {
		return comma_separated_attachment_type_ids;
	}

//	public List<String> getPlanNotes() {
//		return plan_notes;
//	}

	public List<PlanPerson> getPlanPeople() {
		return plan_people;
	}

	public List<PlanItem> getItems() {
		return items;
	}

	public List<Position> getOpenPositions() {
		return open_positions;
	}

	public List<ServiceTime> getServiceTimes() {
		return service_times;
	}

	public List<ServiceTime> getRehearsalTimes() {
		return rehearsal_times;
	}

	public List<ServiceTime> getOtherTimes() {
		return other_times;
	}


	@Override
	public String toString() {
		return "Plan{" +
			"id=" + id +
			", plan_title='" + plan_title + '\'' +
			", series_title='" + series_title + '\'' +
			", service_type_id=" + service_type_id +
			", dates='" + dates + '\'' +
			", series=" + series +
			", is_public=" + is_public +
			", updated_at=" + updated_at +
			", created_at=" + created_at +
			", type='" + type + '\'' +
			", permissions='" + permissions + '\'' +
			", service_type=" + service_type +
			", total_length=" + total_length +
			", total_length_formatted='" + total_length_formatted + '\'' +
			", comma_separated_attachment_type_ids='" +
			comma_separated_attachment_type_ids +
			'\'' +
//			", plan_notes=" + plan_notes +
			", plan_people=" + plan_people +
			", items=" + items +
			", open_positions=" + open_positions +
			", service_times=" + service_times +
			", rehearsal_times=" + rehearsal_times +
			", other_times=" + other_times +
			'}';
	}
}
