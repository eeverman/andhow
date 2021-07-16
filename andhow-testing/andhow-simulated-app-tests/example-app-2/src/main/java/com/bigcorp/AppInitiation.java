package com.bigcorp;

import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.StrProp;

public class AppInitiation implements AndHowInit {

	public static final StrProp ANDHOW_CLASSPATH_FILE = StrProp.builder().mustStartWith("/")
			.aliasIn("AH_CLASSPATH") /* Make this easier to config by adding an alias */
			.defaultValue("/checker.default.properties")
			.description("Path to a file on the classpath.  Classpaths must start w/ a slash.").build();

	@Override
	public AndHowConfiguration getConfiguration() {

		return
				StdConfig.instance().setClasspathPropFilePath(ANDHOW_CLASSPATH_FILE);

	}
}
