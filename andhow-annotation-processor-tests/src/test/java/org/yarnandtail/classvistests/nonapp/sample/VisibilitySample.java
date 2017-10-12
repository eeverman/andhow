package org.yarnandtail.classvistests.nonapp.sample;

/**
 * Non-application related.  Just a general test of what the visibility modifiers
 * do when they are nested in inner classes and interfaces.
 * 
 * @author ericeverman
 */
//Default visibility is the most restrictive available for a top level class
public class VisibilitySample {

	private static final String STRING = "I'm private";
	public static final String STRING_PUB = "I'm public";

	//Direct access via VisibilitySample.PI is not possible from outside this class
	private static interface PI {

		String STRING = "Always public final static in interface.  isAcc is false b/c PI is private.";

		//
		//Default visibility is the most restrictive available inside an interface
		static class PI_DC {
			private static final String STRING = "I'm private";
			public static final String STRING_PUB = "Not isAcc b/c parent is private";
		}

		static interface PI_DI {
			String STRING = "Always public final static in interface.  isAcc is false b/c PI is private.";
		}
	}
	
	//should check for classes or interfaces declaired in methods

	private static class PC {

		private static final String STRING = "I'm private";
		
		private static class PC_PC {
			private static final String STRING = "I'm private";
		}

		private static interface PC_PI {
			String STRING = "I'm private";
		}
	}
	
	//This inner classname is not allowed because it duplicates PI.PI_DC
	//because the JVM uses '$' to separate inner class steps
	//public static interface PI$PI_DC { }
}
