package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.load.*;
import java.util.*;
import org.junit.*;

import static org.junit.Assert.*;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.PropertyGroup;

/**
 * @author eeverman
 */
public class LoaderValueTest {
	
	private static final String CLASSPATH_TO_USE = "/org/yarnandtail/andhow/load/SimpleParams1.properties";

	public static interface TestProps extends PropertyGroup {
		StrProp CLAZZ_PATH = StrProp.builder().defaultValue(CLASSPATH_TO_USE).build();
	}
	
	@Test
	public void testNullHandlingOfValues() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, null));
		LoaderValues existing = new LoaderValues(new StringArgumentLoader(new String[]{}), evl, new ProblemList<Problem>());
		
		assertFalse(existing.isExplicitlySet(TestProps.CLAZZ_PATH));
		assertNull(existing.getExplicitValue(TestProps.CLAZZ_PATH));
		assertEquals(CLASSPATH_TO_USE, existing.getValue(TestProps.CLAZZ_PATH));
		
	}

	
}
