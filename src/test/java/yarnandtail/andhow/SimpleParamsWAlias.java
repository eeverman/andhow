package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsWAlias extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = new StringConfigPoint("bob", false, "", "kvpbob");
	StringConfigPoint KVP_NULL = new StringConfigPoint(null, false, "", "kvpnull");
	FlagConfigPoint FLAG_FALSE = new FlagConfigPoint(false, false, "", "flagfalse");
	FlagConfigPoint FLAG_TRUE = new FlagConfigPoint(true, false, "", "flagtrue");
	FlagConfigPoint FLAG_NULL = new FlagConfigPoint(null, false, "", "flagnull");
}
