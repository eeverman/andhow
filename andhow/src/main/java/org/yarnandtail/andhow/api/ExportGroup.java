package org.yarnandtail.andhow.api;

/**
 * Bundles an exporter and a Group for it to export.
 * 
 * Exporters that have no group (null) are intended to export everything.
 */
public class ExportGroup {
	private Exporter exporter;
	private GroupProxy group;

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