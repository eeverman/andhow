package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.parallel.Execution;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.ext.*;
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
@ConfigFromFileBeforeAllTests(filePath = "ext/MyPropFile.properties")
class ConfigFromFileMixedUsageTest extends InterceptorTestBase {

	private static Object coreFoundInTest1;

	@Order(1)
	@Test
	public void test1() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());

		AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileMixedUsageTest.Config.class);

		assertEquals("Bob", Config.MY_PROP.getValue());

		coreFoundInTest1 = AndHowTestUtils.getAndHowCore();	//Only non-null after value access above.
	}

	@Order(2)
	@Test
	public void test2() {
		assertTrue(AndHow.isInitialized());	// Single initialization for entire class
		assertSame(coreFoundInTest1, AndHowTestUtils.getAndHowCore());
		assertEquals("Bob", Config.MY_PROP.getValue());
	}

	@Order(3)
	@Test
	@ConfigFromFileBeforeThisTest(filePath = "ext/MyPropFile2.properties")
	public void test3() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());

		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileBeforeThisTestExt.class, (Class)(this.getClass()),
				getClass().getMethod("test3", null));

		ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
		assertNotNull(store);
		assertSame(coreFoundInTest1, store.get("core_key"),
				"The stored core should be the same one created via BeforeAll in test1 ");

		AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileMixedUsageTest.Config.class);

		assertEquals("Bob2", Config.MY_PROP.getValue());
	}

	@Order(4)
	@Test
	public void test4() throws NoSuchMethodException {
		assertTrue(AndHow.isInitialized());
		assertSame(coreFoundInTest1, AndHowTestUtils.getAndHowCore());
		assertEquals("Bob", Config.MY_PROP.getValue());
	}

	@Nested
	@Order(1)
	@ConfigFromFileBeforeAllTests(filePath = "ext/MyPropFileNest1.properties")
	class Nest1 {

		@Test
		public void test1() throws NoSuchMethodException {

			assertFalse(AndHow.isInitialized());

			ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
					ConfigFromFileBeforeAllTestsExt.class, (Class)(this.getClass()),
					getClass().getMethod("test1", null));

			ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
			assertNotNull(store);
			assertSame(coreFoundInTest1, store.get("core_key"),
					"The stored core should be the same one created via BeforeAll in test1 ");

			AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileMixedUsageTest.Config.class);

			assertEquals("BobNest1", Config.MY_PROP.getValue());
		}

		@Order(2)
		@Test
		public void test2() {
			assertTrue(AndHow.isInitialized());	// Single initialization for entire class
			assertEquals("BobNest1", Config.MY_PROP.getValue());
		}


		@Order(3)
		@Test
		@ConfigFromFileBeforeThisTest(filePath = "ext/MyPropFile2.properties")
		public void test3() throws NoSuchMethodException {

			assertFalse(AndHow.isInitialized());

			ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
					ConfigFromFileBeforeThisTestExt.class, (Class)(this.getClass()),
					getClass().getMethod("test2", null));

			ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
			assertNotNull(store);
			assertNotSame(coreFoundInTest1, store.get("core_key"));

			AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileMixedUsageTest.Config.class);

			assertEquals("Bob2", Config.MY_PROP.getValue());
		}

		@Order(4)
		@Test
		public void test4() {
			assertTrue(AndHow.isInitialized());	// Single initialization for entire class
			assertEquals("BobNest1", Config.MY_PROP.getValue());
		}
	}

	/** Simple config for use in this test */
	static interface Config {
		StrProp MY_PROP = StrProp.builder().aliasInAndOut("MY_PROP").build();
	}
}