package net.stratfordpark.pco.api;

/**
 *
 */
public class ServiceType {
	private final long id;
	private final Attributes attributes;

	public ServiceType( long id, Attributes attributes ) {
		this.id = id;
		this.attributes = attributes;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return attributes.name;
	}



	@Override public String toString() {
		return "ServiceType{" +
			"id=" + id +
			", name='" + attributes.name + '\'' +
			'}';
	}


	private static class Attributes {
		private String name;
	}
}
