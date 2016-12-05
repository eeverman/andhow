package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAlias extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = StringConfigPoint.builder().build();
	StringConfigPoint KVP_NULL = StringConfigPoint.builder().build();
	FlagConfigPoint FLAG_FALSE = FlagConfigPoint.builder().build();
	FlagConfigPoint FLAG_TRUE = FlagConfigPoint.builder().build();
	FlagConfigPoint FLAG_NULL = FlagConfigPoint.builder().build();
}
