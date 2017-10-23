package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.property.PropertyTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.RequirementProblem;
import org.yarnandtail.andhow.property.BolProp;

/**
 * Tests StrProp instances as they would be used in an app.
 * 
 * Focuses on builder functionality, validation and metadata.
 * 
 * @author ericeverman
 */
public class BolPropTest extends PropertyTestBase {

	@Test
	public void happyPathTest() {
		this.buildConfig(this, "_happyPath", BolGroup.class);
		
		assertTrue(BolGroup.ENABLE.getValue());
		assertTrue(BolGroup.ALIAS_ME.getValue());
		assertTrue(BolGroup.BIG_SWITCH_TRUE.getValue());
		assertFalse(BolGroup.BIG_SWITCH_FALSE.getValue());
		assertNull(BolGroup.BIG_SWITCH_NULL.getValue());
		
		assertEquals("enable desc", BolGroup.ENABLE.getDescription());
		assertTrue(BolGroup.ENABLE.isNonNullRequired());
		assertEquals("iAmAliased", BolGroup.ALIAS_ME.getRequestedAliases().get(0).getActualName());
	}
	
	@Test
	public void happyPathSetFalseTest() {
		this.buildConfig(this, "_happyPathSetFalse", BolGroup.class);
		
		assertFalse(BolGroup.ENABLE.getValue());
		assertFalse(BolGroup.ALIAS_ME.getValue());
		assertFalse(BolGroup.BIG_SWITCH_TRUE.getValue());
		assertFalse(BolGroup.BIG_SWITCH_FALSE.getValue());
		assertFalse(BolGroup.BIG_SWITCH_NULL.getValue());
	}
	
	@Test
	public void failDueToNotSettingRequiredNonNullTest() {
		
		try {
			this.buildConfig(this, "_enableNotSet", BolGroup.class);
		} catch (AppFatalException e) {
			ProblemList<Problem> problems = e.getProblems();
			assertEquals(1, problems.size());
			assertTrue(problems.get(0) instanceof RequirementProblem);
		}
	}
	
	public interface BolGroup extends PropertyGroup {
		BolProp ENABLE = BolProp.builder().mustBeNonNull().desc("enable desc").build();
		BolProp ALIAS_ME = BolProp.builder().aliasInAndOut("iAmAliased").build();
		BolProp BIG_SWITCH_TRUE = BolProp.builder().defaultValue(true).build();
		BolProp BIG_SWITCH_FALSE = BolProp.builder().defaultValue(false).build();
		BolProp BIG_SWITCH_NULL = BolProp.builder().build();
	}
}
