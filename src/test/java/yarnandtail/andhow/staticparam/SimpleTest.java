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
	public void testParamType() {
		
		HashMap<ConfigPoint, String> start = new HashMap();
		
		start.put(SimpleParams.MY_KVP, "test");
		AppConfig.reset(start);
		
		assertEquals("test", SimpleParams.MY_KVP.getValue());

	}
	

}
