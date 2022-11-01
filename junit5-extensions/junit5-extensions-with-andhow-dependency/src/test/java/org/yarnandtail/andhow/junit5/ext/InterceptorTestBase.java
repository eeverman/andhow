package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import java.lang.reflect.Method;

/**
 * This class shares a static var 'extensionContextDuringTest' which is the JUnit ExtensionContext
 * used during a test.  Multiple threads executing the test would break this, thus SAME_THREAD.
 */
@ExtendWith(ConfigFromFileBeforeAllTestsExtUsageTest.TestInterceptor.class)
public
class InterceptorTestBase {

	protected static ExtensionContext extensionContextDuringTest;

	/**
	 * This Interceptor should capture the ExtensionContext being used during the test and store it
	 * to the parent class's extensionContextDuringTest static var.  Within a @Test method annotated
	 * w/ @ExtendWith(TestInterceptor.class), the extensionContextDuringTest will contain the extension
	 * context of the test.
	 *
	 * Note that '@ExtendWith(TestInterceptor.class)' should be the first ExtendWith annotation to
	 * ensure it executes first.
	 */
	public static class TestInterceptor implements InvocationInterceptor {

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

}