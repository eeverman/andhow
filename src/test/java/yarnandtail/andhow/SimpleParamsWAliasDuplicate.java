package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;
import yarnandtail.andhow.point.FlagPointBuilder;
import yarnandtail.andhow.point.StringPointBuilder;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAliasDuplicate extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = StringPointBuilder.init()
			.setDefault("bob")
			.addAlias("kvpbob")
			.build();
	StringConfigPoint KVP_NULL = StringPointBuilder.init()
			.addAlias("kvpnull")
			.build();
	FlagConfigPoint FLAG_FALSE = FlagPointBuilder.init()
			.setDefault(false)
			.addAlias("flagfalse")
			.build();
	FlagConfigPoint FLAG_TRUE = FlagPointBuilder.init()
			.setDefault(true)
			.addAlias("flagtrue")
			.build();
	FlagConfigPoint FLAG_NULL = FlagPointBuilder.init()
			.addAlias("flagnull")
			.build();
}
