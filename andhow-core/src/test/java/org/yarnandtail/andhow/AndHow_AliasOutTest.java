package org.yarnandtail.andhow;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

/**
 *
 * @author ericeverman
 */
public class AndHow_AliasOutTest extends AndHowCoreTestBase {
	
	//
	//Alias names
	static final String STR_PROP1_IN = "str.Prop.1.In";
	static final String STR_PROP1_OUT_ALIAS = "strProp1Out";
	static final String STR_PROP1_IN_AND_OUT_ALIAS = "StrProp1InAndOut";
	static final String STR_PROP2_IN_ALIAS = "strProp2";
	static final String STR_PROP2_OUT_ALIAS = "strProp2-out";
	static final String INT_PROP1_ALIAS = "IntProp1";
	static final String INT_PROP1_ALT_IN1_ALIAS = "Int.Prop.1-Alt-In!1";
	
	
	//
	//Property values
	private static final String STR1 = "SOME_FIXED_VALUE_STRING_1";
	private static final String STR2 = "SOME_FIXED_VALUE_STRING_2";
	private static final Integer INT1 = 23572374;
	private static final Integer INT2 = 9237347;
	
	@GroupExport(
		exporter=SysPropExporter.class,
		exportByCanonicalName=Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
		exportByOutAliases=Exporter.EXPORT_OUT_ALIASES.ALWAYS
	)
	interface AliasGroup1 {
		StrProp strProp1 = StrProp.builder().mustBeNonNull()
				.aliasIn(STR_PROP1_IN).aliasOut(STR_PROP1_OUT_ALIAS).aliasInAndOut(STR_PROP1_IN_AND_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder()
				.aliasIn(STR_PROP2_IN_ALIAS).build();
		IntProp intProp1 = IntProp.builder()
				.aliasIn(INT_PROP1_ALIAS).aliasInAndOut(INT_PROP1_ALIAS).aliasIn(INT_PROP1_ALT_IN1_ALIAS).build();
		IntProp intProp2 = IntProp.builder().mustBeNonNull().defaultValue(INT2).build();
	}
	

	//Has dup alias for strProp1
	interface AliasGroup4 {
		StrProp strProp1 = StrProp.builder().mustBeNonNull().aliasOut(STR_PROP1_IN_AND_OUT_ALIAS).build();
	}
	
	interface AliasGroup5 {
		StrProp strProp1 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();	//dup right here in the same group
	}
	
	interface AliasGroup6 {
		StrProp strProp1 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder().aliasOut(STR_PROP2_OUT_ALIAS).build();
	}
	
	interface AliasGroup7 {
		StrProp strProp1 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder().aliasOut(STR_PROP2_OUT_ALIAS).build();
	}

	
	@GroupExport(
		exporter=SysPropExporter.class,
		exportByCanonicalName=Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
		exportByOutAliases=Exporter.EXPORT_OUT_ALIASES.ALWAYS
	)
	interface AliasGroup2 {
		StrProp strProp1 = StrProp.builder().mustBeNonNull().build();
		StrProp strProp2 = StrProp.builder().build();
		IntProp intProp1 = IntProp.builder().build();
		IntProp intProp2 = IntProp.builder().mustBeNonNull().defaultValue(INT2).build();
	}
	
	@Test
	public void testOutAliasForGroup1() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN, STR1)
				.addCmdLineArg(STR_PROP2_IN_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);
		
		//This just tests the test...
		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
		assertEquals(INT2, AliasGroup1.intProp2.getValue());	//default should still come thru
		
		//
		//And now for something more interesting
		
