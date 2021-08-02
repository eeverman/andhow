package org.yarnandtail.andhow.property;

import java.util.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author eeverman
 */
public class PropertyTestBase extends AndHowTestBase {
	
	
	public <T extends AndHowTestBase> String buildPropFilePath(T testClass, String propFileSuffix) {
		String testPkgName = testClass.getClass().getPackage().getName();
		String testClsName = testClass.getClass().getSimpleName();
		
		return "/" + testPkgName.replace(".", "/") + "/" + testClsName + 
				TextUtil.trimToEmpty(propFileSuffix) + ".properties";
	}
	
	public <T extends AndHowTestBase> void  buildConfig(T testClass, String propFileSuffix,
																											Class<?>... group) {
		
		String propFilePath = buildPropFilePath(testClass, propFileSuffix);
		List<Class<?>> groups = Arrays.asList(group);
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(TEST_CONFIG.class)
				.addOverrideGroups(groups)
				.addFixedValue(TEST_CONFIG.PROP_FILE, propFilePath)
				.setClasspathPropFilePath(TEST_CONFIG.PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);
		AndHow.instance();
	}
	
	public static interface TEST_CONFIG {
		StrProp PROP_FILE = StrProp.builder().mustBeNonNull().build();
	}
	

}
