package org.yarnandtail.andhow.load;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoaderEnvironmentBuilderTest {

	public static final StrProp STR_1 = StrProp.builder().build();


	@Test
	public void happyPathToImmutable() {
		HashMap<String, String> envVars = new HashMap<>();
		envVars.put("env", "vars");

		HashMap<String, String> sysProps = new HashMap();
		sysProps.put("sys", "props");

		String[] mainArgs = new String[] { "main=args" };

		HashMap<String, Object> fixedNamedVals = new HashMap<>();
		fixedNamedVals.put("fixed", "vals");
		fixedNamedVals.put("object", this);

		List<PropertyValue<?>> fixedPropertyVals = new ArrayList<>();
		fixedPropertyVals.add(new PropertyValue(STR_1, "str1"));

		LoaderEnvironmentBuilder leb = new LoaderEnvironmentBuilder();

		leb.setEnvVars(envVars);
		leb.setSysProps(sysProps);
		leb.setMainArgs(mainArgs);
		leb.setFixedNamedValues(fixedNamedVals);
		leb.setFixedPropertyValues(fixedPropertyVals);

		assertTrue(envVars.equals(leb.getEnvironmentVariables()));
		assertTrue(sysProps.equals(leb.getSystemProperties()));
		assertThat(leb.getMainArgs(), Matchers.containsInAnyOrder(mainArgs));
		assertTrue(fixedNamedVals.equals(leb.getFixedNamedValues()));
		assertTrue(fixedPropertyVals.equals(leb.getFixedPropertyValues()));


		//
		//  Check conversion to immutable

		LoaderEnvironmentImm le = leb.toImmutable();

		assertTrue(envVars.equals(le.getEnvironmentVariables()));
		assertTrue(sysProps.equals(le.getSystemProperties()));
		assertThat(le.getMainArgs(), Matchers.containsInAnyOrder(mainArgs));
		assertTrue(fixedNamedVals.equals(le.getFixedNamedValues()));
		assertTrue(fixedPropertyVals.equals(le.getFixedPropertyValues()));

	}


	@Test
	public void nullValuesHandledCorrectlyForToImmutable() {

		LoaderEnvironmentBuilder leb = new LoaderEnvironmentBuilder();

		//
		//  Check conversion to immutable

		LoaderEnvironmentImm le = leb.toImmutable();

		assertTrue(System.getenv().equals(le.getEnvironmentVariables()));
		assertTrue(System.getProperties().equals(le.getSystemProperties()));
		assertEquals(0, le.getMainArgs().size());
		assertEquals(0, le.getFixedNamedValues().size());
		assertEquals(0, le.getFixedPropertyValues().size());


	}

}