package yarnandtail.andhow;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eeverman
 */
public class AppConfigTest {
	@Test
	public void testStream() {
		ArrayList<String> list = new ArrayList();
		list.add("ZAZ");
		list.add("ZBZ");
		list.add("ZCZ");
		
		boolean containsB = list.stream().anyMatch(i -> i.contains("B"));
		boolean containsZ = list.stream().anyMatch(i -> i.contains("Z"));
		boolean containsZCZ = list.stream().anyMatch(i -> i.contains("ZCZ"));
		boolean containsXYZ = list.stream().anyMatch(i -> i.contains("XYZ"));
		
		assertTrue(containsB);
		assertTrue(containsZ);
		assertTrue(containsZCZ);
		assertFalse(containsXYZ);
	}
	
}
