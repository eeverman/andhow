package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import org.junit.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.Assert.*;

/**
 * 
 * @author ericeverman
 */
public class AndHowTest {
	
	public static final StrProp MY_STR_PROP1 = StrProp.builder().aliasIn("msp1") .build();
	public static final StrProp MY_STR_PROP2 = StrProp.builder().aliasOut("msp2_out").defaultValue("val2").build();
	
	public static final String PERMISSION_MSG = 
			"There is some type of permissions/access error while trying to access and modify"
			+ "private fields during testing. "
			+ "Is there a security manager enforcing security during testing?";
	

	private AndHow originalAndHowInstance;
	
	@Before
	public void clearAndHow() {
		originalAndHowInstance = setAndHowInstance(null);
	}
	
	@After
	public void restoreAndHow() {
		setAndHowInstance(originalAndHowInstance);
	}
	
	/**
	 * Test of findConfig method, of class AndHow.
	 * This method is mostly pass-thru to a util method, so just a smoke test here.
	 */
	@Test
	public void testFindConfig() {
		
		
		AndHowConfiguration<? extends AndHowConfiguration> config1 = AndHow.findConfig();
		AndHowConfiguration<? extends AndHowConfiguration> config2 = AndHow.findConfig();
		
		assertNotEquals("Should return a new instance each time", config1, config2);
		assertFalse("findConfig should not force initialization", AndHow.isInitialize());
	}

	/**
	 * Test of instance method, of class AndHow.
	 */
	@Test
	public void testInstance_0args() {
		assertFalse(AndHow.isInitialize());
		assertNotNull(AndHow.instance());
		assertTrue(AndHow.isInitialize());
	}

	/**
	 * Test of instance method, of class AndHow.
	 */
	@Test
	public void testInstance_AndHowConfiguration() {
		AndHowConfiguration<? extends AndHowConfiguration> config = AndHow.findConfig();
		config.addFixedValue(MY_STR_PROP1, "val");
		AndHow inst = AndHow.instance(config);
		
		assertEquals("val", MY_STR_PROP1.getValue());
		assertEquals("val", inst.getValue(MY_STR_PROP1));
		assertTrue(inst.isExplicitlySet(MY_STR_PROP1));
		assertEquals("val", inst.getExplicitValue(MY_STR_PROP1));
		assertEquals(1, inst.getAliases(MY_STR_PROP1).size());
		assertEquals("MSP1", inst.getAliases(MY_STR_PROP1).get(0).getEffectiveInName());
		assertEquals("org.yarnandtail.andhow.AndHowTest.MY_STR_PROP1", inst.getCanonicalName(MY_STR_PROP1));
		assertEquals(this.getClass(), inst.getGroupForProperty(MY_STR_PROP1).getProxiedGroup());
		
		assertEquals("val2", MY_STR_PROP2.getValue());
		assertEquals("val2", inst.getValue(MY_STR_PROP2));
		assertFalse(inst.isExplicitlySet(MY_STR_PROP2));
		assertNull(inst.getExplicitValue(MY_STR_PROP2));
		assertEquals(1, inst.getAliases(MY_STR_PROP2).size());
		assertEquals("msp2_out", inst.getAliases(MY_STR_PROP2).get(0).getEffectiveOutName());
		assertEquals("org.yarnandtail.andhow.AndHowTest.MY_STR_PROP2", inst.getCanonicalName(MY_STR_PROP2));
		assertEquals(this.getClass(), inst.getGroupForProperty(MY_STR_PROP2).getProxiedGroup());

		assertTrue(inst.getNamingStrategy() instanceof CaseInsensitiveNaming);
	}

	
	public static AndHow setAndHowInstance(AndHow newInstance) {

		try {

			Field ahInstanceField = AndHow.class.getDeclaredField("singleInstance");
			ahInstanceField.setAccessible(true);

			AndHow oldInstance = (AndHow)(ahInstanceField.get(null));
			ahInstanceField.set(null, newInstance);
			
			return oldInstance;

		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
		
	}
	
}
