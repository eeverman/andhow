package org.yarnandtail.andhow.load;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.AndHow;

import static org.yarnandtail.andhow.AndHowTestBase.reloader;

import org.yarnandtail.andhow.SimpleParams;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.internal.ConstructionProblem.LoaderPropertyNotRegistered;
import org.yarnandtail.andhow.internal.LoaderProblem.SourceNotFoundLoaderProblem;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.PropertyGroup;

/**
 * Just like the unit test version, but builds an entire AppConfig instance so
 * some of the higher-level errors can be tested
 * @author eeverman
 */
public class PropertyFileOnFilesystemLoaderAppTest {

	File tempPropertiesFile = null;
	
	public static interface TestProps extends PropertyGroup {
		StrProp FILEPATH = StrProp.builder().build();
	}
	

	@Before
	public void init() throws Exception {
		
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
	public void testHappyPath() throws Exception {
		AndHow.builder().namingStrategy(new CaseInsensitiveNaming())
				.cmdLineArg(AndHowUtil.getCanonicalName(TestProps.class, TestProps.FILEPATH), 
						tempPropertiesFile.getAbsolutePath())
				.loader(new PropertyFileOnFilesystemLoader(TestProps.FILEPATH))
				.group(SimpleParams.class)
				.group(TestProps.class)
				.reloadForNonPropduction(reloader);
		

		assertEquals(tempPropertiesFile.getAbsolutePath(), TestProps.FILEPATH.getValue());
		assertEquals("kvpBobValue", SimpleParams.STR_BOB.getValue());
		assertEquals("kvpNullValue", SimpleParams.STR_NULL.getValue());
		assertEquals(Boolean.FALSE, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(Boolean.TRUE, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(Boolean.TRUE, SimpleParams.FLAG_NULL.getValue());
	}
	
	@Test
	public void testUnregisteredPropLoaderProperty() throws Exception {
		
		try {
			AndHow.builder().namingStrategy(new CaseInsensitiveNaming())
					.cmdLineArg(AndHowUtil.getCanonicalName(TestProps.class, TestProps.FILEPATH), 
							tempPropertiesFile.getAbsolutePath())
					.loader(new PropertyFileOnFilesystemLoader(TestProps.FILEPATH))
					.group(SimpleParams.class)
					//.group(TestProps.class)	//This must be declared or the Prop loader can't work
					.reloadForNonPropduction(reloader);
		
			fail("The Property loader config parameter is not registered, so it should have failed");
		} catch (AppFatalException afe) {
			List<LoaderPropertyNotRegistered> probs = afe.getProblems().filter(LoaderPropertyNotRegistered.class);
			assertEquals(1, probs.size());
		}

	}
	
	/**
	 * It is not an error to not specify the file path property, it just means the loader
	 * will not find anything.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testUnspecifiedConfigParam() throws Exception {
		AndHow.builder().namingStrategy(new CaseInsensitiveNaming())
				.loader(new PropertyFileOnFilesystemLoader(TestProps.FILEPATH))
				.group(SimpleParams.class)
				.group(TestProps.class)
				.reloadForNonPropduction(reloader);
		
		//These are just default values
		assertEquals("bob", SimpleParams.STR_BOB.getValue());
		assertNull(SimpleParams.STR_NULL.getValue());
	}
	
	@Test
	public void testABadClasspathThatDoesNotPointToAFile() throws Exception {
		
		try {
			AndHow.builder().namingStrategy(new CaseInsensitiveNaming())
					.cmdLineArg(AndHowUtil.getCanonicalName(TestProps.class, TestProps.FILEPATH), 
							"asdfasdfasdf/asdfasdf/asdf")
					.loader(new PropertyFileOnFilesystemLoader(TestProps.FILEPATH))
					.group(SimpleParams.class)
					.group(TestProps.class)
					.reloadForNonPropduction(reloader);
			
			fail("The Property loader config property is not pointing to a real file location");
			
		} catch (AppFatalException afe) {
			List<SourceNotFoundLoaderProblem> probs = afe.getProblems().filter(SourceNotFoundLoaderProblem.class);
			assertEquals(1, probs.size());
		}
	}

}
