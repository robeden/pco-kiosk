package net.stratfordpark.pco.api;

import java.util.List;

/**
 *
 */
public class Organization {
	private final long id;
	private final String name;
	private final String owner_name;
	private final boolean music_stand_enabled;
	private final boolean projector_enabled;
	private final List<ServiceType> service_types;
	private final List<ServiceTypeFolder> service_type_folders;
	private final boolean ccli_connected;
	private final long secret;

	public Organization( long id, String name, String owner_name,
		boolean music_stand_enabled, boolean projector_enabled,
		List<ServiceType> service_types,
		List<ServiceTypeFolder> service_type_folders, boolean ccli_connected,
		long secret ) {
		this.id = id;
		this.name = name;
		this.owner_name = owner_name;
		this.music_stand_enabled = music_stand_enabled;
		this.projector_enabled = projector_enabled;
		this.service_types = service_types;
		this.service_type_folders = service_type_folders;
		this.ccli_connected = ccli_connected;
		this.secret = secret;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return owner_name;
	}

	public boolean isMusicStandEnabled() {
		return music_stand_enabled;
	}

	public boolean isProjectorEnabled() {
		return projector_enabled;
	}

	public List<ServiceType> getServiceTypes() {
		return service_types;
	}

	public List<ServiceTypeFolder> getServiceTypeFolders() {
		return service_type_folders;
	}

	public boolean isCcliConnected() {
		return ccli_connected;
	}

	public long getSecret() {
		return secret;
	}

	@Override
	public String toString() {
		return "Organization{" +
			"id=" + id +
			", name='" + name + '\'' +
			", owner_name='" + owner_name + '\'' +
			", music_stand_enabled=" + music_stand_enabled +
			", projector_enabled=" + projector_enabled +
			", service_types=" + service_types +
			", service_type_folders=" + service_type_folders +
			", ccli_connected=" + ccli_connected +
			", secret=" + secret +
			'}';
	}

}
