package org.yarnandtail.andhow;

import java.time.LocalDateTime;
import org.yarnandtail.andhow.property.*;

/**
 * Test set of params w/ one of each type.
 *
 * Naming is [type]_[default value]
 *
 * @author eeverman
 */
public interface SimpleParams {

	//Strings
	StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
	StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();
	StrProp STR_END_XXX = StrProp.builder().mustEndWith("XXX").build();

	//Flags
	FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
	FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
	FlagProp FLAG_NULL = FlagProp.builder().build();

	//Integers
	IntProp INT_TEN = IntProp.builder().defaultValue(10).build();
	IntProp INT_NULL = IntProp.builder().build();
	IntProp INT_BIG_TEN = IntProp.builder().greaterThan(10).build();

	//Long
	LngProp LNG_TEN = LngProp.builder().defaultValue(10L).build();
	LngProp LNG_NULL = LngProp.builder().build();
	LngProp LNG_LESS_TEN = LngProp.builder().lessThan(10L).build();

	//Double
	DblProp DBL_TEN = DblProp.builder().defaultValue(10d).build();
	DblProp DBL_NULL = DblProp.builder().build();
	DblProp DBL_LESS_ZERO = DblProp.builder().lessThan(0).build();

	//LocalDateTime
	LocalDateTimeProp LDT_2007_10_01 = LocalDateTimeProp.builder().defaultValue(LocalDateTime.parse("2007-10-01T00:00")).build();
	LocalDateTimeProp LDT_NULL = LocalDateTimeProp.builder().build();
	LocalDateTimeProp LDT_AFTER_2000 = LocalDateTimeProp.builder().mustBeAfter(LocalDateTime.parse("2000-01-01T00:00")).build();
}
