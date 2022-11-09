package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.internal.RequirementProblem;

import static org.junit.jupiter.api.Assertions.*;


@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ConfigFromFileBeforeAllTests(value = "Conf1And2AsBob.properties", includeClasses = {Conf1.class, Conf2.class})
class ConfigFromFileMixedUsage2Test {

	@Order(1)
	@Test
	public void test1() {
		assertFalse(AndHow.isInitialized(), "Shouldn't be init before 1st test");
		assertEquals("Bob", Conf1.MY_PROP.getValue());
		assertEquals("Bob", Conf1.Inner1.MY_PROP.getValue());
		assertEquals("Bob", Conf2.MY_PROP.getValue());
		assertEquals("Bob", Conf2.Inner1.MY_PROP.getValue());
	}

	@Order(2)
	@Test
	@ConfigFromFileBeforeThisTest(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class})
	public void test2() throws NoSuchMethodException {

		assertFalse(AndHow.isInitialized(), "Shouldn't be init bc this test forces a new config");

		assertEquals("Deb", Conf1.MY_PROP.getValue());
		assertEquals("Deb", Conf1.Inner1.MY_PROP.getValue());
		assertThrows(RuntimeException.class, () -> Conf2.MY_PROP.getValue());
		assertThrows(RuntimeException.class, () -> Conf2.Inner1.MY_PROP.getValue());
	}

	@Order(3)
	@Test
	@ConfigFromFileBeforeThisTest(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class, Conf2.class})
	public void puttingUnconfiguredRequiredPropertiesInScopeShouldError() {

		assertFalse(AndHow.isInitialized(), "Shouldn't be init bc this test forces a new config");

		AppFatalException e = assertThrows(AppFatalException.class, () -> Conf1.MY_PROP.getValue());

		assertEquals(1, e.getProblems().size());
		Problem p = e.getProblems().get(0);
		assertTrue(p instanceof RequirementProblem.NonNullPropertyProblem);
		RequirementProblem.NonNullPropertyProblem nnp = (RequirementProblem.NonNullPropertyProblem)p;
		assertSame(Conf2.Inner1.MY_PROP, nnp.getPropertyCoord().getProperty());
	}

	@Order(4)
	@Test
	public void test4() {

		assertTrue(AndHow.isInitialized(), "Should be init bc the config state should be restore to post-test1");

		assertEquals("Bob", Conf1.MY_PROP.getValue());
		assertEquals("Bob", Conf1.Inner1.MY_PROP.getValue());
		assertEquals("Bob", Conf2.MY_PROP.getValue());
		assertEquals("Bob", Conf2.Inner1.MY_PROP.getValue());
	}

}