package org.yarnandtail.andhow;

import org.yarnandtail.andhow.property.*;

/**
 * Demonstrates an invalid reference to a Property value at construction time.
 * @author ericeverman
 */
public class AndHowReentrantTest_BadSample_1 {
	static final StrProp STR_1 = StrProp.builder().defaultValue("one").build();
	static final StrProp STR_2 = StrProp.builder().defaultValue(STR_1.getValue()).build();
}

