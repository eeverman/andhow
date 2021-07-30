package org.yarnandtail.andhow;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.EffectiveName;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.load.KeyValuePairLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import static org.yarnandtail.andhow.internal.ConstructionProblem.InitiationLoopException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test is a minimal unit teat because this class is not really testable
 * as a unit test.
 * <br>
 * See the AndHowSystem Test sub-project for better test coverage.
 *
 * When the AndHow.instance(config) method is removed, this test will need to be
 * updated to call the private AndHow.initialize(config).
 * 
 * @author ericeverman
 */
public class AndHowTest extends AndHowCoreTestBase {
	

	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	CaseInsensitiveNaming basicNaming = new CaseInsensitiveNaming();
	ArrayList<Class<?>> configPtGroups = new ArrayList();
	Map<Property<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	
	public static interface RequiredParams {
		StrProp STR_BOB_R = StrProp.builder().defaultValue("Bob").mustBeNonNull().build();
		StrProp STR_NULL_R = StrProp.builder().mustBeNonNull().mustStartWith("XYZ").build();
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).mustBeNonNull().build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).mustBeNonNull().build();
		FlagProp FLAG_NULL = FlagProp.builder().mustBeNonNull().build();
	}
	
	@BeforeEach
	public void setup() throws Exception {
		
		configPtGroups.clear();
		configPtGroups.add(SimpleParams.class);
		
		startVals.clear();
		startVals.put(SimpleParams.STR_BOB, "test");
		startVals.put(SimpleParams.STR_NULL, "not_null");
		startVals.put(SimpleParams.FLAG_TRUE, Boolean.FALSE);
		startVals.put(SimpleParams.FLAG_FALSE, Boolean.TRUE);
		startVals.put(SimpleParams.FLAG_NULL, Boolean.TRUE);
		
		cmdLineArgsWFullClassName = new String[] {
			paramFullPath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test",
			paramFullPath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null",
			paramFullPath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false",
			paramFullPath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true",
			paramFullPath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true"
		};
		
	}

	@Test
	public void testTheTest() {
		//This could be generalized to use the class.getCanonicalName(),
		//but this one place we make it explicit
		assertEquals("org.yarnandtail.andhow.SimpleParams.", paramFullPath);
	}

	@Test
	public void findConfigShouldReturnSameInstanceEachTimeAndNotCauseInitialization() {
		AndHowConfiguration<? extends AndHowConfiguration> config1 = AndHow.findConfig();
		AndHowConfiguration<? extends AndHowConfiguration> config2 = AndHow.findConfig();
		assertSame(config1, config2, "Should return the same instance each time");
		assertFalse(AndHow.isInitialized(), "findConfig should not force initialization");
	}

	@Test
	public void findConfigShouldThrowExceptionIfCalledAfterInitialization() {
		AndHow.instance();
		assertThrows(AppFatalException.class, () -> AndHow.findConfig());
	}

	@Test
	public void findConfigLocatorShouldBeUsedIfNonNullAndOnlyOnFirstFindConfigCall() {

		final AtomicInteger callCount = new AtomicInteger();
		final StdConfig.StdConfigImpl config = StdConfig.instance();

		//This locator should be called when AndHow.findConfig() is called to locate a config.
		//After the first invocation, the config is returned w/o calling the locator.
		AndHowTestUtil.setAndHowConfigLocator((c) -> {
				callCount.incrementAndGet();
				return config;
		});

		assertSame(config, AndHow.findConfig(), "Should return the instance from our locator");
		assertSame(config, AndHow.findConfig(), "Should return the instance from our locator");
		assertEquals(1, callCount.get(), "Should only be called the first time");
		assertNotNull(AndHow.instance());		//as normal
	}

	@Test
	public void findConfigShouldReturnNewConfigIfReentrant() {

		//This locator will be called when AndHow.findConfig() is called, creating a loop.
		//This loop is normal and expected and should be handled correctly.
		//When detected, AndHow should just return a new AndHowConfiguration instance
		//rather than have a stack overflow.
		AndHowTestUtil.setAndHowConfigLocator(
				c -> AndHow.findConfig()
		);

		assertNotNull(AndHow.findConfig());	//should be stockoverflow free
		assertNotNull(AndHow.instance());		//as normal
		AndHow.instance();
	}

	@Test
	public void setConfigShouldReplaceConfig() {
		AndHowConfiguration<? extends AndHowConfiguration> config1 = AndHow.findConfig();
		AndHowConfiguration<? extends AndHowConfiguration> config2 = StdConfig.instance();

		assertNotSame(config1, config2);

		AndHow.setConfig(config2);	//set to config2
		assertSame(config2, AndHow.findConfig());

		AndHow.setConfig(config1);	//set back to config1
		assertSame(config1, AndHow.findConfig());
	}

	@Test
	public void setConfigShouldThrowAppFatalSometimes() {
		AndHowConfiguration<? extends AndHowConfiguration> config = StdConfig.instance();

		assertThrows(AppFatalException.class, () -> AndHow.setConfig(null),
				"Cannot set to null");

		AndHow.instance();	//force initialize

		assertThrows(AppFatalException.class, () -> AndHow.setConfig(config),
				"Cannot access after init");
	}

	@Test
	public void setConfigShouldThrowAnExceptionIfCalledFromWithinFindConfig() {

		//This situation could happen in application code's implementation of
		//AndHowInit.getConfiguration().  AndHow.findConfig() was invoked to find configuration,
		//which calls the app code getConfiguration() and in that method implementation
		//setConfig() is called.  Its bad.  Not clear what it would mean.  Needs to throw an error.

		final StdConfig.StdConfigImpl config = StdConfig.instance();

		//This locator should be called when AndHow.findConfig() is called.
		AndHowTestUtil.setAndHowConfigLocator((c) -> {
			AndHow.setConfig(config);	//Bad!  Must throw error!
			return config;
		});

		assertThrows(AppFatalException.class, () -> AndHow.findConfig());
	}

	@Test
	public void callingInstanceShouldReturnTheSameInstance() {

		AndHow ah1 =  AndHow.instance();
		AndHow ah2 =  AndHow.instance();

		assertSame(ah1, ah2, "Very important that all calls to this method return the same");
	}

	@Test
	public void callingInstanceWithConfigShouldFailIfAlreadyInitialized() {
		AndHowConfiguration<? extends AndHowConfiguration> config1 = AndHow.findConfig();

		AndHow.instance(config1);

		assertThrows(AppFatalException.class, () -> AndHow.instance(config1));
	}

	@Test
	public void initializedMethodShouldAgreeWithNormalInitializationProcess() {
		assertNull(AndHowTestUtil.getAndHowInstance());
		assertFalse(AndHow.isInitialized());
		assertFalse(AndHow.isInitialize(), "deprecated, but still tested");
		assertNotNull(AndHow.instance());
		assertNotNull(AndHowTestUtil.getAndHowInstance());
		assertTrue(AndHow.isInitialized());
		assertTrue(AndHow.isInitialize(), "deprecated, but still tested");
	}

	/**
	 * During testing, reflection utilities may 'kill' the core to simulate different
	 * config states, leaving the AndHow singleton alone so app ref's to it are not
	 * broken.  This test ensures that works properly.
	 */
	@Test
	public void initializedMethodShouldAgreeWithSmashedCoreForTestingInitializationProcess() {
		AndHow orgInstance = AndHow.instance();
		AndHowCore orgCore = AndHowTestUtil.setAndHowCore(null);

		assertNotNull(AndHowTestUtil.getAndHowInstance(), "The instance should still exist");
		assertFalse(AndHow.isInitialized(), "Core is null, so considered uninitialized");

		AndHow afterInstance = AndHow.instance();	//should work as normal
		AndHowCore afterCore = AndHowTestUtil.getAndHowCore();

		assertSame(orgInstance, afterInstance,
				"'Same' is the reason tests kill the core, not the singleton");
		assertNotSame(orgCore, afterCore,
				"The core is new, not the singleton");
		assertTrue(AndHow.isInitialized());
	}

	@Test
	public void attemptingToInitializeDuringInitializationShouldBeBlocked() {
		AndHowTestConfig.AndHowTestConfigImpl config1 = AndHowTestConfig.instance();
		AndHowTestConfig.AndHowTestConfigImpl config2 = AndHowTestConfig.instance();

		config1.setGetNamingStrategyCallback(() -> {
			AndHow.instance(config2);	//Try to initialize again when config.getNamingStrategy is called
			return null;
		});

		AppFatalException ex = assertThrows(AppFatalException.class, () -> AndHow.instance(config1));

		assertEquals(1, ex.getProblems().size());
		assertTrue(ex.getProblems().get(0) instanceof InitiationLoopException);
		InitiationLoopException initLoopEx = (InitiationLoopException)ex.getProblems().get(0);
		assertSame(config1, initLoopEx.getOriginalInit().getConfig());
		assertSame(config2, initLoopEx.getSecondInit().getConfig());
	}
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.groups(configPtGroups)
				.setCmdLineArgs(cmdLineArgsWFullClassName);
		
		AndHow.instance(config);
		
		assertTrue(AndHow.getInitializationTrace().length > 0);
		//STR_BOB (Set to 'test')
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("test", AndHow.instance().getValue(SimpleParams.STR_BOB));
		assertEquals("test", AndHow.instance().getExplicitValue(SimpleParams.STR_BOB));
		assertTrue(AndHow.instance().isExplicitlySet(SimpleParams.STR_BOB));
		List<EffectiveName> sbAliases = AndHow.instance().getAliases(SimpleParams.STR_BOB);
		assertEquals("STRING_BOB", sbAliases.get(0).getEffectiveInName());
		assertEquals("Stringy.Bob", sbAliases.get(1).getEffectiveOutName());
		assertEquals("org.yarnandtail.andhow.SimpleParams.STR_BOB", AndHow.instance().getCanonicalName(SimpleParams.STR_BOB));
		assertEquals(SimpleParams.class, AndHow.instance().getGroupForProperty(SimpleParams.STR_BOB).getProxiedGroup());
		assertTrue(AndHow.instance().getNamingStrategy() instanceof CaseInsensitiveNaming);

		//INT_TEN (defaults to 10 and not explicitly set)
		assertEquals(10, AndHow.instance().getValue(SimpleParams.INT_TEN));
		assertNull(AndHow.instance().getExplicitValue(SimpleParams.INT_TEN));
		assertFalse(AndHow.instance().isExplicitlySet(SimpleParams.INT_TEN));

		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(10, SimpleParams.INT_TEN.getValue());
		assertNull(SimpleParams.INT_NULL.getValue());
		assertEquals(10, SimpleParams.LNG_TEN.getValue());
		assertNull(SimpleParams.LNG_NULL.getValue());
		assertEquals(10, SimpleParams.DBL_TEN.getValue());
		assertNull(SimpleParams.DBL_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-10-01T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertNull(SimpleParams.LDT_NULL.getValue());

	}
	
	/**
	 * This is really testing how the NonProductionConfig works - how can this be
	 * targeted to the init config?
	 */
	@Test
	public void testBlowingUpWithDuplicateLoaders() {
		
		KeyValuePairLoader kvpl = new KeyValuePairLoader();
		kvpl.setKeyValuePairs(cmdLineArgsWFullClassName);
		
		try {

			AndHowConfiguration config = AndHowTestConfig.instance()
				.setLoaders(kvpl, kvpl)
				.groups(configPtGroups);

			AndHow.setConfig(config);
			AndHow.instance();
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(ConstructionProblem.class).size());
			assertTrue(ce.getProblems().filter(ConstructionProblem.class).get(0) instanceof ConstructionProblem.DuplicateLoader);
			
			ConstructionProblem.DuplicateLoader dl = (ConstructionProblem.DuplicateLoader)ce.getProblems().filter(ConstructionProblem.class).get(0);
			assertEquals(kvpl, dl.getLoader());
			assertTrue(ce.getSampleDirectory().length() > 0);
			
			File sampleDir = new File(ce.getSampleDirectory());
			assertTrue(sampleDir.exists());
			assertTrue(sampleDir.listFiles().length > 0);
		}
	}
	
	@Test
	public void testCmdLineLoaderMissingRequiredParamShouldThrowAConfigException() {
		
		try {
				AndHowConfiguration config = AndHowTestConfig.instance()
					.groups(configPtGroups)
					.group(RequiredParams.class)
					.setCmdLineArgs(cmdLineArgsWFullClassName);
				
				AndHow.instance(config);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(RequirementProblem.class).size());
			assertEquals(RequiredParams.STR_NULL_R, ce.getProblems().filter(RequirementProblem.class).get(0).getPropertyCoord().getProperty());
		}
	}
	
	@Test
	public void testInvalidValuesShouldCauseValidationException() {
		
		String baseName = AndHowTest.class.getCanonicalName();
		baseName += "." + RequiredParams.class.getSimpleName() + ".";
		
		try {
				AndHowConfiguration config = AndHowTestConfig.instance()
					.group(RequiredParams.class)
					.addCmdLineArg(baseName + "STR_NULL_R", "zzz")
					.addCmdLineArg(baseName + "FLAG_NULL", "present");
				
				AndHow.instance(config);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(ValueProblem.class).size());
			assertEquals(RequiredParams.STR_NULL_R, ce.getProblems().filter(ValueProblem.class).get(0).getBadValueCoord().getProperty());
		}
	}

	@Test
	public void testInitializationClass() {
		Long startTime = System.currentTimeMillis();
		StdConfig.StdConfigImpl config = StdConfig.instance();

		AndHow.Initialization init = new AndHow.Initialization(config);

		Long endTime = System.currentTimeMillis();

		assertEquals(this.getClass().getName(), init.getStackTrace()[0].getClassName());
		assertEquals("testInitializationClass", init.getStackTrace()[0].getMethodName(),
				"The stack trace should go back 1 level to ignore the construction of Initialization");
		assertSame(config, init.getConfig());
		assertTrue(startTime <= init.getTimeStamp());
		assertTrue(endTime >= init.getTimeStamp());
	}
	
}
