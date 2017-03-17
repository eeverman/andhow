package org.yarnandtail.andhow;

import org.yarnandtail.andhow.api.PropertyGroup;
import org.yarnandtail.andhow.property.FlagProp;

/**
 * Global options to configure AndHow behavior.
 * 
 * This PropertyGroup is always implicitly added to the list of registered groups.
 * Configuration instances should not directly add it because it will result in
 * a duplicate PropertyGroup configuration error.
 * 
 * Convention:  All options will have aliases.  To make naming collisions less
 * likely, all aliases will start with 'AH'.
 * 
 * Implementation Note:  All Properties here must be optional, since the
 * framework itself should have no requirements to operate.
 * 
 * @author ericeverman
 */
@GroupInfo(name="AndHow! Configuration Framework Configuration", desc="Configures how AndHow itself operates")
public interface Options extends PropertyGroup {
	FlagProp CREATE_SAMPLES = FlagProp.builder().aliasIn("AHForceCreateSamples")
			.desc("Forces configuration samples to be sent to the console for each loader that supports it.")
			.helpText("On cmdline, this works as a flag and is assumed 'true' just by being present. In other config sources it can be set to 'true'.")
			.build();
}
