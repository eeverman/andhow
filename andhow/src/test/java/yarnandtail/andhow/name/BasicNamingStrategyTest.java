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
		assertEquals(groupFullPath.toUpperCase() + ".BOB", naming.getCanonicalName().getEffectiveName());
		assertEquals(2, naming.getAllInNames().size());
		assertEquals("Mark", naming.getAllInNames().get(0).getActualName());
		assertEquals("MARK", naming.getAllInNames().get(0).getEffectiveName());
		assertEquals("Kathy", naming.getAllInNames().get(1).getActualName());
		assertEquals("KATHY", naming.getAllInNames().get(1).getEffectiveName());
		assertEquals(3, naming.getAllInNames().size());
		assertEquals(naming.getCanonicalName(), naming.getAllInNames().get(0));
		assertEquals(naming.getAllInNames().get(0), naming.getAllInNames().get(1));
		assertEquals(naming.getAllInNames().get(1), naming.getAllInNames().get(2));
	}

}
