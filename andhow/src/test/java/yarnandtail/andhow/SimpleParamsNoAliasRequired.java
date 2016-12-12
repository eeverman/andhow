package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAliasRequired extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = StringConfigPoint.builder().setDefault("Bob").required().build();
	StringConfigPoint KVP_NULL = StringConfigPoint.builder().required().build();
	FlagConfigPoint FLAG_FALSE = FlagConfigPoint.builder().setDefault(false).required().build();
	FlagConfigPoint FLAG_TRUE = FlagConfigPoint.builder().setDefault(true).required().build();
	FlagConfigPoint FLAG_NULL = FlagConfigPoint.builder().required().build();
}
