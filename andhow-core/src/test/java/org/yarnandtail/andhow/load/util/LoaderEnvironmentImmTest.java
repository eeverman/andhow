package org.yarnandtail.andhow.load.util;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.JndiContextWrapper;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoaderEnvironmentImmTest {

	public static final StrProp STR_1 = StrProp.builder().build();

	@Test
	public void happyPath() {
		HashMap<String, String> envVars = new HashMap<>();
		envVars.put("env", "vars");

		HashMap<String, String> sysProps = new HashMap<>();
		sysProps.put("sys", "props");

		ArrayList<String> mainArgs = new ArrayList<>();
		mainArgs.add("main=args");

		HashMap<String, Object> fixedNamedVals = new HashMap<>();
		fixedNamedVals.put("fixed", "vals");
		fixedNamedVals.put("object", this);

		List<PropertyValue<?>> fixedPropertyVals = new ArrayList<>();
		fixedPropertyVals.add(new PropertyValue(STR_1, "str1"));

		TestJndiContextSupplier jndiSupplier = new TestJndiContextSupplier();

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(envVars, sysProps, mainArgs, fixedNamedVals, fixedPropertyVals, jndiSupplier);

		assertTrue(envVars.equals(le.getEnvVars()));
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvVars().put("a", "b"));

		assertTrue(sysProps.equals(le.getSysProps()));
		assertThrows(UnsupportedOperationException.class, () -> le.getSysProps().put("a", "b"));

		assertThat(le.getCmdLineArgs(), Matchers.containsInAnyOrder(mainArgs.toArray(new String[1])));
		assertThrows(UnsupportedOperationException.class, () -> le.getCmdLineArgs().add("a=b"));

		assertTrue(fixedNamedVals.equals(le.getFixedNamedValues()));
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedNamedValues().put("a", "b"));

		assertTrue(fixedPropertyVals.equals(le.getFixedPropertyValues()));
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedPropertyValues().add(new PropertyValue<>(STR_1, "blah")));

		assertSame(TestJndiContextSupplier.TEST_EXCEPTION, le.getJndiContext().getException());
	}


	@Test
	public void nullValuesShouldResultInEmptyCollections() {

		JndiContextSupplier.DefaultJndiContextSupplier jndiSupplier = new JndiContextSupplier.DefaultJndiContextSupplier();

		LoaderEnvironmentImm le = new LoaderEnvironmentImm(null, null, null, null, null, jndiSupplier);

		assertEquals(0, le.getEnvVars().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getEnvVars().put("a", "b"));

		assertEquals(0, le.getSysProps().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getSysProps().put("a", "b"));

		assertEquals(0, le.getCmdLineArgs().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getCmdLineArgs().add("a=b"));

		assertEquals(0, le.getFixedNamedValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedNamedValues().put("a", "b"));

		assertEquals(0, le.getFixedPropertyValues().size());
		assertThrows(UnsupportedOperationException.class, () -> le.getFixedPropertyValues().add(new PropertyValue<>(STR_1, "blah")));
	}

	@Test
	public void nullJndiSupplierShouldThrowIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> new LoaderEnvironmentImm(null, null, null, null, null, null));
	}


	/**
	 * A JndiContextSupplier that can be used in the LoaderEnvironmentBuilder that
	 * uses a distinct Exception in the JndiContextWrapper so its use can be
	 * detected in the LoaderEnvironmentImm when the Supplier is no longer present
	 * (only the wrapper returned from get is present, which will have the distinct
	 * exception).
	 */
	public static class TestJndiContextSupplier implements Supplier<JndiContextWrapper> {

		//Marker exception to verify a TestJndiContextSupplier is in use.
		public static final Exception TEST_EXCEPTION = new Exception();

		@Override
		public JndiContextWrapper get() {
			return new JndiContextWrapperImpl(TEST_EXCEPTION);
		}
	}

}