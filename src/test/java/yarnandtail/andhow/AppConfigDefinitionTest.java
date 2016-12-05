package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Test;
import yarnandtail.andhow.appconfig.AppConfigDefinition;
import yarnandtail.andhow.name.AsIsAliasNamingStrategy;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.valid.StringRegex;

/**
 *
 * @author eeverman
 */
public class AppConfigDefinitionTest {
	
	String paramFullPath = SimpleParamsWAlias.class.getCanonicalName() + ".";
	
	@Test
	public void testHappyPath() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, 
				bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));

		//Canonical Names for Point
		assertEquals(paramFullPath + "KVP_BOB", appDef.getCanonicalName(SimpleParamsWAlias.KVP_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParamsWAlias.FLAG_FALSE));
		
		//Get points for Canonical name and alias
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + "KVP_BOB"));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + "FLAG_FALSE"));
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + 
				SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + 
				SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0)));
		
		//Groups
		assertEquals(1, appDef.getGroups().size());
		assertEquals(SimpleParamsWAlias.class, appDef.getGroups().get(0));
		
		//Point list
		assertEquals(2, appDef.getPoints().size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoints().get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoints().get(1));
		
		//Points for Group
		assertEquals(2, appDef.getPointsForGroup(SimpleParamsWAlias.class).size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(1));
		assertEquals(0, appDef.getPointsForGroup(SimpleParamsNoAlias.class).size());		//A random group that is not registered 
	}
	
	@Test
	public void testDuplicatePointInSeparateGroupWithDistinctNames() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsNoAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "fakeName"));
		
		assertEquals(1, appDef.getPoints().size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoints().get(0));
		assertEquals(1, appDef.getConstructionProblems().size());
		assertTrue(appDef.getConstructionProblems().get(0) instanceof ConstructionProblem.DuplicatePoint);
		
		ConstructionProblem.DuplicatePoint dpcp = (ConstructionProblem.DuplicatePoint)appDef.getConstructionProblems().get(0);
		
		assertEquals(SimpleParamsWAlias.KVP_BOB, dpcp.getRefPoint().getPoint());
		assertEquals(SimpleParamsWAlias.class, dpcp.getRefPoint().getGroup());
		assertEquals(bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB").getCanonicalName(), dpcp.getRefPoint().getName());
		assertEquals(SimpleParamsWAlias.KVP_BOB, dpcp.getBadPoint().getPoint());
		assertEquals(SimpleParamsNoAlias.class, dpcp.getBadPoint().getGroup());
		assertEquals(bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "fakeName").getCanonicalName(), dpcp.getBadPoint().getName());
	}
	
	@Test
	public void testDuplicateAlias() {
		String dupParamFullPath = SimpleParamsWAliasDuplicate.class.getCanonicalName() + ".";
		
		//Use Aliases as-is to cause naming collisions
		NamingStrategy bns = new AsIsAliasNamingStrategy();
		AppConfigDefinition appDef = new AppConfigDefinition();
		
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, 
				bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));
		
		// THIS ONE WILL HAVE A DUPLICATE NAME
		appDef.addPoint(SimpleParamsWAliasDuplicate.class, SimpleParamsWAliasDuplicate.FLAG_FALSE, 
				bns.buildNames(SimpleParamsWAliasDuplicate.FLAG_FALSE, SimpleParamsWAliasDuplicate.class, "FLAG_FALSE"));
		// DUPLICATE
		
		//Continue w/ a non-duplicate, which should be added as normal
		appDef.addPoint(SimpleParamsWAliasDuplicate.class, SimpleParamsWAliasDuplicate.FLAG_TRUE, 
				bns.buildNames(SimpleParamsWAliasDuplicate.FLAG_TRUE, SimpleParamsWAliasDuplicate.class, "FLAG_TRUE"));

		//Check values that were actually added to list - The dup point should not have been added
		assertEquals(3, appDef.getPoints().size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoints().get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoints().get(1));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoints().get(2));
		
		//We should have created 1 NonUniuqeName problem
		assertEquals(1, appDef.getConstructionProblems().size());
		assertTrue(appDef.getConstructionProblems().get(0) instanceof ConstructionProblem.NonUniqueNames);
		ConstructionProblem.NonUniqueNames dupCpn = (ConstructionProblem.NonUniqueNames)appDef.getConstructionProblems().get(0);
		
		//Check problem specifics
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_FALSE, dupCpn.getBadPoint().getPoint());
		assertEquals(dupParamFullPath + "FLAG_FALSE", dupCpn.getBadPoint().getName());
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_FALSE.getBaseAliases().get(0), dupCpn.getConflictName());
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, dupCpn.getRefPoint().getPoint());
		assertEquals(paramFullPath + "FLAG_FALSE", dupCpn.getRefPoint().getName());
		
		//Canonical Names for Point
		assertEquals(paramFullPath + "KVP_BOB", appDef.getCanonicalName(SimpleParamsWAlias.KVP_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParamsWAlias.FLAG_FALSE));
		assertNull(appDef.getCanonicalName(SimpleParamsWAliasDuplicate.FLAG_FALSE));		//not added b/c dup
		assertEquals(dupParamFullPath + "FLAG_TRUE", appDef.getCanonicalName(SimpleParamsWAliasDuplicate.FLAG_TRUE));
		
		//Get points for Canonical name and alias
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + "KVP_BOB"));
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + "FLAG_FALSE"));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoint(dupParamFullPath + "FLAG_TRUE"));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoint(SimpleParamsWAliasDuplicate.FLAG_TRUE.getBaseAliases().get(0)));
		
		//Groups
		assertEquals(2, appDef.getGroups().size());
		assertEquals(SimpleParamsWAlias.class, appDef.getGroups().get(0));
		assertEquals(SimpleParamsWAliasDuplicate.class, appDef.getGroups().get(1));
		
		//Point list
		assertEquals(3, appDef.getPoints().size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoints().get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoints().get(1));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoints().get(2));
		
		//Points for Group
		assertEquals(2, appDef.getPointsForGroup(SimpleParamsWAlias.class).size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(1));
		assertEquals(1, appDef.getPointsForGroup(SimpleParamsWAliasDuplicate.class).size());
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPointsForGroup(SimpleParamsWAliasDuplicate.class).get(0));
		assertEquals(0, appDef.getPointsForGroup(SimpleParamsNoAlias.class).size());		//A random group that is not registered 
	}
	
	@Test
	public void testNonValidDefaultValueAndInvalidRegexValidationSpec() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX, 
				bns.buildNames(BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX, BadDefaultAndValidationGroup.class, "NAME_WITH_BAD_REGEX"));
		appDef.addPoint(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT, 
				bns.buildNames(BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT, BadDefaultAndValidationGroup.class, "COLOR_WITH_BAD_DEFAULT"));
		appDef.addPoint(BadDefaultAndValidationGroup.class, BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, 
				bns.buildNames(BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, BadDefaultAndValidationGroup.class, "COLOR_WITH_OK_DEFAULT"));
		
		assertEquals(1, appDef.getPoints().size());
		assertEquals(BadDefaultAndValidationGroup.COLOR_WITH_OK_DEFAULT, appDef.getPoints().get(0));
		assertEquals(2, appDef.getConstructionProblems().size());
		assertTrue(appDef.getConstructionProblems().get(0) instanceof ConstructionProblem.InvalidValidationConfiguration);
		assertTrue(appDef.getConstructionProblems().get(1) instanceof ConstructionProblem.InvalidDefaultValue);
		
		ConstructionProblem.InvalidValidationConfiguration invalidConfig = (ConstructionProblem.InvalidValidationConfiguration)appDef.getConstructionProblems().get(0);
		ConstructionProblem.InvalidDefaultValue invalidDefault = (ConstructionProblem.InvalidDefaultValue)appDef.getConstructionProblems().get(1);

		
		
		assertEquals(BadDefaultAndValidationGroup.NAME_WITH_BAD_REGEX, invalidConfig.getBadPoint().getPoint());
		assertEquals(BadDefaultAndValidationGroup.class, invalidConfig.getBadPoint().getGroup());
		assertEquals(false, invalidConfig.getValidator().isSpecificationValid());
		assertEquals(BadDefaultAndValidationGroup.COLOR_WITH_BAD_DEFAULT, invalidDefault.getBadPoint().getPoint());
		assertEquals(BadDefaultAndValidationGroup.class, invalidDefault.getBadPoint().getGroup());


	}
	
	/**
	 * Used for testing bad default value (don't match the validator) and bad validator config (invalid regex).
	 */
	public static interface BadDefaultAndValidationGroup extends ConfigPointGroup {
		StringConfigPoint NAME_WITH_BAD_REGEX = StringConfigPoint.builder().mustMatchRegex("The[broekn.*").setDefault("The Big Chill").build();
		StringConfigPoint COLOR_WITH_BAD_DEFAULT = StringConfigPoint.builder().mustMatchRegex("[A-F,0-9]*").setDefault("Red").build();
		StringConfigPoint COLOR_WITH_OK_DEFAULT = StringConfigPoint.builder().mustMatchRegex("[A-F,0-9]*").setDefault("FFF000").build();

	}
}
