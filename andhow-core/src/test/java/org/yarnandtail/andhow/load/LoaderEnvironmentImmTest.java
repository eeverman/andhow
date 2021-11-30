package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LoaderEnvironmentImmTest {

	@Test
	public void happyPath() {
		HashMap<String, String> envVars = new HashMap<>();
		envVars.put("env", "vars");

		Properties sysProps = new Properties();
		sysProps.put("sys", "props");

		ArrayList<String> mainArgs = new ArrayList<>();
		mainArgs.add("main=args");

		HashMap<String, Object> fixedVals = new HashMap<>();
		fixedVals.put("fixed", "vals");
		fixedVals.put("object", this);

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(envVars, sysProps, mainArgs, fixedVals);

		assertEquals("vars", le.getEnvironmentVariables().get("env"));
		assertEquals(1, le.getEnvironmentVariables().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvironmentVariables().put("a", "b"));

		assertEquals("props", le.getSystemProperties().get("sys"));
		assertEquals(1, le.getSystemProperties().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getSystemProperties().put("a", "b"));

		assertEquals("main=args", le.getMainArgs().get(0));
		assertEquals(1, le.getMainArgs().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getMainArgs().add("a=b"));

		assertEquals("vals", le.getFixedValues().get("fixed"));
		assertSame(this, le.getFixedValues().get("object"));
		assertEquals(2, le.getFixedValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedValues().put("a", "b"));
	}


	@Test
	public void nullValuesShouldResultInEmptyCollections() {

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(null, null, null, null);

		assertEquals(0, le.getEnvironmentVariables().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvironmentVariables().put("a", "b"));

		assertEquals(0, le.getSystemProperties().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getSystemProperties().put("a", "b"));

		assertEquals(0, le.getMainArgs().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getMainArgs().add("a=b"));

		assertEquals(0, le.getFixedValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedValues().put("a", "b"));
	}

}