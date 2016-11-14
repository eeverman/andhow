package yarnandtail.andhow.staticparam;

import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eeverman
 */
public class SimpleTest {
	
	@Test
	public void testAssingingValues() {
		
		HashMap<ConfigPoint, String> start = new HashMap();
		
		start.put(SimpleParams.KVP_BOB, "test");
		start.put(SimpleParams.KVP_NULL, "not_null");
		start.put(SimpleParams.FLAG_TRUE, "false");
		start.put(SimpleParams.FLAG_FALSE, "true");
		start.put(SimpleParams.FLAG_NULL, "true");
		AppConfig.reset(start);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
	}
	
	@Test
	public void testDefaultValues() {
		
		AppConfig.reset(null);
		
		assertEquals("bob", SimpleParams.KVP_BOB.getValue());
		assertNull(SimpleParams.KVP_NULL.getValue());
		assertTrue(SimpleParams.FLAG_TRUE.getValue());
		assertFalse(SimpleParams.FLAG_FALSE.getValue());
		assertNull(SimpleParams.FLAG_NULL.getValue());
	}
	

}
