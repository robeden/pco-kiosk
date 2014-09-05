package net.stratfordpark.pco.api;

/**
 *
 */
public class PlanPerson {
	private final long id;
	private final long plan_id;
	private final String position;
//	position_display_times": null,
	private final String status;  // TODO: enum
	private final boolean prepare_notification;
	private final long category_id;
	private final String category_name;
//	category_sequence": null,
//	category_schedule_to": "plan",
//	person_photo_thumbnail": "https://graph.facebook.com/690973354/picture?width=68&height=68",
	private final long person_id;
	private final long account_center_person_id;
	private final String person_name;
	private final long responds_to_id;
//	private final long excluded_times;
	private final String notes;
	private final String decline_reason;
	private final boolean can_accept_partial;
//	declined_time_ids": []


	public PlanPerson( long id, long plan_id, String position, String status,
		boolean prepare_notification, long category_id, String category_name,
		long person_id,
		long account_center_person_id, String person_name, long responds_to_id,
		String notes, String decline_reason,
		boolean can_accept_partial ) {
		this.id = id;
		this.plan_id = plan_id;
		this.position = position;
		this.status = status;
		this.prepare_notification = prepare_notification;
		this.category_id = category_id;
		this.category_name = category_name;
		this.person_id = person_id;
		this.account_center_person_id = account_center_person_id;
		this.person_name = person_name;
		this.responds_to_id = responds_to_id;
//		this.excluded_times = excluded_times;
		this.notes = notes;
		this.decline_reason = decline_reason;
		this.can_accept_partial = can_accept_partial;
	}

	public long getId() {
		return id;
	}

	public long getPlanId() {
		return plan_id;
	}

	public String getPosition() {
		return position;
	}

	public String getStatus() {
		return status;
	}

	public boolean isPrepareNotification() {
		return prepare_notification;
	}

	public long getCategoryId() {
		return category_id;
	}

	public String getCategoryName() {
		return category_name;
	}

	public long getPersonId() {
		return person_id;
	}

	public long getAccountCenterPersonId() {
		return account_center_person_id;
	}

	public String getPersonName() {
		return person_name;
	}

	public long getRespondsToId() {
		return responds_to_id;
	}

//	public long getExcludedTimes() {
//		return excluded_times;
//	}

	public String getNotes() {
		return notes;
	}

	public String getDeclineReason() {
		return decline_reason;
	}

	public boolean isCanAcceptPartial() {
		return can_accept_partial;
	}
}
