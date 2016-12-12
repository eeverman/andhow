package yarnandtail.andhow.example.restclient;

import yarnandtail.andhow.property.StringProp;
import yarnandtail.andhow.property.FlagProp;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.PropertyGroup;

/**
 *
 * @author eeverman
 */
public interface SampleRestClientGroup extends PropertyGroup {
	
	StringProp REST_HOST = StringProp.builder().mustMatchRegex(".*\\.usgs\\.gov") .required().build();
	IntProp REST_PORT = IntProp.builder().required().mustBeGreaterThanOrEqualTo(80).mustBeLessThan(10000).build();
	StringProp REST_SERVICE_NAME = StringProp.builder().setDefault("query/").mustEndWith("/").build();
	StringProp AUTH_KEY = StringProp.builder().required().build();
	IntProp RETRY_COUNT = IntProp.builder().setDefault(2).build();
	FlagProp REQUEST_META_DATA = FlagProp.builder().setDefault(true).build();
	FlagProp REQUEST_SUMMARY_DATA = FlagProp.builder().build();
}
