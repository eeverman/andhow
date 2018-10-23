package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.property.StrProp;

/**
 * A class that should have compile problems
 * 
 * @author ericeverman
 */
public class BadProps_2 {

	private static StrProp STR_1 = StrProp.builder().build(); //---Should be final!!!

}
