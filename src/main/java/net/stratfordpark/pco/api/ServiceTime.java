package net.stratfordpark.pco.api;

import java.util.Date;

/**
 *
 */
public class ServiceTime {
	private final long id;
	private final long ministry_id;
	private final Date starts_at;
	private final Date ends_at;
	private final Date updated_at;
	private final Date created_at;
	private final long plan_id;
	private final boolean plan_visible;
	private final long created_by_id;
	private final long updated_by_id;
	private final boolean print;
	private final boolean recorded;
	private final String name;
	private final String time_type;

	public ServiceTime( long id, long ministry_id, Date starts_at, Date ends_at,
		Date updated_at, Date created_at, long plan_id, boolean plan_visible,
		long created_by_id, long updated_by_id, boolean print, boolean recorded,
		String name, String time_type ) {
		this.id = id;
		this.ministry_id = ministry_id;
		this.starts_at = starts_at;
		this.ends_at = ends_at;
		this.updated_at = updated_at;
		this.created_at = created_at;
		this.plan_id = plan_id;
		this.plan_visible = plan_visible;
		this.created_by_id = created_by_id;
		this.updated_by_id = updated_by_id;
		this.print = print;
		this.recorded = recorded;
		this.name = name;
		this.time_type = time_type;
	}

	public long getId() {
		return id;
	}

	public long getMinistryId() {
		return ministry_id;
	}

	public Date getStartsAt() {
		return starts_at;
	}

	public Date getEndsAt() {
		return ends_at;
	}

	public Date getUpdatedAt() {
		return updated_at;
	}

	public Date getCreatedAt() {
		return created_at;
	}

	public long getPlanId() {
		return plan_id;
	}

	public boolean isPlanVisible() {
		return plan_visible;
	}

	public long getCreatedById() {
		return created_by_id;
	}

	public long getUpdatedById() {
		return updated_by_id;
	}

	public boolean isPrint() {
		return print;
	}

	public boolean isRecorded() {
		return recorded;
	}

	public String getName() {
		return name;
	}

	public String getTimeType() {
		return time_type;
	}

	@Override
	public String toString() {
		return "ServiceTime{" +
			"id=" + id +
			", ministry_id=" + ministry_id +
			", starts_at=" + starts_at +
			", ends_at=" + ends_at +
			", updated_at=" + updated_at +
			", created_at=" + created_at +
			", plan_id=" + plan_id +
			", plan_visible=" + plan_visible +
			", created_by_id=" + created_by_id +
			", updated_by_id=" + updated_by_id +
			", print=" + print +
			", recorded=" + recorded +
			", name='" + name + '\'' +
			", time_type='" + time_type + '\'' +
			'}';
	}
}
