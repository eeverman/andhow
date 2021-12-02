package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LoaderEnvironmentImmTest {

	public static final StrProp STR_1 = StrProp.builder().build();

	@Test
	public void happyPath() {
		HashMap<String, String> envVars = new HashMap<>();
		envVars.put("env", "vars");

		Properties sysProps = new Properties();
		sysProps.put("sys", "props");

		ArrayList<String> mainArgs = new ArrayList<>();
		mainArgs.add("main=args");

		HashMap<String, Object> fixedNamedVals = new HashMap<>();
		fixedNamedVals.put("fixed", "vals");
		fixedNamedVals.put("object", this);

		List<PropertyValue<?>> fixedPropertyVals = new ArrayList<>();
		fixedPropertyVals.add(new PropertyValue(STR_1, "str1"));

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(envVars, sysProps, mainArgs, fixedNamedVals, fixedPropertyVals);

		assertEquals("vars", le.getEnvironmentVariables().get("env"));
		assertEquals(1, le.getEnvironmentVariables().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvironmentVariables().put("a", "b"));

		assertEquals("props", le.getSystemProperties().get("sys"));
		assertEquals(1, le.getSystemProperties().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getSystemProperties().put("a", "b"));

		assertEquals("main=args", le.getMainArgs().get(0));
		assertEquals(1, le.getMainArgs().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getMainArgs().add("a=b"));

		assertEquals("vals", le.getFixedNamedValues().get("fixed"));
		assertSame(this, le.getFixedNamedValues().get("object"));
		assertEquals(2, le.getFixedNamedValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedNamedValues().put("a", "b"));

		assertEquals("str1", le.getFixedPropertyValues().get(0).getValue());
		assertSame(STR_1, le.getFixedPropertyValues().get(0).getProperty());
		assertEquals(1, le.getFixedPropertyValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedPropertyValues().add(new PropertyValue<>(STR_1, "blah")));
	}


	@Test
	public void nullValuesShouldResultInEmptyCollections() {

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(null, null, null, null, null);

		assertEquals(0, le.getEnvironmentVariables().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvironmentVariables().put("a", "b"));

		assertEquals(0, le.getSystemProperties().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getSystemProperties().put("a", "b"));

		assertEquals(0, le.getMainArgs().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getMainArgs().add("a=b"));

		assertEquals(0, le.getFixedNamedValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedNamedValues().put("a", "b"));

		assertEquals(0, le.getFixedPropertyValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedPropertyValues().add(new PropertyValue<>(STR_1, "blah")));
	}

}