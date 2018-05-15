package net.stratfordpark.pco.api;

/**
 *
 */
public class Organization {
	private final long id;
	private final String name;

	public Organization( long id, String name ) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Organization{" +
			"id=" + id +
			", name='" + name + '\'' +
			'}';
	}
}
