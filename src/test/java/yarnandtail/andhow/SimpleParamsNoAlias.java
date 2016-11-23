package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;
import yarnandtail.andhow.point.FlagPointBuilder;
import yarnandtail.andhow.point.StringPointBuilder;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAlias extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = StringPointBuilder.init().build();
	StringConfigPoint KVP_NULL = StringPointBuilder.init().build();
	FlagConfigPoint FLAG_FALSE = FlagPointBuilder.init().build();
	FlagConfigPoint FLAG_TRUE = FlagPointBuilder.init().build();
	FlagConfigPoint FLAG_NULL = FlagPointBuilder.init().build();
}
