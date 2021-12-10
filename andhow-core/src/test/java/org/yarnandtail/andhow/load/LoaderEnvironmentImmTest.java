package org.yarnandtail.andhow.load;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
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

		assertTrue(envVars.equals(le.getEnvironmentVariables()));
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvironmentVariables().put("a", "b"));

		assertTrue(sysProps.equals(le.getSystemProperties()));
		assertThrows(UnsupportedOperationException.class, () -> le.getSystemProperties().put("a", "b"));

		assertThat(le.getCmdLineArgs(), Matchers.containsInAnyOrder(mainArgs.toArray(new String[1])));
		assertThrows(UnsupportedOperationException.class, () -> le.getCmdLineArgs().add("a=b"));

		assertTrue(fixedNamedVals.equals(le.getFixedNamedValues()));
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedNamedValues().put("a", "b"));

		assertTrue(fixedPropertyVals.equals(le.getFixedPropertyValues()));
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedPropertyValues().add(new PropertyValue<>(STR_1, "blah")));
	}


	@Test
	public void nullValuesShouldResultInEmptyCollections() {

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(null, (Properties)null, null, null, null);

		assertEquals(0, le.getEnvironmentVariables().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvironmentVariables().put("a", "b"));

		assertEquals(0, le.getSystemProperties().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getSystemProperties().put("a", "b"));

		assertEquals(0, le.getCmdLineArgs().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getCmdLineArgs().add("a=b"));

		assertEquals(0, le.getFixedNamedValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedNamedValues().put("a", "b"));

		assertEquals(0, le.getFixedPropertyValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedPropertyValues().add(new PropertyValue<>(STR_1, "blah")));
	}

}