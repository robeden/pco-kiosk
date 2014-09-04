package net.stratfordpark.pco.api;

/**
 *
 */
public class ServiceType {
	private final long id;
	private final String name;
	private final long parent_id;
	private final String type;
	private final String container;
	private final Long container_id;
	private final long sequence;
	private final boolean attachment_types_enabled;

	public ServiceType( long id, String name, long parent_id, String type,
		String container, Long container_id, long sequence,
		boolean attachment_types_enabled ) {
		this.id = id;
		this.name = name;
		this.parent_id = parent_id;
		this.type = type;
		this.container = container;
		this.container_id = container_id;
		this.sequence = sequence;
		this.attachment_types_enabled = attachment_types_enabled;
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

	public long getSequence() {
		return sequence;
	}

	public boolean areAttachmentTypesEnabled() {
		return attachment_types_enabled;
	}

	@Override
	public String toString() {
		return "ServiceType{" +
			"id=" + id +
			", name='" + name + '\'' +
			", parent_id=" + parent_id +
			", type='" + type + '\'' +
			", container='" + container + '\'' +
			", container_id=" + container_id +
			", sequence=" + sequence +
			", attachment_types_enabled=" + attachment_types_enabled +
			'}';
	}
}
