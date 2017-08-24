package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author ericeverman
 */
public class SampleNestedPropGroup {
	
	public static interface Nest1 {
		public static interface Config {
			FlagProp PROP1 = FlagProp.builder().build();
		}
	}
	
	public static interface Nest2 {
		public static interface Config {
			FlagProp PROP1 = FlagProp.builder().build();
		}
	}
}
