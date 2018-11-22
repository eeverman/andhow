package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.property.StrProp;

/**
 * A class that should compile just fine w/ AndHow's Annotation compiler
 * 
 * @author ericeverman
 */
public class HappyPathProps {

	private static final StrProp STR_1 = StrProp.builder().build();
	public static final StrProp STR_2 = StrProp.builder().build();

	private static interface INNER {

		static final StrProp STR_1 = StrProp.builder().build();
		static final StrProp STR_2 = StrProp.builder().build();
	}
	
}
