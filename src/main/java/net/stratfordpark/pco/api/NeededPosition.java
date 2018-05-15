package net.stratfordpark.pco.api;

/**
 *
 */
public class NeededPosition {
	private final Attributes attributes;



	public NeededPosition( Attributes attributes ) {
		this.attributes = attributes;
	}



	public int getQuantity() {
		return attributes.quantity;
	}

	public String getPositionName() {
		return attributes.team_position_name;
	}


	private static class Attributes {
		private int quantity;
		private String team_position_name;
	}
}
