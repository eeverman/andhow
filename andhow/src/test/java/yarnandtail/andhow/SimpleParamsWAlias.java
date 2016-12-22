package yarnandtail.andhow;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAlias extends PropertyGroup {
	
	StrProp KVP_BOB = StrProp.builder().defaultValue("bob").build();
	StrProp KVP_NULL = StrProp.builder().build();
	FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
	FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
	FlagProp FLAG_NULL = FlagProp.builder().build();
}
