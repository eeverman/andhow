package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAlias extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = new StringConfigPoint("bob", "", "kvpbob");
	StringConfigPoint KVP_NULL = new StringConfigPoint(null, "", "kvpnull");
	FlagConfigPoint FLAG_FALSE = new FlagConfigPoint(false, "", "flagfalse");
	FlagConfigPoint FLAG_TRUE = new FlagConfigPoint(true, "", "flagtrue");
	FlagConfigPoint FLAG_NULL = new FlagConfigPoint(null, "", "flagnull");
}
