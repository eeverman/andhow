package org.yarnandtail.andhow;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.junit5.EnableJndiForThisTestMethod;
import org.yarnandtail.andhow.junit5.EnableJndiUtil;
import org.yarnandtail.andhow.load.std.StdJndiLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

import javax.naming.InitialContext;

import static org.junit.jupiter.api.Assertions.*;

public class AndHow_AliasInTest extends AndHowTestBase {

	//
	//Alias names
	static final String STR_PROP1_IN = "str.Prop.1.In";
	static final String STR_PROP1_OUT_ALIAS = "strProp1Out";
	static final String STR_PROP1_IN_AND_OUT_ALIAS = "StrProp1InAndOut";
	static final String STR_PROP2_ALIAS = "strProp2";
	static final String STR_PROP2_IN_ALT1_ALIAS = "StrProp2.InAlt1";
	static final String STR_PROP2_IN_ALT2_ALIAS = "StrProp2.InAlt2";
	static final String INT_PROP1_ALIAS = "IntProp1";
	static final String INT_PROP1_ALT_IN1_ALIAS = "Int.Prop.1-Alt-In!1";


	//
	//Property values
	private static final String STR1 = "SOME_FIXED_VALUE_STRING_1";
	private static final String STR2 = "SOME_FIXED_VALUE_STRING_2";
	private static final Integer INT1 = 23572374;
	private static final Integer INT2 = 9237347;

	interface AliasGroup1 {
		StrProp strProp1 = StrProp.builder().notNull()
				.aliasIn(STR_PROP1_IN).aliasOut(STR_PROP1_OUT_ALIAS).aliasInAndOut(STR_PROP1_IN_AND_OUT_ALIAS).build();

		StrProp strProp2 = StrProp.builder()
				.aliasIn(STR_PROP2_ALIAS).aliasOut(STR_PROP2_ALIAS).aliasIn(STR_PROP2_IN_ALT1_ALIAS).aliasIn(STR_PROP2_IN_ALT2_ALIAS).build();
		IntProp intProp1 = IntProp.builder()
				.aliasIn(INT_PROP1_ALIAS).aliasInAndOut(INT_PROP1_ALIAS).aliasIn(INT_PROP1_ALT_IN1_ALIAS).build();
		IntProp intProp2 = IntProp.builder().notNull().defaultValue(INT2).build();
	}

	//Has dup alias for strProp1
	interface AliasGroup2 {
		StrProp strProp1 = StrProp.builder().notNull().aliasIn(STR_PROP1_IN).build();
	}

	//Has dup alias for strProp1
	interface AliasGroup3 {
		StrProp strProp1 = StrProp.builder().notNull().aliasIn(STR_PROP1_IN_AND_OUT_ALIAS).build();
	}

	//Has dup alias for strProp1, but is all lower case
	interface AliasGroup4 {
		StrProp strProp1 = StrProp.builder().notNull().aliasIn(STR_PROP1_IN.toLowerCase()).build();
	}



