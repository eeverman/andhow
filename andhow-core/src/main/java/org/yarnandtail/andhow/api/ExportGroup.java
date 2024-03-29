package org.yarnandtail.andhow.api;

/**
 * Bundles an exporter and a Group for it to export.
 *
 */
public class ExportGroup {
	private final Exporter exporter;
	private final GroupProxy group;

	public ExportGroup(Exporter exporter, GroupProxy group) {
		this.exporter = exporter;
		this.group = group;
	}

	public Exporter getExporter() {
		return exporter;
	}

	public GroupProxy getGroup() {
		return group;
	}
	
	
}