package yarnandtail.andhow.name;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import yarnandtail.andhow.PropertyNaming;
import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.SimpleParams;

/**
 *
 * @author eeverman
 */
public class BasicNamingStrategyTest {

	//Using SimpleParams as an arbitrary group to use for naming
	final String groupFullPath = SimpleParams.class.getCanonicalName();
		
	//Stateless, so ok to have a single instance
	final BasicNamingStrategy bns = new BasicNamingStrategy();
	
	@Test
	public void testDefaultNaming() {

		StrProp point = StrProp.builder().aliasIn("Mark").aliasInAndOut("Kathy").build();
		PropertyNaming naming = bns.buildNames(point, SimpleParams.class, "Bob");
		
		assertEquals(groupFullPath + ".Bob", naming.getCanonicalName().getActualName());
		assertEquals(groupFullPath.toUpperCase() + ".BOB", naming.getCanonicalName().getEffectiveInName());
		
		//In Names
		assertEquals(2, naming.getInAliases().size());
		assertEquals("Mark", naming.getInAliases().get(0).getActualName());
		assertEquals("MARK", naming.getInAliases().get(0).getEffectiveInName());
		assertEquals("Kathy", naming.getInAliases().get(1).getActualName());
		assertEquals("KATHY", naming.getInAliases().get(1).getEffectiveInName());
		
		//out Names
		assertEquals(1, naming.getOutAliases().size());
		assertEquals("Kathy", naming.getOutAliases().get(0).getActualName());
		assertEquals("Kathy", naming.getOutAliases().get(0).getEffectiveOutName());
	}

}
