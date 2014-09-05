package net.stratfordpark.pco.api;

import java.util.List;

/**
 *
 */
public class PlanItem {
	private final long id;
	private final String title;
	private final long sequence;
	private final long plan_id;
	private final String dom_id;
	private final String type;
	private final int length;
	private final String length_formatted;
	private final String detail;
	private final int comments_count;
	private final boolean is_preservice;
	private final boolean is_postservice;
	private final boolean is_header;
	private final boolean using_custom_slides;
//	plan_item_medias": [],
	private final List<String> plan_item_notes;
//	plan_item_times": [],
//	ccli_print_single": 0,
//	ccli_print_collected": 0,
//	ccli_screen": 0,
//	ccli_custom_arrangement": 0,
//	ccli_recorded": 0,
	private final Long song_id;
	private final Long arrangement_id;
	private final Long key_id;
	private final String information;
	private final String description;
//	arrangement_sequence_to_s": "",
//	attachments": []


	public PlanItem( long id, String title, long sequence, long plan_id,
		String dom_id, String type, int length, String length_formatted,
		String detail, int comments_count, boolean is_preservice, boolean is_postservice,
		boolean is_header, boolean using_custom_slides,
		List<String> plan_item_notes, Long song_id, Long arrangement_id,
		Long key_id, String information, String description ) {
		this.id = id;
		this.title = title;
		this.sequence = sequence;
		this.plan_id = plan_id;
		this.dom_id = dom_id;
		this.type = type;
		this.length = length;
		this.length_formatted = length_formatted;
		this.detail = detail;
		this.comments_count = comments_count;
		this.is_preservice = is_preservice;
		this.is_postservice = is_postservice;
		this.is_header = is_header;
		this.using_custom_slides = using_custom_slides;
		this.plan_item_notes = plan_item_notes;
		this.song_id = song_id;
		this.arrangement_id = arrangement_id;
		this.key_id = key_id;
		this.information = information;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public long getSequence() {
		return sequence;
	}

	public long getPlanId() {
		return plan_id;
	}

	public String getDomId() {
		return dom_id;
	}

	public String getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public String getLengthFormatted() {
		return length_formatted;
	}

	public String getDetail() {
		return detail;
	}

	public int getCommentsCount() {
		return comments_count;
	}

	public boolean isPreservice() {
		return is_preservice;
	}

	public boolean isPostservice() {
		return is_postservice;
	}

	public boolean isHeader() {
		return is_header;
	}

	public boolean isUsingCustomSlides() {
		return using_custom_slides;
	}

	public List<String> getPlanItemNotes() {
		return plan_item_notes;
	}

	public Long getSongId() {
		return song_id;
	}

	public Long getArrangementId() {
		return arrangement_id;
	}

	public Long getKeyId() {
		return key_id;
	}

	public String getInformation() {
		return information;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "PlanItem{" +
			"id=" + id +
			", title='" + title + '\'' +
			", sequence=" + sequence +
			", plan_id=" + plan_id +
			", dom_id='" + dom_id + '\'' +
			", type='" + type + '\'' +
			", length=" + length +
			", length_formatted='" + length_formatted + '\'' +
			", detail='" + detail + '\'' +
			", comments_count=" + comments_count +
			", is_preservice=" + is_preservice +
			", is_postservice=" + is_postservice +
			", is_header=" + is_header +
			", using_custom_slides=" + using_custom_slides +
			", plan_item_notes=" + plan_item_notes +
			", song_id=" + song_id +
			", arrangement_id=" + arrangement_id +
			", key_id=" + key_id +
			", information='" + information + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}
