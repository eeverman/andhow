package yarnandtail.andhow;

/**
 * Bundles an exporter and a PropertyGroup for it to export.
 * 
 * Exporters that have no group (null) are intended to export everything.
 */
public class ExportGroup {
	private Exporter exporter;
	private Class<? extends PropertyGroup> group;

	public ExportGroup(Exporter exporter, Class<? extends PropertyGroup> group) {
		this.exporter = exporter;
		this.group = group;
	}

	public Exporter getExporter() {
		return exporter;
	}

	public Class<? extends PropertyGroup> getGroup() {
		return group;
	}
	
	
}