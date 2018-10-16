package org.yarnandtail.andhow;

import org.yarnandtail.andhow.property.*;

/**
 * Demonstrates an invalid reference to a Property value at construction time.
 * @author ericeverman
 */
public class AndHowReentrantTest_BadSample_2 {
	static final StrProp STR_1 = StrProp.builder().defaultValue("one").build();
	static final StrProp STR_2 = StrProp.builder().defaultValue("two").build();
	static final String SOME_STRING;
	
	static {
		SOME_STRING = STR_1.getValue() + STR_2.getValue();	//This should be ok
	}
}

