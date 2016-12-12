package yarnandtail.andhow;

import yarnandtail.andhow.property.StringProp;
import yarnandtail.andhow.property.FlagProp;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAlias extends PropertyGroup {
	
	StringProp KVP_BOB = StringProp.builder()
			.setDefault("bob")
			.addAlias("kvpbob")
			.build();
	StringProp KVP_NULL = StringProp.builder()
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
