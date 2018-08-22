/*
 */
package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import org.junit.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.Assert.*;

/**
 * This test is a minimal unit teat because this class is not really testable
 * as a unit test.
 * <br>
 * See the AndHowSystem Test sub-project for better test coverage.
 * 
 * @author ericeverman
 */
public class AndHowTest {
	
	@Test
	public void testIsInitialize() {
		assertFalse(AndHow.isInitialize());
	}
	
}
