package net.stratfordpark.pco.api;

import java.util.List;

/**
 *
 */
public class ServiceTypeFolder {
	private final long id;
	private final String name;
	private final long parent_id;
	private final String type;
	private final String container;
	private final Long container_id;
	private final List<ServiceType> service_types;

	public ServiceTypeFolder( long id, String name, long parent_id, String type,
		String container, Long container_id,
		List<ServiceType> service_types ) {
		this.id = id;
		this.name = name;
		this.parent_id = parent_id;
		this.type = type;
		this.container = container;
		this.container_id = container_id;
		this.service_types = service_types;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getParentId() {
		return parent_id;
	}

	public String getType() {
		return type;
	}

	public String getContainer() {
		return container;
	}

	public Long getContainerId() {
		return container_id;
	}

	public List<ServiceType> getServiceTypes() {
		return service_types;
	}

	@Override
	public String toString() {
		return "ServiceTypeFolder{" +
			"id=" + id +
			", name='" + name + '\'' +
			", parent_id=" + parent_id +
			", type='" + type + '\'' +
			", container='" + container + '\'' +
			", container_id=" + container_id +
			", service_types=" + service_types +
			'}';
	}
}
