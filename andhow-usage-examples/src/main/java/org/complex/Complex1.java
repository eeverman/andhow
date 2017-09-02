package org.complex;

/**
 * This example has almost no comments b/c it's a minimal example for the AndHow! homepage.
 */

import org.simple.*;
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.PropertyGroup;

public class Complex1 {
	
	public static void main(String[] args) {
		StrProp ERR_DECLAIRED_IN_MAIN_METHOD = StrProp.builder().build();
	}
	
	public static interface Int1 {
		
		StrProp DECLAIRED_IN_Int1 = StrProp.builder().build();
		
		public static interface Int1_1 {
		
			StrProp DECLAIRED_IN_Int1_1 = StrProp.builder().build();
		}
	}
	
	public static interface Int2 {
		
		StrProp DECLAIRED_IN_Int2 = StrProp.builder().build();
		
		public static class Class2_1 {
		
			public static final StrProp DECLAIRED_IN_Class2_1 = StrProp.builder().build();
		}
	}
	
	public static class Class3 {
		
		public static final StrProp DECLAIRED_IN_Class3 = StrProp.builder().build();
		
		public static interface Int3_1 {
			StrProp DECLAIRED_IN_Int3_1 = StrProp.builder().build();
		}
	}
	
	public static class Class4 {
		
		public static final StrProp DECLAIRED_IN_Class4 = StrProp.builder().build();
		
		public static class Class4_1 {
			public static final StrProp DECLAIRED_IN_Class4_1 = StrProp.builder().build();
		}
	}
}
