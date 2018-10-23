package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.property.StrProp;

/**
 * A class that should have compile problems
 * 
 * @author ericeverman
 */
public class BadProps_1 {

	private static StrProp STR_1 = StrProp.builder().build(); //---Should be final!!!
	public final StrProp STR_2 = StrProp.builder().build();	//---Should be static!!!
	StrProp STR_3 = StrProp.builder().build();	//---Should be static final!!!
	
	private static interface INNER {
		StrProp STR_1 = StrProp.builder().build();	//static final is assumed
		static final StrProp STR_2 = StrProp.builder().build();
	}
	
	private static class INNER_CLASS {
		private static StrProp STR_1 = StrProp.builder().build(); //---Should be final!!!
		public final StrProp STR_2 = StrProp.builder().build();	//---Should be static!!!
		StrProp STR_3 = StrProp.builder().build();	//---Should be static final!!!
	}
	
}
