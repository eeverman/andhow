package org.yarnandtail.andhow.property;

import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.internal.RequirementProblem.NonNullPropertyProblem;
import org.yarnandtail.andhow.internal.ValueProblem.InvalidValueProblem;

import javax.naming.NamingException;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests BigDecProp instances as they would be used in an app.
 *
 * Focuses on builder functionality, validation, and metadata.
 *
 * @author chace86
 */
public class BigDecPropTest extends PropertyTestBase {
    private static final BigDecimal VALUE_1 = new BigDecimal("10.789");
    private static final BigDecimal VALUE_2 = new BigDecimal("101.123");
    private static final BigDecimal VALUE_3 = new BigDecimal("100.123456");
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
        assertEquals(VALUE_2, BigDecGroup.GREATER_THAN.getValue());
        assertEquals(VALUE_3, BigDecGroup.GREATER_THAN_OR_EQUAL.getValue());
        assertEquals(VALUE_2.negate(), BigDecGroup.LESS_THAN.getValue());
        assertEquals(VALUE_3, BigDecGroup.LESS_THAN_OR_EQUAL.getValue());
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
        assertEquals(VALUE_2, BigDecGroup.GREATER_THAN.getValue());
        assertEquals("alias", BigDecGroup.GREATER_THAN.getRequestedAliases().get(0).getActualName());
    }

    @Test
    public void happyPathTest_JndiProp() throws NamingException {
        SimpleNamingContextBuilder jndi = getJndi();
        jndi.bind("java:" + GREATER_THAN_JNDI_PATH, JNDI_VALUE);
        jndi.activate();
        this.buildConfig(this, "_happyPath", BigDecGroup.class);
        assertEquals(JNDI_VALUE, BigDecGroup.GREATER_THAN.getValue());
    }

    @Test
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
        } catch(AppFatalException e) {
            ProblemList<Problem> problems = e.getProblems();
            List<String> descriptions = new ArrayList<>();
            for (Problem problem : problems) {
                assertTrue(problem instanceof InvalidValueProblem);
                descriptions.add(problem.getProblemDescription());
            }
            assertEquals(4, problems.size());
            assertEquals("The value '" + VALUE_2.negate() + "' must be greater than " + VALUE_3, descriptions.get(0));
            assertEquals("The value '" + VALUE_3.negate() + "' must be greater than or equal to " + VALUE_3, descriptions.get(1));
            assertEquals("The value '" + VALUE_2 + "' must be less than " + VALUE_3, descriptions.get(2));
            assertEquals("The value '" + VALUE_3.add(BigDecimal.ONE) + "' must be less than or equal to " + VALUE_3, descriptions.get(3));
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
            assertEquals("This Property must be non-null - It must have a non-null default or be loaded by one of the loaders to a non-null value", problem.getProblemDescription());
        }
    }

    public interface BigDecGroup {
        BigDecProp NOT_NULL = BigDecProp.builder().mustBeNonNull().desc(DESCRIPTION).build();
        BigDecProp NULL = BigDecProp.builder().build();
        BigDecProp GREATER_THAN = BigDecProp.builder().mustBeGreaterThan(VALUE_3).aliasInAndOut("alias").build();
        BigDecProp GREATER_THAN_OR_EQUAL = BigDecProp.builder().mustBeGreaterThanOrEqualTo(VALUE_3).build();
        BigDecProp LESS_THAN = BigDecProp.builder().mustBeLessThan(VALUE_3).build();
        BigDecProp LESS_THAN_OR_EQUAL = BigDecProp.builder().mustBeLessThanOrEqualTo(VALUE_3).build();
        BigDecProp DEFAULT = BigDecProp.builder().defaultValue(DEFAULT_VALUE).build();
    }
}
