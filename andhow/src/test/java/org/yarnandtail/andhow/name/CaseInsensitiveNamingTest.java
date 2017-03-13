package org.yarnandtail.andhow.name;

import org.yarnandtail.andhow.PropertyNaming;
import org.yarnandtail.andhow.PropertyGroup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class CaseInsensitiveNamingTest {

	//Using SimpleParams as an arbitrary group to use for naming
	final String groupFullPath = SimpleParams.class.getCanonicalName();
		
	//Stateless, so ok to have a single instance
	final CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
	
	@Test
	public void testDefaultNaming() throws Exception {

		PropertyNaming naming = bns.buildNames(SimpleParams.Bob, SimpleParams.class);
		
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
	
	public interface SimpleParams extends PropertyGroup {
		StrProp Bob = StrProp.builder().aliasIn("Mark").aliasInAndOut("Kathy").build();
	}

}
