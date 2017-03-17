package org.yarnandtail.andhow.internal;


import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.api.PropertyGroup;
import org.yarnandtail.andhow.api.NamingStrategy;
import org.yarnandtail.andhow.api.ConstructionDefinition;
import static org.junit.Assert.*;
import org.junit.Test;
import org.yarnandtail.andhow.SimpleParams;
import org.yarnandtail.andhow.internal.ConstructionDefinitionMutable;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class ConstructionDefinitionImmutableTest {
	
	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	
	//Two PropGroups w/ a duplicate (shared) property
	public interface SampleGroup extends PropertyGroup { StrProp STR_1 = StrProp.builder().build(); }
	public interface SampleGroupDup extends PropertyGroup { StrProp STR_1_DUP = SampleGroup.STR_1; }
	
	public interface RandomUnregisteredGroup extends PropertyGroup { StrProp STR_RND = StrProp.builder().build(); }
	
	@Test
	public void testHappyPath() throws Exception {
		
		NamingStrategy bns = new CaseInsensitiveNaming();
		
		ConstructionDefinitionMutable cdm = new ConstructionDefinitionMutable(bns);
		cdm.addProperty(SimpleParams.class, SimpleParams.STR_BOB);
		cdm.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE);
		
		
		ConstructionDefinition appDef = cdm.toImmutable();
		
		//Canonical Names for Property
		assertEquals(paramFullPath + "STR_BOB", appDef.getCanonicalName(SimpleParams.STR_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParams.FLAG_FALSE));
		
		//Get properties for Canonical name
		assertEquals(SimpleParams.STR_BOB, appDef.getProperty(paramFullPath + "STR_BOB"));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getProperty(paramFullPath + "FLAG_FALSE"));

		
		//Groups
		assertEquals(1, appDef.getPropertyGroups().size());
		assertEquals(SimpleParams.class, appDef.getPropertyGroups().get(0));
		
		//prop list
		assertEquals(2, appDef.getProperties().size());
		assertEquals(SimpleParams.STR_BOB, appDef.getProperties().get(0));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getProperties().get(1));
		
		//Properties for Group
		assertEquals(2, appDef.getPropertiesForGroup(SimpleParams.class).size());
		assertEquals(SimpleParams.STR_BOB, appDef.getPropertiesForGroup(SimpleParams.class).get(0));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getPropertiesForGroup(SimpleParams.class).get(1));
		assertEquals(0, appDef.getPropertiesForGroup(RandomUnregisteredGroup.class).size());		//A random group that is not registered 
	}
	
	@Test
	public void testDuplicatePropertiesInSeparateGroupWithDistinctNames() throws Exception {
		
		NamingStrategy bns = new CaseInsensitiveNaming();
		ProblemList<ConstructionProblem> problems = new ProblemList();
		ConstructionDefinitionMutable cdm = new ConstructionDefinitionMutable(bns);
		
		problems.add(cdm.addProperty(SampleGroup.class, SampleGroup.STR_1));

		problems.add(cdm.addProperty(SampleGroupDup.class, SampleGroupDup.STR_1_DUP));
		
		ConstructionDefinition appDef = cdm.toImmutable();
		
		assertEquals(1, appDef.getProperties().size());
		assertEquals(SampleGroup.STR_1, appDef.getProperties().get(0));
		assertEquals(1, problems.size());
		assertTrue(problems.get(0) instanceof ConstructionProblem.DuplicateProperty);
		
		ConstructionProblem.DuplicateProperty dpcp = (ConstructionProblem.DuplicateProperty)problems.get(0);
		
		assertEquals(SampleGroup.STR_1, dpcp.getRefPropertyCoord().getProperty());
		assertEquals(SampleGroup.class, dpcp.getRefPropertyCoord().getGroup());
		assertEquals(AndHowUtil.getCanonicalName(SampleGroup.class, SampleGroup.STR_1), dpcp.getRefPropertyCoord().getPropName());
		assertEquals(SampleGroupDup.STR_1_DUP, dpcp.getBadPropertyCoord().getProperty());
		assertEquals(SampleGroupDup.class, dpcp.getBadPropertyCoord().getGroup());
		assertEquals(AndHowUtil.getCanonicalName(SampleGroupDup.class, SampleGroupDup.STR_1_DUP), dpcp.getBadPropertyCoord().getPropName());
	}

	
	@Test
	public void testNonValidDefaultValueAndInvalidRegexValidationSpec() throws Exception {
		
		NamingStrategy bns = new CaseInsensitiveNaming();
		ProblemList<ConstructionProblem> problems = new ProblemList();
		ConstructionDefinitionMutable cdm = new ConstructionDefinitionMutable(bns);
		
		problems.add(cdm.addProperty(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX));

		problems.add(cdm.addProperty(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT));
		
		problems.add(cdm.addProperty(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT));
		
		ConstructionDefinition appDef = cdm.toImmutable();
		
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
