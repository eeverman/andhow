package net.spacebase;

import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

/**
 * A standalone interface used to contain a related set of configuration
 * properties for a legacy service that doesn't use AndHow and only reads configuration
 * from system properties.
 * 
 * @author ericeverman
 */
@GroupExport(exporter = SysPropExporter.class,
		exportByCanonicalName = Exporter.EXPORT_CANONICAL_NAME.NEVER,
		exportByOutAliases = Exporter.EXPORT_OUT_ALIASES.ALWAYS)
@GroupInfo(name = "Satellite Service Configuration for a legacy service",
		desc = "ReallyOldSatelliteSerive is a legacy/3rd party service that doesn't " +
				"use AndHow and is only configurable via system properties.  No Problem! " +
				"Create Properties for the configuration points and add aliases for " +
				"the System property names the legacy service is expecting.")
public interface SatelliteServiceConfig {

	/*
	Each Property below includes an 'aliasInAndOut', which does two things:  It
	adds an alternate name that will be recognized when loading the property, and
	it adds the ability to export the property under that aliased name.
	The @GroupExport annotation above specifies that the values of all properties
	should be exported as system properties using their 'out' alias names.
	
	This enables use to use the strong typing, early validation and rich documentation
	of AndHow, but still pass values thru to legacy systems that only use System
	properties for configuration.
	
	To be more selective, its possible to just specify aliasIn() - specify an
	alternate name that will be recognized for loading values - or just specify
	aliasOut() - just an export name.
	 */
	StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").mustBeNonNull().aliasInAndOut("sat.svs").build();
	IntProp TIMEOUT = IntProp.builder().defaultValue(20).mustBeNonNull().aliasInAndOut("sat.to").build();
	StrProp QUERY_ENDPOINT = StrProp.builder().mustBeNonNull().aliasInAndOut("sat.query").build();
	StrProp ITEM_ENDPOINT = StrProp.builder().mustBeNonNull().aliasInAndOut("sat.item").build();

}
