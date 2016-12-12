package yarnandtail.andhow;

import yarnandtail.andhow.property.StringProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAliasRequired extends PropertyGroup {
	
	StringProp KVP_BOB = StringProp.builder().setDefault("Bob").required().build();
	StringProp KVP_NULL = StringProp.builder().required().build();
	FlagProp FLAG_FALSE = FlagProp.builder().setDefault(false).required().build();
	FlagProp FLAG_TRUE = FlagProp.builder().setDefault(true).required().build();
	FlagProp FLAG_NULL = FlagProp.builder().required().build();
}
