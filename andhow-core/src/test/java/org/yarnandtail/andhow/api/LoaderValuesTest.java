package org.yarnandtail.andhow.api;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.load.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.yarnandtail.andhow.property.StrProp;

/**
 * @author eeverman
 */
public class LoaderValuesTest {
	
	private static final String CLASSPATH_TO_USE = "/org/yarnandtail/andhow/load/LoaderValuesTest_SimpleParams.properties";

	public static interface TestProps {
		StrProp CLAZZ_PATH = StrProp.builder().defaultValue(CLASSPATH_TO_USE).build();
	}
	
	@Test
	public void testNullHandlingOfValues() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, null));
		LoaderValues existing = new LoaderValues(new MapLoader(), evl, new ProblemList<Problem>());
		
		assertFalse(existing.isExplicitlySet(TestProps.CLAZZ_PATH));
		assertNull(existing.getExplicitValue(TestProps.CLAZZ_PATH));
		assertEquals(CLASSPATH_TO_USE, existing.getValue(TestProps.CLAZZ_PATH));
		
	}

	
}
