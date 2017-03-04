package org.yarnandtail.andhow;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.AppFatalException;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.Exporter;
import org.yarnandtail.andhow.Name;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import java.util.List;

import static org.yarnandtail.andhow.AndHowTestBase.reloader;

import org.yarnandtail.andhow.load.StringArgumentLoader;
import org.yarnandtail.andhow.name.BasicNamingStrategy;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;

import org.yarnandtail.andhow.PropertyGroup.NameAndProperty;
import org.yarnandtail.andhow.export.SysPropExporter;

/**
 *
 * @author ericeverman
 */
public class AndHow_AliasOutTest extends AndHowTestBase {
	
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
	interface AliasGroup1 extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().required()
				.aliasIn(STR_PROP1_IN).aliasOut(STR_PROP1_OUT_ALIAS).aliasInAndOut(STR_PROP1_IN_AND_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder()
				.aliasIn(STR_PROP2_IN_ALIAS).build();
		IntProp intProp1 = IntProp.builder()
				.aliasIn(INT_PROP1_ALIAS).aliasInAndOut(INT_PROP1_ALIAS).aliasIn(INT_PROP1_ALT_IN1_ALIAS).build();
		IntProp intProp2 = IntProp.builder().required().defaultValue(INT2).build();
	}
	

	//Has dup alias for strProp1
	interface AliasGroup4 extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().required().aliasOut(STR_PROP1_IN_AND_OUT_ALIAS).build();
	}
	
	interface AliasGroup5 extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();	//dup right here in the same group
	}
	
	interface AliasGroup6 extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder().aliasOut(STR_PROP2_OUT_ALIAS).build();
	}
	
	interface AliasGroup7 extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().aliasOut(STR_PROP1_OUT_ALIAS).build();
		StrProp strProp2 = StrProp.builder().aliasOut(STR_PROP2_OUT_ALIAS).build();
	}

	
	@GroupExport(
		exporter=SysPropExporter.class,
		exportByCanonicalName=Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
		exportByOutAliases=Exporter.EXPORT_OUT_ALIASES.ALWAYS
	)
	interface AliasGroup2 extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().required().build();
		StrProp strProp2 = StrProp.builder().build();
		IntProp intProp1 = IntProp.builder().build();
		IntProp intProp2 = IntProp.builder().required().defaultValue(INT2).build();
	}
	

	
	@Test
	public void testOutAliasForGroup1() {
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new StringArgumentLoader())
				.cmdLineArg(STR_PROP1_IN, STR1)
				.cmdLineArg(STR_PROP2_IN_ALIAS, STR2)
				.cmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.group(AliasGroup1.class)
				.reloadForNonPropduction(reloader);
		
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
		
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new StringArgumentLoader())
				.cmdLineArg(grp2Name + ".strProp1", STR1)
				.cmdLineArg(grp2Name + ".strProp2", STR2)
				.cmdLineArg(grp2Name + ".intProp1", INT1.toString())
				.group(AliasGroup2.class)
				.reloadForNonPropduction(reloader);
	

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
		
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new StringArgumentLoader())
				.group(AliasGroup1.class)
				.group(AliasGroup2.class)
				.cmdLineArg(STR_PROP1_IN, STR1)
				.cmdLineArg(STR_PROP2_IN_ALIAS, STR2)
				.cmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.cmdLineArg(grp2Name + ".strProp1", STR1)
				.cmdLineArg(grp2Name + ".strProp2", STR2)
				.cmdLineArg(grp2Name + ".intProp1", INT1.toString())
				.reloadForNonPropduction(reloader);
		
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
		
		try {
			AndHow.builder().namingStrategy(new BasicNamingStrategy())
					.loader(new StringArgumentLoader())
					.cmdLineArg(STR_PROP1_IN, STR1)	//minimal values set to ensure no missing value error
					.cmdLineArg(STR_PROP2_IN_ALIAS, STR2)
					.cmdLineArg(INT_PROP1_ALIAS, INT1.toString())
					.group(AliasGroup1.class)
					.group(AliasGroup4.class)
					.reloadForNonPropduction(reloader);
			
			fail("Should have thrown an exception");
		} catch (AppFatalException e) {
			List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);
			
			assertEquals(1, probs.size());
			assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
			ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);
			
			assertEquals(AliasGroup4.strProp1, nun.getBadPropertyCoord().property);
			assertEquals(AliasGroup4.class, nun.getBadPropertyCoord().group);
			assertEquals(STR_PROP1_IN_AND_OUT_ALIAS, nun.getConflictName());
		}
	}
	
	@Test
	public void testSingleOutDuplicateWithinASingleGroup() {
		
		try {
			AndHow.builder().namingStrategy(new BasicNamingStrategy())
					.loader(new StringArgumentLoader())
					.group(AliasGroup5.class)
					.reloadForNonPropduction(reloader);
			
			fail("Should have thrown an exception");
		} catch (AppFatalException e) {
			List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);
			
			assertEquals(1, probs.size());
			assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
			ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);
			
			assertEquals(AliasGroup5.strProp2, nun.getBadPropertyCoord().property);
			assertEquals(AliasGroup5.class, nun.getBadPropertyCoord().group);
			assertEquals(STR_PROP1_OUT_ALIAS, nun.getConflictName());
		}
	}
	
	@Test
	public void testTwoOutOutDuplicatesBetweenTwoGroups() {
		
		try {
			AndHow.builder().namingStrategy(new BasicNamingStrategy())
					.loader(new StringArgumentLoader())
					.group(AliasGroup6.class)
					.group(AliasGroup7.class)
					.reloadForNonPropduction(reloader);
			
			fail("Should have thrown an exception");
		} catch (AppFatalException e) {
			List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);
			
			assertEquals(2, probs.size());
			assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
			ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);
			
			assertEquals(AliasGroup7.strProp1, nun.getBadPropertyCoord().property);
			assertEquals(AliasGroup7.class, nun.getBadPropertyCoord().group);
			assertEquals(STR_PROP1_OUT_ALIAS, nun.getConflictName());
		}
	}
	
	

	@Before
	public void beforeEachTest() throws Exception {
		deleteTestSysProps(AliasGroup1.class);
	}
	
	@After
	public void afterEachTest() throws Exception {
		deleteTestSysProps(AliasGroup1.class);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		deleteTestSysProps(AliasGroup1.class);
	}
	
	public static void deleteTestSysProps(Class<? extends PropertyGroup> group) throws Exception {
		List<NameAndProperty> properties = PropertyGroup.getProperties(group);
		
		for (NameAndProperty nap : properties) {
			
			System.getProperties().remove(PropertyGroup.getCanonicalName(group, nap.property));
			
			for (Name a : nap.property.getRequestedAliases()) {
				System.getProperties().remove(a.getActualName());
			}
	
			
		}
	}
}
