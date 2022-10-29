package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.parallel.Execution;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileExt;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

/**
 * This class shares a static var 'extensionContextDuringTest' which is the JUnit ExtensionContext
 * used during a test.  Multiple threads executing the test would break this, thus SAME_THREAD.
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(SAME_THREAD)
class ConfigFromFileUsageTest {

	protected static ExtensionContext extensionContextDuringTest;

	@Order(1)
	@Test
	@ExtendWith(TestInterceptor.class)
	@ConfigFromFile(filePath = "ext/MyPropFile.properties")
	public void test1() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());
		assertNotNull(extensionContextDuringTest);

		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileExt.class, (Class)(this.getClass()),
				getClass().getMethod("test1", null));

		ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
		assertNotNull(store);

		AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileUsageTest.Config.class);


		assertEquals("Bob", Config.MY_PROP.getValue());
	}

	@Order(2)
	@Test
	@ExtendWith(TestInterceptor.class)
	@ConfigFromFile(filePath = "ext/MyPropFile.properties")
	public void test2() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());
		assertNotNull(extensionContextDuringTest);

		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileExt.class, (Class)(this.getClass()),
				getClass().getMethod("test2", null));

		ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
		assertNotNull(store);

		AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileUsageTest.Config.class);


		assertEquals("Bob", Config.MY_PROP.getValue());
	}

	@Nested
	@Order(1)
	@ConfigFromFile(filePath = "ext/MyPropFileNest1.properties")
	class Nest1 {

		@Test
		@ExtendWith(TestInterceptor.class)
		public void test1() throws NoSuchMethodException {

			assertFalse(AndHow.isInitialized());
			assertNotNull(extensionContextDuringTest);

			ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
					ConfigFromFileExt.class, (Class)(this.getClass()),
					getClass().getMethod("test1", null));

			ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
			assertNotNull(store);

			AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileUsageTest.Config.class);

			assertEquals("BobNest1", Config.MY_PROP.getValue());
		}
	}

	@Nested
	@Order(2)
	@ConfigFromFile(filePath = "ext/MyPropFileNest2.properties")
	class Nest2 {

		@Test
		@ExtendWith(TestInterceptor.class)
		public void test1() throws NoSuchMethodException {

			assertFalse(AndHow.isInitialized());
			assertNotNull(extensionContextDuringTest);

			ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
					ConfigFromFileExt.class, (Class)(this.getClass()),
					getClass().getMethod("test1", null));

			ExtensionContext.Store store = extensionContextDuringTest.getStore(expectedNamespace);
			assertNotNull(store);

			AndHowTestUtils.setConfigurationOverrideGroups(AndHow.findConfig(), ConfigFromFileUsageTest.Config.class);

			assertEquals("BobNest2", Config.MY_PROP.getValue());
		}
	}

	/**
	 * This Interceptor should capture the ExtensionContext being used during the test and store it
	 * to the parent class's extensionContextDuringTest static var.  Within a @Test method annotated
	 * w/ @ExtendWith(TestInterceptor.class), the extensionContextDuringTest will contain the extension
	 * context of the test.
	 *
	 * Note that '@ExtendWith(TestInterceptor.class)' should be the first ExtendWith annotation to
	 * ensure it executes first.
	 */
	static class TestInterceptor implements InvocationInterceptor {

		@Override
		public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {

			Throwable throwable = null;

			try {
				extensionContextDuringTest = extensionContext;
				invocation.proceed();
				extensionContextDuringTest = null;
			} catch (Throwable t) {
				throwable = t;
			}

			if (throwable != null) {
				throw throwable;
			}
		}

	}

	/** Simple config for use in this test */
	static interface Config {
		StrProp MY_PROP = StrProp.builder().aliasInAndOut("MY_PROP").build();
	}
}