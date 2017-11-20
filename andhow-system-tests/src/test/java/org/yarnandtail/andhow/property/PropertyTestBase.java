package org.yarnandtail.andhow.property;

import java.util.*;
import org.yarnandtail.andhow.*;
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
			Class<?>... group) {
		
		String propFilePath = buildPropFilePath(testClass, propFileSuffix);
		List<Class<?>> groups = Arrays.asList(group);
				
		FixedValueLoader fvl = new FixedValueLoader();
		fvl.setPropertyValues(new PropertyValue(TEST_CONFIG.PROP_FILE, propFilePath));
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader();
		pfl.setFilePath(TEST_CONFIG.PROP_FILE);
		pfl.setMissingFileAProblem(true);
		
		AndHowNonProduction.builder()
				.group(TEST_CONFIG.class)
				.groups(groups)
				.loader(fvl)
				.loader(pfl)
				.build();

	}
	
	public static interface TEST_CONFIG extends PropertyGroup {
		StrProp PROP_FILE = StrProp.builder().mustBeNonNull().build();
	}
	

}
