package yarnandtail.andhow;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAlias extends PropertyGroup {
	
	StrProp KVP_BOB = StrProp.builder().build();
	StrProp KVP_NULL = StrProp.builder().build();
	FlagProp FLAG_FALSE = FlagProp.builder().build();
	FlagProp FLAG_TRUE = FlagProp.builder().build();
	FlagProp FLAG_NULL = FlagProp.builder().build();
}
