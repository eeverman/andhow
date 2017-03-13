package org.yarnandtail.andhow;

import org.yarnandtail.andhow.property.FlagProp;

/**
 * Global options to configure AndHow behavior.
 * 
 * @author ericeverman
 */
public interface Options extends PropertyGroup {
	FlagProp CREATE_SAMPLES = FlagProp.builder().aliasIn("forceCreateSamples")
			.desc("Forces configuration samples to be sent to the console for each loader that supports it.")
			.helpText("On cmdline, this works as a flag and is assumed 'true' just by being present. In other config sources it can be set to 'true'.")
			.build();
}
