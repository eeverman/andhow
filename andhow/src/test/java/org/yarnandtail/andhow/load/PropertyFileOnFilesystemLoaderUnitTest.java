package org.yarnandtail.andhow.load;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.SimpleParams;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.GlobalScopeConfigurationMutable;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.PropertyValuesWithContextMutable;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 *
 * @author eeverman
 */
public class PropertyFileOnFilesystemLoaderUnitTest {
	
	GlobalScopeConfigurationMutable appDef;
	PropertyValuesWithContextMutable appValuesBuilder;
	File tempPropertiesFile = null;
	
	public static interface TestProps extends PropertyGroup {
		StrProp FILEPATH = StrProp.builder().mustBeNonNull().build();
	}
	
	public interface SimpleParams {
		//Strings
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();

		//Flags
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
		FlagProp FLAG_NULL = FlagProp.builder().build();
	}
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new PropertyValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		appDef = new GlobalScopeConfigurationMutable(bns);
		
		GroupProxy simpleProxy = AndHowUtil.buildGroupProxy(SimpleParams.class);
		
		appDef.addProperty(AndHowUtil.buildGroupProxy(TestProps.class), TestProps.FILEPATH);

		
		appDef.addProperty(simpleProxy, SimpleParams.STR_BOB);
		appDef.addProperty(simpleProxy, SimpleParams.STR_NULL);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_NULL);
		
		//copy a properties file to a temp location
		URL inputUrl = getClass().getResource("/org/yarnandtail/andhow/load/SimpleParams1.properties");
		tempPropertiesFile = File.createTempFile("andhow_test", ".properties");
		tempPropertiesFile.deleteOnExit();
		FileUtils.copyURLToFile(inputUrl, tempPropertiesFile);

	}
	
	@After
	public void afterTest() {
		if (tempPropertiesFile != null) {
			tempPropertiesFile.delete();
		}
	}
	
	
	@Test
	public void testHappyPath() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.FILEPATH, tempPropertiesFile.getAbsolutePath()));
		LoaderValues existing = new LoaderValues(new StringArgumentLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnFilesystemLoader pfl = new PropertyFileOnFilesystemLoader(TestProps.FILEPATH);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertEquals("kvpBobValue", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("kvpNullValue", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testPropFileLoaderWithMissingFile() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.FILEPATH, "/org/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new StringArgumentLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnFilesystemLoader pfl = new PropertyFileOnFilesystemLoader(TestProps.FILEPATH);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(1, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.SourceNotFoundLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	/**
	 * The loader itself is OK w/ not having its parameter specified - it just
	 * ignores.
	 */
	@Test
	public void testPropFileLoaderWithNoClasspathConfigured() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		//evl.add(new PropertyValue(TestProps.FILEPATH, "/org/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new StringArgumentLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnFilesystemLoader pfl = new PropertyFileOnFilesystemLoader(TestProps.FILEPATH);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
	}

}
