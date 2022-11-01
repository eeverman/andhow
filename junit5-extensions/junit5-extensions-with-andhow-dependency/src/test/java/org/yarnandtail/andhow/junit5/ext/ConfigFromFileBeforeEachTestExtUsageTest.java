package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.parallel.Execution;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

/**
 * This class shares a static var 'extensionContextDuringTest' which is the JUnit ExtensionContext
 * used during a test.  Multiple threads executing the test would break this, thus SAME_THREAD.
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(SAME_THREAD)
class ConfigFromFileBeforeEachTestExtUsageTest extends InterceptorTestBase {

	@RegisterExtension
	static ConfigFromFileBeforeEachTestExt _configFromFileBaseExt = new ConfigFromFileBeforeEachTestExt("MyPropFile.properties");

	@Order(1)
	@Test
	public void test1() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());
		assertNotNull(extensionContextDuringTest);

		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileBeforeEachTestExt.class, (Class)(this.getClass()),
				getClass().getMethod("test1", null));

		ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
		assertNotNull(store);

		AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileBeforeEachTestExtUsageTest.Config.class);

		assertEquals("Bob", Config.MY_PROP.getValue());
	}

	// test2 is the same as test1 - They should each have a newly configured state
	// based on "MyPropFile.properties"
	@Order(2)
	@Test
	public void test2() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());
		assertNotNull(extensionContextDuringTest);

		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileBeforeEachTestExt.class, (Class)(this.getClass()),
				getClass().getMethod("test1", null));

		ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
		assertNotNull(store);

		AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileBeforeEachTestExtUsageTest.Config.class);

		assertEquals("Bob", Config.MY_PROP.getValue());
	}

	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@Order(1)
	class Nest1 {

		// The nested class inherits the registered ConfigFromFileBeforeEachTestExt
		// extension and it is invoked again, newly creating configuration based on
		// "MyPropFile.properties".
		@Test
		@Order(1)
		@ExtendWith(TestInterceptor.class)
		public void test1() throws NoSuchMethodException {

			assertFalse(AndHow.isInitialized());
			assertNotNull(extensionContextDuringTest);

			ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
					ConfigFromFileBeforeEachTestExt.class, (Class)(this.getClass()),
					getClass().getMethod("test1", null));

			ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
			assertNotNull(store);

			AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileBeforeEachTestExtUsageTest.Config.class);

			assertEquals("Bob", Config.MY_PROP.getValue());

		}
	}

	/** Simple config for use in this test */
	static interface Config {
		StrProp MY_PROP = StrProp.builder().aliasInAndOut("MY_PROP").build();
	}
}