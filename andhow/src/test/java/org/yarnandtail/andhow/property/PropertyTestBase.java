package org.yarnandtail.andhow.property;

import java.util.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.*;

import static org.yarnandtail.andhow.AndHowTestBase.reloader;

import org.yarnandtail.andhow.load.*;
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
			Class<? extends BasePropertyGroup>... group) {
		
		String propFilePath = buildPropFilePath(testClass, propFileSuffix);
		List<Class<? extends BasePropertyGroup>> groups = Arrays.asList(group);
				
		AndHow.builder()
				.group(TEST_CONFIG.class)
				.groups(groups)
				.loader(new FixedValueLoader(new PropertyValue(TEST_CONFIG.PROP_FILE, propFilePath)))
				.loader(new PropertyFileOnClasspathLoader(TEST_CONFIG.PROP_FILE))
				.reloadForNonPropduction(reloader);

	}
	
	public static interface TEST_CONFIG extends PropertyGroup {
		StrProp PROP_FILE = StrProp.builder().mustBeNonNull().build();
	}
	

}
