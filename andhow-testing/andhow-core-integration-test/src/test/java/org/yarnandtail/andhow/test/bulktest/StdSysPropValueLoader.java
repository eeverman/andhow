package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.AndHowTestConfig.AndHowTestConfigImpl;

import java.util.Properties;

public class StdSysPropValueLoader extends MapValueLoader {

	public void completeConfiguration(AndHowTestConfigImpl config) {
		config.setSystemProperties(args);
	}

}
