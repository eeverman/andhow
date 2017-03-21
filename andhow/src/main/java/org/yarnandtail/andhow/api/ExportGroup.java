package org.yarnandtail.andhow.api;

/**
 * Bundles an exporter and a BasePropertyGroup for it to export.
 * 
 * Exporters that have no group (null) are intended to export everything.
 */
public class ExportGroup {
	private Exporter exporter;
	private Class<? extends BasePropertyGroup> group;

	public ExportGroup(Exporter exporter, Class<? extends BasePropertyGroup> group) {
		this.exporter = exporter;
		this.group = group;
	}

	public Exporter getExporter() {
		return exporter;
	}

	public Class<? extends BasePropertyGroup> getGroup() {
		return group;
	}
	
	
}