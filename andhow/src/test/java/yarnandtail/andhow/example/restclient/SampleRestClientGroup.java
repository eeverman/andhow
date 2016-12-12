package yarnandtail.andhow.example.restclient;

import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;
import yarnandtail.andhow.point.IntConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SampleRestClientGroup extends ConfigPointGroup {
	
	StringConfigPoint REST_HOST = StringConfigPoint.builder().mustMatchRegex(".*\\.usgs\\.gov") .required().build();
	IntConfigPoint REST_PORT = IntConfigPoint.builder().required().mustBeGreaterThanOrEqualTo(80).mustBeLessThan(10000).build();
	StringConfigPoint REST_SERVICE_NAME = StringConfigPoint.builder().setDefault("query/").mustEndWith("/", true).build();
	StringConfigPoint AUTH_KEY = StringConfigPoint.builder().required().build();
	IntConfigPoint RETRY_COUNT = IntConfigPoint.builder().setDefault(2).build();
	FlagConfigPoint REQUEST_META_DATA = FlagConfigPoint.builder().setDefault(true).build();
	FlagConfigPoint REQUEST_SUMMARY_DATA = FlagConfigPoint.builder().build();
}
