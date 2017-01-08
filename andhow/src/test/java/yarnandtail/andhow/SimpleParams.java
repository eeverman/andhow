package yarnandtail.andhow;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.property.LngProp;

/**
 * Test set of params w/ one of each type.
 * 
 * Naming is [type]_[default value]
 * 
 * @author eeverman
 */
public interface SimpleParams extends PropertyGroup {
	
	//Strings
	StrProp STR_BOB = StrProp.builder().defaultValue("bob").build();
	StrProp STR_NULL = StrProp.builder().build();
	
	//Flags
	FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
	FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
	FlagProp FLAG_NULL = FlagProp.builder().build();
	
	//Integers
	IntProp INT_TEN = IntProp.builder().defaultValue(10).build();
	IntProp INT_NULL = IntProp.builder().build();
	
	//Integers
	LngProp LNG_TEN = LngProp.builder().defaultValue(10L).build();
	LngProp LNG_NULL = LngProp.builder().build();
}
