package org.yarnandtail.andhow.load;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class LoaderEnvironmentBuilderTest {

	public static final StrProp STR_1 = StrProp.builder().build();
	public static final IntProp INT_1 = IntProp.builder().build();

	LoaderEnvironmentBuilder leb;

	HashMap<String, String> envVars;
	HashMap<String, String> sysProps;
	String[] mainArgs;
	HashMap<String, Object> fixedNamedVals;
	List<PropertyValue<?>> fixedPropertyVals;

	@BeforeEach
	public void beforeEach() {
		envVars = new HashMap<>();
		sysProps = new HashMap();
		mainArgs = new String[] { "main=args" };
		fixedNamedVals = new HashMap<>();
		fixedPropertyVals = new ArrayList<>();

		leb = new LoaderEnvironmentBuilder();
	}

	public void populateCollections() {
		envVars.put("env", "vars");
		sysProps.put("sys", "props");
		fixedNamedVals.put("fixed", "vals");
		fixedNamedVals.put("object", this);
		fixedPropertyVals.add(new PropertyValue(STR_1, "str1"));
		fixedPropertyVals.add(new PropertyValue(INT_1, "int1"));
	}


	@Test
	public void happyPathToImmutable() {

		populateCollections();

		leb.setEnvVars(envVars);
		leb.setSysProps(sysProps);
		leb.setCmdLineArgs(mainArgs);
		leb.setFixedNamedValues(fixedNamedVals);
		leb.setFixedPropertyValues(fixedPropertyVals);

		assertTrue(envVars.equals(leb.getEnvironmentVariables()));
		assertTrue(sysProps.equals(leb.getSystemProperties()));
		assertThat(leb.getCmdLineArgs(), Matchers.containsInAnyOrder(mainArgs));
		assertTrue(fixedNamedVals.equals(leb.getFixedNamedValues()));
		assertTrue(fixedPropertyVals.equals(leb.getFixedPropertyValues()));

		//
		//  Check conversion to immutable

		LoaderEnvironmentImm le = leb.toImmutable();

		assertTrue(envVars.equals(le.getEnvironmentVariables()));
		assertTrue(sysProps.equals(le.getSystemProperties()));
		assertThat(le.getCmdLineArgs(), Matchers.containsInAnyOrder(mainArgs));
		assertTrue(fixedNamedVals.equals(le.getFixedNamedValues()));
		assertTrue(fixedPropertyVals.equals(le.getFixedPropertyValues()));

	}


	@Test
	public void nullValuesHandledCorrectlyForToImmutable() {

		//
		//  Check conversion to immutable

		LoaderEnvironmentImm le = leb.toImmutable();

		assertTrue(System.getenv().equals(le.getEnvironmentVariables()));
		assertTrue(System.getProperties().equals(le.getSystemProperties()));
		assertEquals(0, le.getCmdLineArgs().size());
		assertEquals(0, le.getFixedNamedValues().size());
		assertEquals(0, le.getFixedPropertyValues().size());
	}

	@Test
	public void AddNamedFixedValueShouldThrowErrorForDuplicateNames() {
		leb.addFixedValue("MY_STR_1", "AAA");

		//Try to add a duplicate property
		assertThrows(IllegalArgumentException.class, () -> {
			leb.addFixedValue("MY_STR_1", "ZZZ");
		});
	}

	@Test
	public void AddNamedFixedValueShouldThrowErrorForNullName() {
		assertThrows(IllegalArgumentException.class, () -> {
			leb.addFixedValue((String)null, "ZZZ");
		});
	}

	@Test
	public void AddNamedFixedValueShouldThrowErrorForWhitespaceName() {
		assertThrows(IllegalArgumentException.class, () -> {
			leb.addFixedValue("   ", "ZZZ");
		});
	}

	@Test
	public void AddAndRemoveNamedFixedValueShouldWork() {
		leb.addFixedValue("AAA", 99);
		leb.addFixedValue("BBB", "Foo");

		assertEquals(2, leb.getFixedNamedValues().size());
		assertEquals(99, leb.removeFixedValue("AAA"));
		assertEquals(1, leb.getFixedNamedValues().size());
		assertEquals("Foo", leb.getFixedNamedValues().get("BBB"));	//still present
		assertNull(leb.removeFixedValue("AAA"), "Should be a no-op");

		assertEquals("Foo", leb.removeFixedValue("BBB"));
		assertEquals(0, leb.getFixedNamedValues().size());
		assertNull(leb.removeFixedValue("BBB"), "Should be a no-op");
	}

	@Test
	public void AddAndRemoveFixedValueShouldWork() {
		leb.addFixedValue(STR_1, "bob");
		leb.addFixedValue(INT_1, 99);

		assertEquals(2, leb.getFixedPropertyValues().size());
		assertEquals("bob", leb.removeFixedValue(STR_1));
		assertEquals(1, leb.getFixedPropertyValues().size());
		assertEquals(99, leb.getFixedPropertyValues().get(0).getValue());	//still present
		assertNull(leb.removeFixedValue(STR_1), "Should be a no-op");

		assertEquals(99, leb.removeFixedValue(INT_1));
		assertEquals(0, leb.getFixedNamedValues().size());
		assertNull(leb.removeFixedValue(INT_1), "Should be a no-op");
	}


	@Test
	public void setCmdLineArgsTest() {

		String[] args = new String[] {"arg1", "arg2"};
		leb.setCmdLineArgs(args);
		assertThat(leb.getCmdLineArgs(), containsInAnyOrder(args));

		// Set new values - they should replace the old
		String[] args2 = new String[]{"arg3", "arg4", "arg5"};
		leb.setCmdLineArgs(args2);

		List<String> actualArgs = leb.getCmdLineArgs();

		assertThat(actualArgs, containsInAnyOrder(args2));

		// Set empty array
		leb.setCmdLineArgs(new String[0]);
		assertEquals(0, leb.getCmdLineArgs().size());

		// Set null
		leb.setCmdLineArgs(new String[]{"arg6"});
		leb.setCmdLineArgs(null);
		assertEquals(0, leb.getCmdLineArgs().size());
	}



	<T> boolean containsPropertyAndValue(List<PropertyValue<?>> propertyValues, Property<T> property, T value) {
		PropertyValue pv = propertyValues.stream().filter(p -> p.getProperty().equals(property)).findFirst().get();
		return pv != null && pv.getValue().equals(value);
	}

	boolean containsNameAndValue(Map<String, Object> valueMap, String name, Object value) {
		Object valueInMap = valueMap.entrySet().stream().filter(p -> p.getKey().equals(name)).findFirst().get().getValue();
		return valueInMap != null && valueInMap.equals(value);
	}

}