package yarnandtail.andhow;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAliasDuplicate extends PropertyGroup {
	
	StrProp KVP_BOB = StrProp.builder()
			.setDefault("bob")
			.addAlias("kvpbob")
			.build();
	StrProp KVP_NULL = StrProp.builder()
			.addAlias("kvpnull")
			.build();
	FlagProp FLAG_FALSE = FlagProp.builder()
			.setDefault(false)
			.addAlias("flagfalse")
			.build();
	FlagProp FLAG_TRUE = FlagProp.builder()
			.setDefault(true)
			.addAlias("flagtrue")
			.build();
	FlagProp FLAG_NULL = FlagProp.builder()
			.addAlias("flagnull")
			.build();
}
