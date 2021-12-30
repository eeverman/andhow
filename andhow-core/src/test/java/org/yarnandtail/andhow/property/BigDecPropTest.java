package org.yarnandtail.andhow.property;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.RequirementProblem.NonNullPropertyProblem;
import org.yarnandtail.andhow.internal.ValueProblem.InvalidValueProblem;
import org.yarnandtail.andhow.junit5.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests BigDecProp instances as they would be used in an app.
 * <p>
 * Focuses on builder functionality, validation, and metadata.
 *
 */
public class BigDecPropTest extends PropertyTestBase {
	private static final BigDecimal VALUE_1 = new BigDecimal("10.789");
	private static final BigDecimal GREATER_THAN_VALUE = new BigDecimal("101.123E+5");
	private static final BigDecimal GREATER_THAN_OR_EQUAL_VALUE = new BigDecimal("100.123456");
	private static final BigDecimal LESS_THAN_VALUE = GREATER_THAN_VALUE.negate();
	private static final BigDecimal LESS_THAN_VALUE_OR_EQUAL_VALUE = GREATER_THAN_OR_EQUAL_VALUE;
	private static final BigDecimal DEFAULT_VALUE = new BigDecimal("456.456");
	private static final BigDecimal JNDI_VALUE = new BigDecimal("777.777");
	private static final BigDecimal SYS_PROP_VALUE = new BigDecimal("-523.789");
	private static final String DESCRIPTION = "BigDecimal description";
	private static final String GREATER_THAN_JNDI_PATH = "org.yarnandtail.andhow.property.BigDecPropTest.BigDecGroup.GREATER_THAN";
	private static final String LESS_THAN_SYS_PROP_PATH = "org.yarnandtail.andhow.property.BigDecPropTest.BigDecGroup.LESS_THAN";

	@Test
	public void happyPathTest() {
		this.buildConfig(this, "_happyPath", BigDecGroup.class);
		assertEquals(VALUE_1, BigDecGroup.NOT_NULL.getValue());
		assertEquals(DESCRIPTION, BigDecGroup.NOT_NULL.getDescription());
		assertEquals(GREATER_THAN_VALUE, BigDecGroup.GREATER_THAN.getValue());
		assertEquals(GREATER_THAN_OR_EQUAL_VALUE, BigDecGroup.GREATER_THAN_OR_EQUAL.getValue());
		assertEquals(LESS_THAN_VALUE, BigDecGroup.LESS_THAN.getValue());
		assertEquals(LESS_THAN_VALUE_OR_EQUAL_VALUE, BigDecGroup.LESS_THAN_OR_EQUAL.getValue());
		assertEquals(new BigDecimal("789.789"), BigDecGroup.DEFAULT.getValue());
		assertNull(BigDecGroup.NULL.getValue());
	}

	@Test
	public void happyPathTest_DefaultValue() {
		this.buildConfig(this, "_default", BigDecGroup.class);
		assertEquals(DEFAULT_VALUE, BigDecGroup.DEFAULT.getDefaultValue());
	}

	@Test
	public void happyPathTest_Alias() {
		this.buildConfig(this, "_default", BigDecGroup.class);
		assertEquals(GREATER_THAN_VALUE, BigDecGroup.GREATER_THAN.getValue());
		assertEquals("alias", BigDecGroup.GREATER_THAN.getRequestedAliases().get(0).getActualName());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void happyPathTest_JndiProp() throws NamingException {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:");
		//

		jndi.bind("java:" + GREATER_THAN_JNDI_PATH, JNDI_VALUE);

		this.buildConfig(this, "_happyPath", BigDecGroup.class);
		assertEquals(JNDI_VALUE, BigDecGroup.GREATER_THAN.getValue());
	}

	@Test
	@RestoreSysPropsAfterThisTest
	public void happyPathTest_SystemProp() {
		System.setProperty(LESS_THAN_SYS_PROP_PATH, SYS_PROP_VALUE.toString());
		this.buildConfig(this, "_happyPath", BigDecGroup.class);
		assertEquals(SYS_PROP_VALUE, BigDecGroup.LESS_THAN.getValue());
	}

	@Test
	public void badPathTest_InvalidValues() {
		try {
			this.buildConfig(this, "_badPath", BigDecGroup.class);
			fail("Should throw exception!");
		} catch (AppFatalException e) {
			ProblemList<Problem> problems = e.getProblems();
			for (Problem problem : problems) {
				assertTrue(problem instanceof InvalidValueProblem);
			}
			assertEquals(4, problems.size());
		}
	}

	@Test
	public void badPathTest_NullProperty() {
		try {
			this.buildConfig(this, "_missingProps", BigDecGroup.class);
			fail("Should throw exception!");
		} catch (AppFatalException e) {
			ProblemList<Problem> problems = e.getProblems();
			assertEquals(1, problems.size());
			Problem problem = problems.get(0);
			assertTrue(problem instanceof NonNullPropertyProblem);
		}
	}

	public interface BigDecGroup {
		BigDecProp NOT_NULL = BigDecProp.builder().notNull().desc(DESCRIPTION).build();
		BigDecProp NULL = BigDecProp.builder().build();
		BigDecProp GREATER_THAN = BigDecProp.builder().mustBeGreaterThan(GREATER_THAN_OR_EQUAL_VALUE).aliasInAndOut("alias").build();
		BigDecProp GREATER_THAN_OR_EQUAL = BigDecProp.builder().mustBeGreaterThanOrEqualTo(GREATER_THAN_OR_EQUAL_VALUE).build();
		BigDecProp LESS_THAN = BigDecProp.builder().mustBeLessThan(GREATER_THAN_OR_EQUAL_VALUE).build();
		BigDecProp LESS_THAN_OR_EQUAL = BigDecProp.builder().mustBeLessThanOrEqualTo(LESS_THAN_VALUE_OR_EQUAL_VALUE).build();
		BigDecProp DEFAULT = BigDecProp.builder().defaultValue(DEFAULT_VALUE).build();
	}
}
