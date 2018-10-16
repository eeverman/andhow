package org.yarnandtail.andhow;

import org.yarnandtail.andhow.property.*;

/**
 * This should be just fine
 * @author ericeverman
 */
public class AndHowReentrantTest_OkSample_1 {
	static final StrProp STR_1 = StrProp.builder().defaultValue("one").build();
	static final StrProp STR_2 = StrProp.builder().defaultValue("two").build();
	
	static String getSomeString() {
		return STR_1.getValue() + STR_2.getValue();	//This should be ok
	}
}

