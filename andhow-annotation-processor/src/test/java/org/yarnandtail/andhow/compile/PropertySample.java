package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.property.StrProp;

/**
 *
 * @author ericeverman
 */
//Default visibility is the most restrictive available for a top level class
public class PropertySample {

	private static final StrProp STRING = StrProp.builder().build();
	public static final StrProp STRING_PUB = StrProp.builder().build();

	//Direct access via VisibilitySample.PI is not possible from outside this class
	private static interface PI {

		StrProp STRING = StrProp.builder().build();

		//
		//Default visibility is the most restrictive available inside an interface
		static class PI_DC {
			private static final StrProp STRING = StrProp.builder().build();
			public static final StrProp STRING_PUB = StrProp.builder().build();
		}

		static interface PI_DI {
			StrProp STRING = StrProp.builder().build();
			public static final StrProp STRING_PUB = StrProp.builder().build();
		}
	}
	
	//should check for classes or interfaces declaired in methods

	private static class PC {

		private static final StrProp STRING = StrProp.builder().build();
		public static final StrProp STRING_PUB = StrProp.builder().build();
		
		private static class PC_PC {
			private static final StrProp STRING = StrProp.builder().build();
		}

		private static interface PC_PI {
			StrProp STRING = StrProp.builder().build();
		}
	}
	
	//This inner classname is not allowed because it duplicates PI.PI_DC
	//because the JVM uses '$' to separate inner class steps
	//public static interface PI$PI_DC { }
}