	@Test
	public void testFirstSetOfInAliasesViaCmdLine() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN, STR1)
				.addCmdLineArg(STR_PROP2_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
		assertEquals(INT2, AliasGroup1.intProp2.getValue());	//default should still come thru
	}

	@Test
	public void testSecondSetOfInAliasesViaCmdLine() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN_AND_OUT_ALIAS, STR1)
				.addCmdLineArg(STR_PROP2_IN_ALT1_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALT_IN1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
	}

	@Test
	public void testThirdSetOfInAliasesViaCmdLine() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN_AND_OUT_ALIAS, STR1)
				.addCmdLineArg(STR_PROP2_IN_ALT2_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALT_IN1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
	}



	@Test
	@EnableJndiForThisTestMethod
	public void testInAliasesViaJndiCompEnvClassPath() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/");
		//

		jndi.bind("java:comp/env/" + STR_PROP1_IN, STR1);
		jndi.bind("java:comp/env/" + STR_PROP2_ALIAS, STR2);
		jndi.bind("java:comp/env/" + INT_PROP1_ALIAS, INT1.toString());

		AndHowConfiguration config = AndHowTestConfig.instance()
				.setLoaders(new StdJndiLoader())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
		assertEquals(INT2, AliasGroup1.intProp2.getValue());	//default should still come thru
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testInAliasesViaJndiRootClassPath() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/AndHow_AliasInTest/");

		jndi.bind("java:" + STR_PROP1_IN_AND_OUT_ALIAS, STR1);
		jndi.bind("java:" + STR_PROP2_IN_ALT1_ALIAS, STR2);
		jndi.bind("java:" + INT_PROP1_ALT_IN1_ALIAS, INT1.toString());

		AndHowConfiguration config = AndHowTestConfig.instance()
				.setLoaders(new StdJndiLoader())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
		assertEquals(INT2, AliasGroup1.intProp2.getValue());	//default should still come thru
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testInAliasesViaJndiCompEnvUrlNames() throws Exception {

		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/str/Prop/1/");

		jndi.bind("java:comp/env/" + bns.getUriName(STR_PROP1_IN), STR1);
		jndi.bind("java:comp/env/" + bns.getUriName(STR_PROP2_ALIAS), STR2);
		jndi.bind("java:comp/env/" + bns.getUriName(INT_PROP1_ALIAS), INT1.toString());

		AndHowConfiguration config = AndHowTestConfig.instance()
				.setLoaders(new StdJndiLoader())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
		assertEquals(INT2, AliasGroup1.intProp2.getValue());	//default should still come thru
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testInAliasesViaJndiRootUrlNames() throws Exception {

		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:str/Prop/1/");

		jndi.bind("java:" + bns.getUriName(STR_PROP1_IN), STR1);
		jndi.bind("java:" + bns.getUriName(STR_PROP2_ALIAS), STR2);
		jndi.bind("java:" + bns.getUriName(INT_PROP1_ALIAS), INT1.toString());


		AndHowConfiguration config = AndHowTestConfig.instance()
				.setLoaders(new StdJndiLoader())
				.addOverrideGroup(AliasGroup1.class);

		AndHow.setConfig(config);

		assertEquals(STR1, AliasGroup1.strProp1.getValue());
		assertEquals(STR2, AliasGroup1.strProp2.getValue());
		assertEquals(INT1, AliasGroup1.intProp1.getValue());
		assertEquals(INT2, AliasGroup1.intProp2.getValue());	//default should still come thru
	}

	//
	// Degenerate cases

	@Test
	public void testSingleInDuplicateOfGroup1InAlias() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN, STR1)	//minimal values set to ensure no missing value error
				.addCmdLineArg(STR_PROP2_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class)
				.addOverrideGroup(AliasGroup2.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);

		assertEquals(1, probs.size());
		assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
		ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);

		assertEquals(AliasGroup2.strProp1, nun.getBadPropertyCoord().getProperty());
		assertEquals(STR_PROP1_IN, nun.getConflictName());
	}


	@Test
	public void testSingleInDuplicateOfGroup1InAliasInLowerCase() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN, STR1)	//minimal values set to ensure no missing value error
				.addCmdLineArg(STR_PROP2_ALIAS, STR2)
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
		assertEquals(STR_PROP1_IN.toLowerCase(), nun.getConflictName());
	}

	@Test
	public void testSingleInDuplicateOfGroup1InOutAlias() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addCmdLineArg(STR_PROP1_IN, STR1)	//minimal values set to ensure no missing value error
				.addCmdLineArg(STR_PROP2_ALIAS, STR2)
				.addCmdLineArg(INT_PROP1_ALIAS, INT1.toString())
				.addOverrideGroup(AliasGroup1.class)
				.addOverrideGroup(AliasGroup3.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<ConstructionProblem> probs = e.getProblems().filter(ConstructionProblem.class);

		assertEquals(1, probs.size());
		assertTrue(probs.get(0) instanceof ConstructionProblem.NonUniqueNames);
		ConstructionProblem.NonUniqueNames nun = (ConstructionProblem.NonUniqueNames)probs.get(0);

		assertEquals(AliasGroup3.strProp1, nun.getBadPropertyCoord().getProperty());
		assertEquals(STR_PROP1_IN_AND_OUT_ALIAS, nun.getConflictName());
	}

}
