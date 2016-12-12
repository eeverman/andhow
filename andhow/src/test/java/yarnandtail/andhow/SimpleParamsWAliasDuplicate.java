package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAliasDuplicate extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = StringConfigPoint.builder()
			.setDefault("bob")
			.addAlias("kvpbob")
			.build();
	StringConfigPoint KVP_NULL = StringConfigPoint.builder()
			.addAlias("kvpnull")
			.build();
	FlagConfigPoint FLAG_FALSE = FlagConfigPoint.builder()
			.setDefault(false)
			.addAlias("flagfalse")
			.build();
	FlagConfigPoint FLAG_TRUE = FlagConfigPoint.builder()
			.setDefault(true)
			.addAlias("flagtrue")
			.build();
	FlagConfigPoint FLAG_NULL = FlagConfigPoint.builder()
			.addAlias("flagnull")
			.build();
}
