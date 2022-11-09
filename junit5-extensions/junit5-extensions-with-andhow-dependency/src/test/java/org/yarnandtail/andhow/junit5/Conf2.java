package org.yarnandtail.andhow.junit5;

import org.yarnandtail.andhow.property.StrProp;

public interface Conf2 {

	StrProp MY_PROP = StrProp.builder().aliasInAndOut("CONF2.MY.PROP").build();

	static interface Inner1 {
		// Required!
		StrProp MY_PROP = StrProp.builder().aliasInAndOut("CONF2_INNER1_MY_PROP").notNull().build();
	}

	static interface Inner2 {
		// I have no config properties...
	}
}
