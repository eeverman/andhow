package yarnandtail.andhow;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAlias extends PropertyGroup {
	
	StrProp KVP_BOB = StrProp.builder()
			.defaultValue("bob")
			.alias("kvpbob")
			.build();
	StrProp KVP_NULL = StrProp.builder()
			.alias("kvpnull")
			.build();
	FlagProp FLAG_FALSE = FlagProp.builder()
			.defaultValue(false)
			.alias("flagfalse")
			.build();
	FlagProp FLAG_TRUE = FlagProp.builder()
			.defaultValue(true)
			.alias("flagtrue")
			.build();
	FlagProp FLAG_NULL = FlagProp.builder()
			.alias("flagnull")
			.build();
}
