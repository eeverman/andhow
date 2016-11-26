package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;
import yarnandtail.andhow.point.FlagPointBuilder;
import yarnandtail.andhow.point.IntConfigPoint;
import yarnandtail.andhow.point.IntPointBuilder;
import yarnandtail.andhow.point.StringPointBuilder;

/**
 *
 * @author eeverman
 */
public interface SampleRestConnectGroup extends ConfigPointGroup {
	
	StringConfigPoint REST_HOST = StringPointBuilder.init().required().build();
	IntConfigPoint REST_PORT = IntPointBuilder.init().required().build();
	StringConfigPoint REST_SERVICE_NAME = StringPointBuilder.init().setDefault("query").build();
	StringConfigPoint AUTH_KEY = StringPointBuilder.init().required().build();
	IntConfigPoint RETRY_COUNT = IntPointBuilder.init().setDefault(2).build();
	FlagConfigPoint REQUEST_META_DATA = FlagPointBuilder.init().setDefault(true).build();
	FlagConfigPoint REQUEST_EDIT_KEYS = FlagPointBuilder.init().build();
}
