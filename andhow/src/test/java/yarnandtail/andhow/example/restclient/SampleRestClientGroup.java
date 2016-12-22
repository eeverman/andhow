package yarnandtail.andhow.example.restclient;

import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.property.FlagProp;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.PropertyGroup;

/**
 *
 * @author eeverman
 */
public interface SampleRestClientGroup extends PropertyGroup {
	
	StrProp APP_NAME = StrProp.builder().build();
	StrProp REST_HOST = StrProp.builder().mustMatchRegex(".*\\.usgs\\.gov") .required().build();
	IntProp REST_PORT = IntProp.builder().required().mustBeGreaterThanOrEqualTo(80).mustBeLessThan(10000).build();
	StrProp REST_SERVICE_NAME = StrProp.builder().defaultValue("query/").mustEndWith("/").build();
	StrProp AUTH_KEY = StrProp.builder().required().build();
	IntProp RETRY_COUNT = IntProp.builder().defaultValue(2).build();
	FlagProp REQUEST_META_DATA = FlagProp.builder().defaultValue(true).build();
	FlagProp REQUEST_SUMMARY_DATA = FlagProp.builder().build();
}
