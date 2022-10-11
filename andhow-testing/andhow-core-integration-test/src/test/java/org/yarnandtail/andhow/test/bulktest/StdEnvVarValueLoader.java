package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.AndHowTestConfig.AndHowTestConfigImpl;

public class StdEnvVarValueLoader extends MapValueLoader {

	public void completeConfiguration(AndHowTestConfigImpl config) {
		config.setEnvironmentVariables(args);
	}

}
