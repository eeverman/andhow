package yarnandtail.andhow;

import yarnandtail.andhow.property.StringProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAlias extends PropertyGroup {
	
	StringProp KVP_BOB = StringProp.builder().build();
	StringProp KVP_NULL = StringProp.builder().build();
	FlagProp FLAG_FALSE = FlagProp.builder().build();
	FlagProp FLAG_TRUE = FlagProp.builder().build();
	FlagProp FLAG_NULL = FlagProp.builder().build();
}
