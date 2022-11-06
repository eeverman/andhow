package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.Execution;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileBeforeThisTestExt;
import org.yarnandtail.andhow.junit5.ext.InterceptorTestBase;
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
@ConfigFromFileBeforeAllTests(filePath = "Conf1And2AsBob.properties", classesInScope = {Conf1.class, Conf2.class})
//@TestInstance(Lifecycle.PER_CLASS) // Need to try this
class ConfigFromFileMixedUsage2Test {

	@Order(1)
	@Test
	public void test1() {
		assertEquals("Bob", Conf1.MY_PROP.getValue());
		assertEquals("Bob", Conf1.Inner1.MY_PROP.getValue());
		assertEquals("Bob", Conf2.MY_PROP.getValue());
		assertEquals("Bob", Conf2.Inner1.MY_PROP.getValue());
	}

	@Order(2)
	@Test
	@ConfigFromFileBeforeThisTest(filePath = "Conf1OnlyAsDeb.properties", classesInScope = {Conf1.class})
	public void test2() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized());

		assertEquals("Deb", Conf1.MY_PROP.getValue());
		assertEquals("Deb", Conf1.Inner1.MY_PROP.getValue());
		assertThrows(RuntimeException.class, () -> Conf2.MY_PROP.getValue());
		assertThrows(RuntimeException.class, () -> Conf2.Inner1.MY_PROP.getValue());
	}

	@Order(3)
	@Test
	public void test3() {
		assertEquals("Bob", Conf1.MY_PROP.getValue());
		assertEquals("Bob", Conf1.Inner1.MY_PROP.getValue());
		assertEquals("Bob", Conf2.MY_PROP.getValue());
		assertEquals("Bob", Conf2.Inner1.MY_PROP.getValue());
	}

}