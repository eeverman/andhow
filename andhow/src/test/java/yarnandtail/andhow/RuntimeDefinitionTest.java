package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Test;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class RuntimeDefinitionTest {
	
	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	
	//Two PropGroups w/ a duplicate (shared) property
	public interface SampleGroup extends PropertyGroup { StrProp STR_1 = StrProp.builder().build(); }
	public interface SampleGroupDup extends PropertyGroup { StrProp STR_1_DUP = SampleGroup.STR_1; }
	
	public interface RandomUnregisteredGroup extends PropertyGroup { StrProp STR_RND = StrProp.builder().build(); }
	
	@Test
	public void testHappyPath() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		RuntimeDefinition appDef = new RuntimeDefinition();
		appDef.addProperty(SimpleParams.class, SimpleParams.KVP_BOB, 
				bns.buildNames(SimpleParams.KVP_BOB, SimpleParams.class, "KVP_BOB"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE, 
				bns.buildNames(SimpleParams.FLAG_FALSE, SimpleParams.class, "FLAG_FALSE"));

		//Canonical Names for Property
		assertEquals(paramFullPath + "KVP_BOB", appDef.getCanonicalName(SimpleParams.KVP_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParams.FLAG_FALSE));
		
		//Get properties for Canonical name
		assertEquals(SimpleParams.KVP_BOB, appDef.getProperty(paramFullPath + "KVP_BOB"));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getProperty(paramFullPath + "FLAG_FALSE"));

		
		//Groups
		assertEquals(1, appDef.getPropertyGroups().size());
		assertEquals(SimpleParams.class, appDef.getPropertyGroups().get(0));
		
		//prop list
		assertEquals(2, appDef.getProperties().size());
		assertEquals(SimpleParams.KVP_BOB, appDef.getProperties().get(0));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getProperties().get(1));
		
		//Properties for Group
		assertEquals(2, appDef.getPropertiesForGroup(SimpleParams.class).size());
		assertEquals(SimpleParams.KVP_BOB, appDef.getPropertiesForGroup(SimpleParams.class).get(0));
		assertEquals(SimpleParams.FLAG_FALSE, appDef.getPropertiesForGroup(SimpleParams.class).get(1));
		assertEquals(0, appDef.getPropertiesForGroup(RandomUnregisteredGroup.class).size());		//A random group that is not registered 
	}
	
	@Test
	public void testDuplicatePropertiesInSeparateGroupWithDistinctNames() throws Exception {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		RuntimeDefinition appDef = new RuntimeDefinition();
		appDef.addProperty(SampleGroup.class, SampleGroup.STR_1, 
				bns.buildNames(SampleGroup.STR_1, SampleGroup.class, "STR_1"));
		appDef.addProperty(SampleGroupDup.class, SampleGroupDup.STR_1_DUP, 
				bns.buildNames(SampleGroupDup.STR_1_DUP, SampleGroupDup.class, "STR_1_DUP"));
		
		assertEquals(1, appDef.getProperties().size());
		assertEquals(SampleGroup.STR_1, appDef.getProperties().get(0));
		assertEquals(1, appDef.getConstructionProblems().size());
		assertTrue(appDef.getConstructionProblems().get(0) instanceof ConstructionProblem.DuplicateProperty);
		
		ConstructionProblem.DuplicateProperty dpcp = (ConstructionProblem.DuplicateProperty)appDef.getConstructionProblems().get(0);
		
		assertEquals(SampleGroup.STR_1, dpcp.getRefPropertyCoord().getProperty());
		assertEquals(SampleGroup.class, dpcp.getRefPropertyCoord().getGroup());
		assertEquals(PropertyGroup.getCanonicalName(SampleGroup.class, SampleGroup.STR_1), dpcp.getRefPropertyCoord().getPropName());
		assertEquals(SampleGroupDup.STR_1_DUP, dpcp.getBadPropertyCoord().getProperty());
		assertEquals(SampleGroupDup.class, dpcp.getBadPropertyCoord().getGroup());
		assertEquals(PropertyGroup.getCanonicalName(SampleGroupDup.class, SampleGroupDup.STR_1_DUP), dpcp.getBadPropertyCoord().getPropName());
	}

	
	@Test
	public void testNonValidDefaultValueAndInvalidRegexValidationSpec() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		RuntimeDefinition appDef = new RuntimeDefinition();
		appDef.addProperty(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX, 
				bns.buildNames(BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX, BadDefaultAndValidationGroup.class, "NAME_WITH_BAD_REGEX"));
		appDef.addProperty(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT, 
				bns.buildNames(BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT, BadDefaultAndValidationGroup.class, "COLOR_WITH_BAD_DEFAULT"));
		appDef.addProperty(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, 
				bns.buildNames(BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, BadDefaultAndValidationGroup.class, "COLOR_WITH_OK_DEFAULT"));
		
		assertEquals(1, appDef.getProperties().size());
		assertEquals(BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, appDef.getProperties().get(0));
		assertEquals(2, appDef.getConstructionProblems().size());
		assertTrue(appDef.getConstructionProblems().get(0) instanceof ConstructionProblem.InvalidValidationConfiguration);
		assertTrue(appDef.getConstructionProblems().get(1) instanceof ConstructionProblem.InvalidDefaultValue);
		
		ConstructionProblem.InvalidValidationConfiguration invalidConfig = (ConstructionProblem.InvalidValidationConfiguration)appDef.getConstructionProblems().get(0);
		ConstructionProblem.InvalidDefaultValue invalidDefault = (ConstructionProblem.InvalidDefaultValue)appDef.getConstructionProblems().get(1);

		
		
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
