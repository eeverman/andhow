package org.yarnandtail.andhow.load;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.internal.ConstructionProblem.LoaderPropertyNotRegistered;
import org.yarnandtail.andhow.internal.LoaderProblem.SourceNotFoundLoaderProblem;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.NameUtil;

/**
 * Just like the unit test version, but builds an entire AppConfig instance so
 * some of the higher-level errors can be tested
 * @author eeverman
 */
public class PropFileOnClasspathLoaderAppTest extends AndHowCoreTestBase {
	
	public static interface TestProps {
		StrProp CLAZZ_PATH = StrProp.builder().build();
	}
	
	@Test
	public void testHappyPath() throws Exception {
		
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH), 
						"/org/yarnandtail/andhow/load/SimpleParams1.properties")
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.classpathPropertiesRequired()
				.group(SimpleParams.class)
				.group(TestProps.class);
		
		AndHow.build(config);
		

		assertEquals("/org/yarnandtail/andhow/load/SimpleParams1.properties", TestProps.CLAZZ_PATH.getValue());
		assertEquals("kvpBobValue", SimpleParams.STR_BOB.getValue());
		assertEquals("kvpNullValue", SimpleParams.STR_NULL.getValue());
		assertEquals(Boolean.FALSE, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(Boolean.TRUE, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(Boolean.TRUE, SimpleParams.FLAG_NULL.getValue());
	}
	
	@Test
	public void testInvalid() throws Exception {
		
		try {
			AndHowConfiguration config = AndHowCoreTestConfig.instance()
					.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH), 
							"/org/yarnandtail/andhow/load/SimpleParamsInvalid.properties")
					.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
					.classpathPropertiesRequired()
					.group(SimpleParams.class)
					.group(TestProps.class);
		
			AndHow.build(config);
		
				fail("The property file contains several invalid props - should have errored");
		} catch (AppFatalException afe) {
			List<Problem> probs = afe.getProblems();
			assertEquals(5, probs.size());
		}

	}
	
	@Test
	public void testNullReferencePropLoaderProperty() throws Exception {
		
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.group(SimpleParams.class)
				.group(TestProps.class);	//This must be declared or the Prop loader can't work
		
		AndHow.build(config);	

		
		//It is OK to have a null config for the PropFile loader - it just turns it off
	}
	
	@Test
	public void testUnregisteredPropLoaderProperty() throws Exception {
		
		try {
			AndHowConfiguration config = AndHowCoreTestConfig.instance()
					.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH), 
							"/org/yarnandtail/andhow/load/SimpleParams1.properties")
					.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
					.group(SimpleParams.class);
					//.group(TestProps.class) //Missing - should cause failure
			AndHow.build(config);
		
			fail("The Property loader config parameter is not registered, so it should have failed");
		} catch (AppFatalException afe) {
			List<LoaderPropertyNotRegistered> probs = afe.getProblems().filter(LoaderPropertyNotRegistered.class);
			assertEquals(1, probs.size());
		}
	}
	
	/**
	 * It is not an error to not specify the classpath param, it just means the loader
	 * will not find anything.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testUnspecifiedConfigParam() throws Exception {
		
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.classpathPropertiesRequired()
				.group(SimpleParams.class)
				.group(TestProps.class);
		
		AndHow.build(config);
		
		//These are just default values
		assertEquals("bob", SimpleParams.STR_BOB.getValue());
		assertNull(SimpleParams.STR_NULL.getValue());
	}
	
	@Test
	public void testABadClasspathThatDoesNotPointToAFile() throws Exception {
		
		try {
			AndHowConfiguration config = AndHowCoreTestConfig.instance()
					.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH), 
							"asdfasdfasdf/asdfasdf/asdf")
					.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
					.classpathPropertiesRequired()
					.group(SimpleParams.class)
					.group(TestProps.class);
		
			AndHow.build(config);
			
			fail("The Property loader config property is not pointing to a real file location");
			
		} catch (AppFatalException afe) {
			List<SourceNotFoundLoaderProblem> probs = afe.getProblems().filter(SourceNotFoundLoaderProblem.class);
			assertEquals(1, probs.size());
		}
	}

}