		//strProp1
		assertEquals(STR1, System.getProperty(STR_PROP1_OUT_ALIAS));
		assertEquals(STR1, System.getProperty(STR_PROP1_IN_AND_OUT_ALIAS));
		assertNull(System.getProperty(STR_PROP1_IN));
		assertNull(System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.strProp1)));
		
		//strProp2
		assertEquals(STR2, System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.strProp2)));
		assertNull(System.getProperty(STR_PROP2_IN_ALIAS));
		
		//intProp1
		assertEquals(INT1.toString(), System.getProperty(INT_PROP1_ALIAS));
		assertNull(System.getProperty(INT_PROP1_ALT_IN1_ALIAS));
		assertNull(System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.intProp1)));
		
		//intProp2
		assertEquals(INT2.toString(), System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.intProp2)));
	}
	

	@Test
	public void testOutAliasForGroup2() {
		
		String grp2Name = AliasGroup2.class.getCanonicalName();
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(grp2Name + ".strProp1", STR1)
				.addCmdLineArg(grp2Name + ".strProp2", STR2)
				.addCmdLineArg(grp2Name + ".intProp1", INT1.toString())
				.addOverrideGroup(AliasGroup2.class);

		AndHow.setConfig(config);
	

		//
		//No aliases - all canon names should be present
		
		assertEquals(STR1, System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.strProp1)));
		assertEquals(STR2, System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.strProp2)));
		assertEquals(INT1.toString(), System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.intProp1)));
		assertEquals(INT2.toString(), System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.intProp2)));
	}
	
	@Test
	public void testOutAliasForGroup1and2() {
		
		String grp2Name = AliasGroup2.class.getCanonicalName();
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(AliasGroup1.class)
				.addOverrideGroup(AliasGroup2.class)
				.addCmdLineArg(STR_PROP1_IN, STR1)
				.addCmdLineArg(STR_PROP2_IN_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.addCmdLineArg(grp2Name + ".strProp1", STR1)
				.addCmdLineArg(grp2Name + ".strProp2", STR2)
				.addCmdLineArg(grp2Name + ".intProp1", INT1.toString());

		AndHow.setConfig(config);
		AndHow.instance();
		
		//
		// Group 1
		
		//strProp1
		assertEquals(STR1, System.getProperty(STR_PROP1_OUT_ALIAS));
		assertEquals(STR1, System.getProperty(STR_PROP1_IN_AND_OUT_ALIAS));
		assertNull(System.getProperty(STR_PROP1_IN));
		assertNull(System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.strProp1)));
		
		//strProp2
		assertEquals(STR2, System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.strProp2)));
		assertNull(System.getProperty(STR_PROP2_IN_ALIAS));
		
		//intProp1
		assertEquals(INT1.toString(), System.getProperty(INT_PROP1_ALIAS));
		assertNull(System.getProperty(INT_PROP1_ALT_IN1_ALIAS));
		assertNull(System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.intProp1)));
		
		//intProp2
		assertEquals(INT2.toString(), System.getProperty(AndHow.instance().getCanonicalName(AliasGroup1.intProp2)));
		
		
		//
		//No aliases for group 2 - all canon names should be present
		
		assertEquals(STR1, System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.strProp1)));
		assertEquals(STR2, System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.strProp2)));
		assertEquals(INT1.toString(), System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.intProp1)));
		assertEquals(INT2.toString(), System.getProperty(AndHow.instance().getCanonicalName(AliasGroup2.intProp2)));
	}
	

	@Test
	public void testSingleOutDuplicateOfGroup1InOutAlias() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN, STR1)	//minimal values set to ensure no missing value error
				.addCmdLineArg(STR_PROP2_IN_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class)
				.addOverrideGroup(AliasGroup4.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);
			
		assertEquals(1, probs.size());
		assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
		ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);

		assertEquals(AliasGroup4.strProp1, nun.getBadPropertyCoord().getProperty());
		assertEquals(AliasGroup4.class, nun.getBadPropertyCoord().getGroup());
		assertEquals(STR_PROP1_IN_AND_OUT_ALIAS, nun.getConflictName());
	}
	
	@Test
	public void testSingleOutDuplicateWithinASingleGroup() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(AliasGroup5.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);
			
		assertEquals(1, probs.size());
		assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
		ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);

		assertEquals(AliasGroup5.strProp2, nun.getBadPropertyCoord().getProperty());
		assertEquals(AliasGroup5.class, nun.getBadPropertyCoord().getGroup());
		assertEquals(STR_PROP1_OUT_ALIAS, nun.getConflictName());
	}
	
	@Test
	public void testTwoOutOutDuplicatesBetweenTwoGroups() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(AliasGroup6.class)
				.addOverrideGroup(AliasGroup7.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);

		assertEquals(2, probs.size());
		assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
		ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);

		assertEquals(AliasGroup7.strProp1, nun.getBadPropertyCoord().getProperty());
		assertEquals(AliasGroup7.class, nun.getBadPropertyCoord().getGroup());
		assertEquals(STR_PROP1_OUT_ALIAS, nun.getConflictName());
	}
}
