package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;
import yarnandtail.andhow.point.FlagPointBuilder;
import yarnandtail.andhow.point.StringPointBuilder;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAliasRequired extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = StringPointBuilder.init().setDefault("Bob").required().build();
	StringConfigPoint KVP_NULL = StringPointBuilder.init().required().build();
	FlagConfigPoint FLAG_FALSE = FlagPointBuilder.init().setDefault(false).required().build();
	FlagConfigPoint FLAG_TRUE = FlagPointBuilder.init().setDefault(true).required().build();
	FlagConfigPoint FLAG_NULL = FlagPointBuilder.init().required().build();
}
