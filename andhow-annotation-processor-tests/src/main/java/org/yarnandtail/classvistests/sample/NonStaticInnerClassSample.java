package org.yarnandtail.classvistests.sample;

import org.yarnandtail.andhow.property.StrProp;

/**
 * For the record, what happens when inner classes are non-static
 * @author ericeverman
 */
//Default visibility is the most restrictive available for a top level class
public class NonStaticInnerClassSample {

	//Implicitly static - inner interfaces are always static because they cannot
	//be instanced.
	private interface PI {

		StrProp STRING = StrProp.builder().build();

		//
		//Unsure on this, but since the parent is an interface, we have no
		//instance parent defined, so the nonstatic PI_DC class is kind of like
		//a top level class.  Thus, static fields are JVM allowed.
		class PI_DC {
			
			//The JVM may optimize away private vars that are not used.
			//How to handle that possibility??  Should we gracefully ignore
			//NoSuchFieldException when they happen and the field has non-public
			//visibility?  Would need to track visibility in that case.
			private static final StrProp STRING = StrProp.builder().build();
			public static final StrProp STRING_PUB = StrProp.builder().build();
			public final StrProp NON_STATIC_STRING_PUB = StrProp.builder().build();
			
			class PI_DC_DC {
				
				//JVM banned
				//private static final StrProp STRING = StrProp.builder().build();
				
				private final StrProp NON_STATIC_STRING_PUB = StrProp.builder().build();
			}
		}

	}
	
	//should check for classes or interfaces declaired in methods

	private class PC {
		//JVM Not allowed b/c a non-static innerclass is considered part of the 
		//parent INSTANCE.  Its too late to add class level (static) state
		//to the class after an instance is created.
		//private static final StrProp STRING = StrProp.builder().build();
		
		
		public final StrProp NON_STATIC_STRING_PUB = StrProp.builder().build();

//		private static final StrProp STRING = StrProp.builder().build();
//		
//		private static class PC_PC {
//			private static final StrProp STRING = StrProp.builder().build();
//		}
//
//		private static interface PC_PI {
//			StrProp STRING = StrProp.builder().build();
//		}
	}

}
