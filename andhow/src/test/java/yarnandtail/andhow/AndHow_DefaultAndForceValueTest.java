package yarnandtail.andhow;

import java.util.ArrayList;
import static yarnandtail.andhow.AndHowTestBase.reloader;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.property.StrProp;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class AndHow_DefaultAndForceValueTest extends AndHowTestBase {
	
	private static final String STR1 = "SOME_FIXED_VALUE_STRING_1";
	private static final String STR2 = "SOME_FIXED_VALUE_STRING_2";
	private static final Integer INT1 = 23572374;
	private static final Integer INT2 = 9237347;
	
	interface DefTestGroup extends PropertyGroup {
		StrProp strProp1 = StrProp.builder().required().build();
		StrProp strProp2 = StrProp.builder().defaultValue(STR2).build();
		IntProp intProp1 = IntProp.builder().build();
		IntProp intProp2 = IntProp.builder().required().defaultValue(INT2).build();
	}
	
	@Test
	public void testStringDefaultOnRequiredValue() {
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				.defaultValue(DefTestGroup.strProp1, STR1)	/* set a default val */
				.reloadForNonPropduction(reloader);
		
		assertEquals(STR1, DefTestGroup.strProp1.getValue());
		assertEquals(STR2, DefTestGroup.strProp2.getValue());
		assertNull(DefTestGroup.intProp1.getValue());
		assertEquals(INT2, DefTestGroup.intProp2.getValue());
	}
	
	@Test
	public void testMutlitpleAndHowDefaultsToOverridePropDefinedDefaults() {
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				.defaultValue(DefTestGroup.strProp1, "AAA")	/* set a default val */
				.defaultValue(DefTestGroup.strProp2, "BBB")	/* set a default val */
				.defaultValue(DefTestGroup.intProp1, 888)	/* set a default val */
				.defaultValue(DefTestGroup.intProp2, 999)	/* set a default val */
				.reloadForNonPropduction(reloader);
		
		assertEquals("AAA", DefTestGroup.strProp1.getValue());
		assertEquals("BBB", DefTestGroup.strProp2.getValue());
		assertEquals(new Integer(888), DefTestGroup.intProp1.getValue());
		assertEquals(new Integer(999), DefTestGroup.intProp2.getValue());
	}
	
	@Test
	public void testMixedForcedAndDefaults() {
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				
				/* Defaults */
				.defaultValue(DefTestGroup.strProp1, "AAA")	/* set a default val */
				.defaultValue(DefTestGroup.strProp2, "BBB")	/* set a default val */
				.defaultValue(DefTestGroup.intProp1, 888)	/* set a default val */
				.defaultValue(DefTestGroup.intProp2, 999)	/* set a default val */
				/*FORCED VALUES */
				.forceValue(DefTestGroup.strProp1, "CCC")	/* set a default val */
				.forceValue(DefTestGroup.strProp2, "DDD")	/* set a default val */
				.forceValue(DefTestGroup.intProp1, 111)	/* set a default val */
				.forceValue(DefTestGroup.intProp2, 222)	/* set a default val */
				.reloadForNonPropduction(reloader);
		
		assertEquals("CCC", DefTestGroup.strProp1.getValue());
		assertEquals("DDD", DefTestGroup.strProp2.getValue());
		assertEquals(new Integer(111), DefTestGroup.intProp1.getValue());
		assertEquals(new Integer(222), DefTestGroup.intProp2.getValue());
	}
	
	
	//
	// Degenerate cases
	@Test(expected = RuntimeException.class)
	public void testSettingTheSameDefaultMultipleTimesShouldFail() {
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				.defaultValue(DefTestGroup.strProp1, "AAA")	/* set a default val */
				.defaultValue(DefTestGroup.strProp1, "BBB")	/* This should fail */
				.reloadForNonPropduction(reloader);
		
	}
	
	@Test(expected = RuntimeException.class)
	public void testSettingTheSameDefaultMultipleTimesInASingleListShouldFail() {
		
		ArrayList<PropertyValue> defPvs = new ArrayList();
		defPvs.add(new PropertyValue(DefTestGroup.strProp1, "AAA"));
		defPvs.add(new PropertyValue(DefTestGroup.strProp1, "BBB"));
		
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				.defaultValues(defPvs)	/* should fail b/c the list has dup props */
				.reloadForNonPropduction(reloader);
		
	}
	
	@Test(expected = RuntimeException.class)
	public void testSettingTheSameForcedMultipleTimesShouldFail() {
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				.forceValue(DefTestGroup.strProp1, "AAA")
				.forceValue(DefTestGroup.strProp1, "BBB")	/* This should fail */
				.reloadForNonPropduction(reloader);
		
	}
	
	@Test(expected = RuntimeException.class)
	public void testSettingTheSameForcedMultipleTimesInASingleListShouldFail() {
		
		ArrayList<PropertyValue> forcePvs = new ArrayList();
		forcePvs.add(new PropertyValue(DefTestGroup.strProp1, "AAA"));
		forcePvs.add(new PropertyValue(DefTestGroup.strProp1, "BBB"));
		
		AndHow.builder().namingStrategy(new BasicNamingStrategy())
				.loader(new CmdLineLoader())
				.group(DefTestGroup.class)
				.forceValues(forcePvs)	/* should fail b/c the list has dup props */
				.reloadForNonPropduction(reloader);
		
	}
	

}
