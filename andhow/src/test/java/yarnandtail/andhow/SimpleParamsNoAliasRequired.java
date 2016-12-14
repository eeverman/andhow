package yarnandtail.andhow;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAliasRequired extends PropertyGroup {
	
	StrProp KVP_BOB = StrProp.builder().defaultValue("Bob").required().build();
	StrProp KVP_NULL = StrProp.builder().required().build();
	FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).required().build();
	FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).required().build();
	FlagProp FLAG_NULL = FlagProp.builder().required().build();
}
