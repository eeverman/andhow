package yarnandtail.andhow.name;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import yarnandtail.andhow.NamingStrategy.Naming;
import yarnandtail.andhow.property.StrProp;
import yarnandtail.andhow.SimpleParamsWAlias;

/**
 *
 * @author eeverman
 */
public class BasicNamingStrategyTest {

	//Using SimpleParamsWAlias as an arbitrary group to use for naming
	final String groupFullPath = SimpleParamsWAlias.class.getCanonicalName();
		
	//Stateless, so ok to have a single instance
	final BasicNamingStrategy bns = new BasicNamingStrategy();
	
	@Test
	public void testDefaultNaming() {

		StrProp point = StrProp.builder().build();
		Naming naming = bns.buildNames(point, SimpleParamsWAlias.class, "BOB");
		
		assertEquals(groupFullPath + ".BOB", naming.getCanonicalName());
		assertEquals(0, naming.getAliases().size());
	}

}
