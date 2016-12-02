package yarnandtail.andhow.example.restclient;

import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;
import yarnandtail.andhow.point.FlagPointBuilder;
import yarnandtail.andhow.point.IntConfigPoint;
import yarnandtail.andhow.point.IntPointBuilder;
import yarnandtail.andhow.point.StringPointBuilder;
import yarnandtail.andhow.valid.StringRegex;

/**
 *
 * @author eeverman
 */
public interface SampleRestClientGroup extends ConfigPointGroup {
	
	StringConfigPoint REST_HOST = StringPointBuilder.init().addValidation(new StringRegex("http.*")) .required().build();
	IntConfigPoint REST_PORT = IntPointBuilder.init().required().build();
	StringConfigPoint REST_SERVICE_NAME = StringPointBuilder.init().setDefault("query").build();
	StringConfigPoint AUTH_KEY = StringPointBuilder.init().required().build();
	IntConfigPoint RETRY_COUNT = IntPointBuilder.init().setDefault(2).build();
	FlagConfigPoint REQUEST_META_DATA = FlagPointBuilder.init().setDefault(true).build();
	FlagConfigPoint REQUEST_SUMMARY_DATA = FlagPointBuilder.init().build();
}
