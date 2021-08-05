package org.yarnandtail.andhow.load;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
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
public class PropFileOnClasspathLoaderAppTest extends AndHowTestBase {
	
	public static interface TestProps {
		StrProp CLAZZ_PATH = StrProp.builder().build();
	}
	
	@Test
	public void testHappyPath() throws Exception {
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH), 
						"/org/yarnandtail/andhow/load/SimpleParams1.properties")
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.classpathPropertiesRequired()
				.addOverrideGroup(SimpleParams.class)
				.addOverrideGroup(TestProps.class);
		
		AndHow.setConfig(config);

		assertEquals("/org/yarnandtail/andhow/load/SimpleParams1.properties", TestProps.CLAZZ_PATH.getValue());
		assertEquals("kvpBobValue", SimpleParams.STR_BOB.getValue());
		assertEquals("kvpNullValue", SimpleParams.STR_NULL.getValue());
		assertEquals(Boolean.FALSE, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(Boolean.TRUE, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(Boolean.TRUE, SimpleParams.FLAG_NULL.getValue());
	}
	
	@Test
	public void testInvalid() throws Exception {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH),
						"/org/yarnandtail/andhow/load/SimpleParamsInvalid.properties")
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.classpathPropertiesRequired()
				.addOverrideGroup(SimpleParams.class)
				.addOverrideGroup(TestProps.class);

		AndHow.setConfig(config);

		AppFatalException afe = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<Problem> probs = afe.getProblems();
		assertEquals(5, probs.size());

	}
	
	@Test
	public void testNullReferencePropLoaderProperty() throws Exception {
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class)
				.addOverrideGroup(TestProps.class);	//This must be declared or the Prop loader can't work

		AndHow.setConfig(config);
		AndHow.instance();
		
		//It is OK to have a null config for the PropFile loader - it just turns it off
	}
	
	@Test
	public void testUnregisteredPropLoaderProperty() throws Exception {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH),
						"/org/yarnandtail/andhow/load/SimpleParams1.properties")
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.addOverrideGroup(SimpleParams.class);
				//.group(TestProps.class) //Missing - should cause failure

		AndHow.setConfig(config);

		AppFatalException afe = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<LoaderPropertyNotRegistered> probs = afe.getProblems().filter(LoaderPropertyNotRegistered.class);
		assertEquals(1, probs.size());
	}
	
	/**
	 * It is not an error to not specify the classpath param, it just means the loader
	 * will not find anything.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testUnspecifiedConfigParam() throws Exception {
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.classpathPropertiesRequired()
				.addOverrideGroup(SimpleParams.class)
				.addOverrideGroup(TestProps.class);
		
		AndHow.setConfig(config);
		
		//These are just default values
		assertEquals("bob", SimpleParams.STR_BOB.getValue());
		assertNull(SimpleParams.STR_NULL.getValue());
	}
	
	@Test
	public void testABadClasspathThatDoesNotPointToAFile() throws Exception {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(NameUtil.getAndHowName(TestProps.class, TestProps.CLAZZ_PATH),
						"asdfasdfasdf/asdfasdf/asdf")
				.setClasspathPropFilePath(TestProps.CLAZZ_PATH)
				.classpathPropertiesRequired()
				.addOverrideGroup(SimpleParams.class)
				.addOverrideGroup(TestProps.class);

		AndHow.setConfig(config);

		AppFatalException afe = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<SourceNotFoundLoaderProblem> probs = afe.getProblems().filter(SourceNotFoundLoaderProblem.class);
		assertEquals(1, probs.size());
	}

}
