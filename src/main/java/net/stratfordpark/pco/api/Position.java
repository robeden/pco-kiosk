package net.stratfordpark.pco.api;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Position {
	private final String category_name;
	private final long category_id;
	private final long id;
	private final long plan_id;
	private final @SerializedName( "position" ) String name;
	private final int quantity;

	public Position( String category_name, long category_id, long id, long plan_id,
		String name, int quantity ) {
		this.category_name = category_name;
		this.category_id = category_id;
		this.id = id;
		this.plan_id = plan_id;
		this.name = name;
		this.quantity = quantity;
	}


	public String getCategoryName() {
		return category_name;
	}

	public long getCategoryId() {
		return category_id;
	}

	public long getId() {
		return id;
	}

	public long getPlanId() {
		return plan_id;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}
}
