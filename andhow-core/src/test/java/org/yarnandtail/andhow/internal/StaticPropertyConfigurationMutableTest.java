package org.yarnandtail.andhow.internal;


import org.yarnandtail.andhow.util.AndHowUtil;

import static org.junit.Assert.*;

import org.junit.Test;
import org.yarnandtail.andhow.api.NamingStrategy;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.util.NameUtil;

/**
 *
 * @author eeverman
 */
public class StaticPropertyConfigurationMutableTest {
	
	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	
	public interface SimpleParams {
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
	}
	
	public interface SampleGroup extends PropertyGroup { StrProp STR_1 = StrProp.builder().build(); }
	public interface SampleGroupDup extends PropertyGroup { StrProp STR_1_DUP = SampleGroup.STR_1; }
	
	public interface RandomUnregisteredGroup extends PropertyGroup { StrProp STR_RND = StrProp.builder().build(); }
	
	@Test
	public void testHappyPath() throws Exception {
		
		NamingStrategy bns = new CaseInsensitiveNaming();
		
		StaticPropertyConfigurationMutable appDef = new StaticPropertyConfigurationMutable(bns);
		
		GroupProxy proxy = AndHowUtil.buildGroupProxy(SimpleParams.class);
		
		appDef.addProperty(proxy, SimpleParams.STR_BOB);
		appDef.addProperty(proxy, SimpleParams.FLAG_FALSE);

		//Canonical Names for Property
		assertEquals(paramFullPath + "STR_BOB", appDef.getCanonicalName(SimpleParams.STR_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParams.FLAG_FALSE));
		
		//Get properties for Canonical name
		assertEquals(SimpleParams.STR_BOB, appDef.getProperty(paramFullPath + "STR_BOB"));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getProperty(paramFullPath + "FLAG_FALSE"));

		
		//Groups
		assertEquals(1, appDef.getPropertyGroups().size());
		assertEquals(proxy, appDef.getPropertyGroups().get(0));
		
		//prop list
		assertEquals(2, appDef.getProperties().size());
		assertEquals(SimpleParams.STR_BOB, appDef.getProperties().get(0));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getProperties().get(1));
		
		//Properties for Group
		assertEquals(2, appDef.getPropertiesForGroup(proxy).size());
		assertEquals(SimpleParams.STR_BOB, appDef.getPropertiesForGroup(proxy).get(0));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getPropertiesForGroup(proxy).get(1));
	}
	
	@Test
	public void testDuplicatePropertiesInSeparateGroupWithDistinctNames() throws Exception {
		
		NamingStrategy bns = new CaseInsensitiveNaming();
		ProblemList<ConstructionProblem> problems = new ProblemList();
		StaticPropertyConfigurationMutable appDef = new StaticPropertyConfigurationMutable(bns);
		
		GroupProxy sampleGroupProxy = AndHowUtil.buildGroupProxy(SampleGroup.class);
		GroupProxy sampleGroupDupProxy = AndHowUtil.buildGroupProxy(SampleGroupDup.class);
		
		problems.add(appDef.addProperty(sampleGroupProxy, SampleGroup.STR_1));
		problems.add(appDef.addProperty(sampleGroupDupProxy, SampleGroupDup.STR_1_DUP));
		
		assertEquals(1, appDef.getProperties().size());
		assertEquals(SampleGroup.STR_1, appDef.getProperties().get(0));
		assertEquals(1, problems.size());
		assertTrue(problems.get(0) instanceof ConstructionProblem.DuplicateProperty);
		
		ConstructionProblem.DuplicateProperty dpcp = (ConstructionProblem.DuplicateProperty)problems.get(0);
		
		assertEquals(SampleGroup.STR_1, dpcp.getRefPropertyCoord().getProperty());
		assertEquals(SampleGroup.class, dpcp.getRefPropertyCoord().getGroup());
		assertEquals(NameUtil.getAndHowName(SampleGroup.class, SampleGroup.STR_1), dpcp.getRefPropertyCoord().getPropName());
		assertEquals(SampleGroupDup.STR_1_DUP, dpcp.getBadPropertyCoord().getProperty());
		assertEquals(SampleGroupDup.class, dpcp.getBadPropertyCoord().getGroup());
		assertEquals(NameUtil.getAndHowName(SampleGroupDup.class, SampleGroupDup.STR_1_DUP), dpcp.getBadPropertyCoord().getPropName());
	}

	
	@Test
	public void testNonValidDefaultValueAndInvalidRegexValidationSpec() throws Exception {
		
		NamingStrategy bns = new CaseInsensitiveNaming();
		ProblemList<ConstructionProblem> problems = new ProblemList();
		StaticPropertyConfigurationMutable appDef = new StaticPropertyConfigurationMutable(bns);
		
		GroupProxy proxy = AndHowUtil.buildGroupProxy(BadDefaultAndValidationGroup.class);
		
		problems.add(appDef.addProperty(proxy, BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX));
		problems.add(appDef.addProperty(proxy, BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT));
		problems.add(appDef.addProperty(proxy, BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT));
		
		assertEquals(1, appDef.getProperties().size());
		assertEquals(BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, appDef.getProperties().get(0));
		assertEquals(2, problems.size());
		assertTrue(problems.get(0) instanceof ConstructionProblem.InvalidValidationConfiguration);
		assertTrue(problems.get(1) instanceof ConstructionProblem.InvalidDefaultValue);
		
		ConstructionProblem.InvalidValidationConfiguration invalidConfig = (ConstructionProblem.InvalidValidationConfiguration)problems.get(0);
		ConstructionProblem.InvalidDefaultValue invalidDefault = (ConstructionProblem.InvalidDefaultValue)problems.get(1);

		
		
		assertEquals(BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX, invalidConfig.getBadPropertyCoord().getProperty());
		assertEquals(BadDefaultAndValidationGroup.class, invalidConfig.getBadPropertyCoord().getGroup());
		assertEquals(false, invalidConfig.getValidator().isSpecificationValid());
		assertEquals(BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT, invalidDefault.getBadPropertyCoord().getProperty());
		assertEquals(BadDefaultAndValidationGroup.class, invalidDefault.getBadPropertyCoord().getGroup());


	}
	
	/**
	 * Used for testing bad default value (don't match the validator) and bad validator config (invalid regex).
	 */
	public static interface BadDefaultAndValidationGroup extends PropertyGroup {
		StrProp NAME_WITH_BAD_REGEX = StrProp.builder().mustMatchRegex("The[broekn.*").defaultValue("The Big Chill").build();
		StrProp COLOR_WITH_BAD_DEFAULT = StrProp.builder().mustMatchRegex("[A-F,0-9]*").defaultValue("Red").build();
		StrProp COLOR_WITH_OK_DEFAULT = StrProp.builder().mustMatchRegex("[A-F,0-9]*").defaultValue("FFF000").build();
	}
}
