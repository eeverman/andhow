package org.yarnandtail.andhow.junit5.usagetests;

import org.yarnandtail.andhow.property.StrProp;

public interface Conf1 {

	StrProp MY_PROP = StrProp.builder().aliasInAndOut("CONF1_MY_PROP").build();

	static interface Inner1 {
		StrProp MY_PROP = StrProp.builder().aliasInAndOut("CONF1_INNER1_MY_PROP").build();
	}
}
