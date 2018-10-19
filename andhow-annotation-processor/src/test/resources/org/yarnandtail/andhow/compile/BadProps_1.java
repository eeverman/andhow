package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.property.StrProp;

/**
 * A class that should compile just fine w/ AndHow's Annotation compiler
 * 
 * @author ericeverman
 */
public class BadProps_1 {

	private static StrProp STR_1 = StrProp.builder().build(); //---Should be final!!!
	public final StrProp STR_2 = StrProp.builder().build();	//---Should be static!!!

	private static interface INNER {

		static final StrProp STR_1 = StrProp.builder().build();
		static final StrProp STR_2 = StrProp.builder().build();
	}
	
}
