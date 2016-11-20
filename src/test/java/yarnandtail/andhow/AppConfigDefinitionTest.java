package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Test;
import yarnandtail.andhow.AppConfigDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AppConfigDefinitionTest {
	
	String paramFullPath = SimpleParamsWAlias.class.getCanonicalName() + ".";
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));

		//Canonical Names for Point
		assertEquals(paramFullPath + "KVP_BOB", appDef.getCanonicalName(SimpleParamsWAlias.KVP_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParamsWAlias.FLAG_FALSE));
		
		//Get points for Canonical name and alias
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + "KVP_BOB"));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + "FLAG_FALSE"));
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0)));
		
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
		assertEquals(0, appDef.getPointsForGroup(SimpleParamsNoAlias.class).size());
	}
}
