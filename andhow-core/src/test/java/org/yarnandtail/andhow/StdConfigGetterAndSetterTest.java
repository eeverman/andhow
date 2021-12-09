/*
 */
package org.yarnandtail.andhow;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.StdConfig.StdConfigImpl;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.ValidatedValuesWithContextMutable;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.DblProp;
import org.yarnandtail.andhow.property.LngProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.testutil.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author ericeverman
 */
public class StdConfigGetterAndSetterTest {

	public StdConfigGetterAndSetterTest() {
	}

	/**
	 * Test of getNamingStrategy method, of class BaseConfig.
	 */
	@Test
	public void testGetNamingStrategy() {
		StdConfigImpl std = StdConfig.instance();
		assertTrue(std.getNamingStrategy() instanceof CaseInsensitiveNaming);
	}

	/**
	 * Test of buildLoaders method, of class BaseConfig.
	 */
	@Test
	public void testBuildLoadersWithoutAnyChanges() {
		StdConfigImpl std = StdConfig.instance();
		List<Loader> loaders = std.buildLoaders();
		assertEquals(7, loaders.size());
		assertEquals(StdFixedValueLoader.class, loaders.get(0).getClass());
		assertEquals(StdMainStringArgsLoader.class, loaders.get(1).getClass());
		assertEquals(StdSysPropLoader.class, loaders.get(2).getClass());
		assertEquals(StdEnvVarLoader.class, loaders.get(3).getClass());
		assertEquals(StdJndiLoader.class, loaders.get(4).getClass());
		assertEquals(StdPropFileOnFilesystemLoader.class, loaders.get(5).getClass());
		assertEquals(StdPropFileOnClasspathLoader.class, loaders.get(6).getClass());
	}

	@Test
	public void testBuildLoadersWithCustomListOfStandardLoaders() {
		StdConfigImpl std = StdConfig.instance();

		List<Class<? extends StandardLoader>> newLoaders = new ArrayList();
		newLoaders.add(StdFixedValueLoader.class);
		newLoaders.add(StdJndiLoader.class);

		std.setStandardLoaders(newLoaders);
		List<Loader> loaders = std.buildLoaders();
		assertEquals(2, loaders.size());
		assertEquals(StdFixedValueLoader.class, loaders.get(0).getClass());
		assertEquals(StdJndiLoader.class, loaders.get(1).getClass());

		std.setStandardLoaders(std.getDefaultLoaderList());
		loaders = std.buildLoaders();
		assertEquals(7, loaders.size());
		assertEquals(StdFixedValueLoader.class, loaders.get(0).getClass());
		assertEquals(StdPropFileOnClasspathLoader.class, loaders.get(6).getClass());

		std.setStandardLoaders(StdSysPropLoader.class, StdPropFileOnFilesystemLoader.class);
		loaders = std.buildLoaders();
		assertEquals(2, loaders.size());
		assertEquals(StdSysPropLoader.class, loaders.get(0).getClass());
		assertEquals(StdPropFileOnFilesystemLoader.class, loaders.get(1).getClass());
	}

	@Test
	public void testBuildLoadersWithInsertingLoadersBeforeAndAfter() {
		StdConfigImpl std = StdConfig.instance();

		Loader loader1 = new MapLoader();
		Loader loader2 = new KeyValuePairLoader();
		Loader loader3 = new PropFileOnClasspathLoader();
		Loader loader4 = new PropFileOnFilesystemLoader();
		Loader loader5 = new FixedValueLoader();
		Loader loader6 = new MapLoader();
		Loader loader7 = new KeyValuePairLoader();
		Loader loader8 = new PropFileOnClasspathLoader();
		Loader loader9 = new PropFileOnFilesystemLoader();

		//At the beginning of the list
		std.insertLoaderBefore(StdFixedValueLoader.class, loader1);
		std.insertLoaderBefore(StdFixedValueLoader.class, loader2);
		std.insertLoaderAfter(StdFixedValueLoader.class, loader3);
		std.insertLoaderAfter(StdFixedValueLoader.class, loader4);
		std.insertLoaderBefore(StdMainStringArgsLoader.class, loader5);

		//At the end of the list
		std.insertLoaderBefore(StdPropFileOnFilesystemLoader.class, loader6);
		std.insertLoaderAfter(StdPropFileOnFilesystemLoader.class, loader7);
		std.insertLoaderBefore(StdPropFileOnClasspathLoader.class, loader8);
		std.insertLoaderAfter(StdPropFileOnClasspathLoader.class, loader9);

		List<Loader> loaders = std.buildLoaders();
		assertEquals(16, loaders.size());
		assertEquals(loader1, loaders.get(0));
		assertEquals(loader2, loaders.get(1));
		assertEquals(StdFixedValueLoader.class, loaders.get(2).getClass());
		assertEquals(loader3, loaders.get(3));
		assertEquals(loader4, loaders.get(4));
		assertEquals(loader5, loaders.get(5));
		assertEquals(StdMainStringArgsLoader.class, loaders.get(6).getClass());
		assertEquals(StdSysPropLoader.class, loaders.get(7).getClass());
		assertEquals(StdEnvVarLoader.class, loaders.get(8).getClass());
		assertEquals(StdJndiLoader.class, loaders.get(9).getClass());
		assertEquals(loader6, loaders.get(10));
		assertEquals(StdPropFileOnFilesystemLoader.class, loaders.get(11).getClass());
		assertEquals(loader7, loaders.get(12));
		assertEquals(loader8, loaders.get(13));
		assertEquals(StdPropFileOnClasspathLoader.class, loaders.get(14).getClass());
		assertEquals(loader9, loaders.get(15));
	}

	@Test
	public void getRegisteredGroupsShouldReturnNull() {
		MyStdConfig config = new MyStdConfig();
		assertNull(config.getRegisteredGroups(),
				"This should return non-null for test loaders during testing.");
	}

	@Test
	public void FixedValuesBasedOnPropertiesTest() {
		MyStdConfig config = new MyStdConfig();

		StrProp MY_STR_1 = StrProp.builder().build();
		LngProp MY_LNG_2 = LngProp.builder().build();
		DblProp MY_DBL_3 = DblProp.builder().build();

		config.addFixedValue(MY_STR_1, "ABC");
		config.addFixedValue(MY_LNG_2, 23L);

		assertEquals(2, config.getFixedValues().size());
		assertTrue(containsPropertyAndValue(config.getFixedValues(), MY_STR_1, "ABC"));
		assertTrue(containsPropertyAndValue(config.getFixedValues(), MY_LNG_2, 23L));

		//Try to add a duplicate property
		assertThrows(IllegalArgumentException.class, () -> {
			config.addFixedValue(MY_STR_1, "ZZZ");
		});

		assertEquals("ABC",
				config.getFixedValues().stream().filter(p -> p.getProperty().equals(MY_STR_1)).findFirst().get()
						.getValue().toString(),
				"The value set for this Property should be unchanged");

		//Try to add a null property
		assertThrows(IllegalArgumentException.class, () -> {
			config.addFixedValue((Property)null, "ZZZ");
		});

		assertEquals(2, config.getFixedValues().size(), "Still should have two values");

		//Try removing some
		config.removeFixedValue(MY_STR_1);
		assertEquals(1, config.getFixedValues().size());
		assertTrue(containsPropertyAndValue(config.getFixedValues(), MY_LNG_2, 23L));

		//it should be OK and a no-op to attempt to remove a non-existing property
		//...but how would this ever happen??
		config.removeFixedValue(MY_DBL_3);
		assertEquals(1, config.getFixedValues().size());

		config.removeFixedValue(MY_LNG_2);
		assertEquals(0, config.getFixedValues().size());
	}


	@Test
	public void FixedValuesBasedOnNamesTest() {
		MyStdConfig config = new MyStdConfig();

		config.addFixedValue("MY_STR_1", "ABC");
		config.addFixedValue("MY_LNG_2", 23L);

		assertEquals(2, config.getFixedKeyObjectPairValues().size());
		assertTrue(containsNameAndValue(config.getFixedKeyObjectPairValues(), "MY_STR_1", "ABC"));
		assertTrue(containsNameAndValue(config.getFixedKeyObjectPairValues(), "MY_LNG_2", 23L));

		//Try to add a duplicate property
		assertThrows(IllegalArgumentException.class, () -> {
			config.addFixedValue("MY_STR_1", "ZZZ");
		});

		assertEquals("ABC",
				config.getFixedKeyObjectPairValues().entrySet().stream().filter(k -> k.getKey().equals("MY_STR_1")).findFirst().get()
						.getValue().toString(),
				"The value set for this Property should be unchanged");

		//Try to add a null property name
		assertThrows(IllegalArgumentException.class, () -> {
			config.addFixedValue((String)null, "ZZZ");
		});

		//Try to add an empty (all space) property name
		assertThrows(IllegalArgumentException.class, () -> {
			config.addFixedValue("   ", "ZZZ");
		});

		assertEquals(2, config.getFixedKeyObjectPairValues().size(), "Still should have two values");

		//Try removing some
		config.removeFixedValue("MY_STR_1");
		assertEquals(1, config.getFixedKeyObjectPairValues().size());
		assertTrue(containsNameAndValue(config.getFixedKeyObjectPairValues(), "MY_LNG_2", 23L));

		//it should be OK and a no-op to attempt to remove a non-existing property, or a property not in the list.
		config.removeFixedValue("MY_DBL_3");
		assertEquals(1, config.getFixedKeyObjectPairValues().size());

		config.removeFixedValue("MY_LNG_2");
		assertEquals(0, config.getFixedKeyObjectPairValues().size());
	}

	@Test
	public void setCmdLineArgsTest() {
		MyStdConfig config = new MyStdConfig();

		String[] args = new String[] {"arg1", "arg2"};
		config.setCmdLineArgs(args);
		assertThat(config.getCmdLineArgs().toArray(), arrayContainingInAnyOrder(args));

		List<String> kvps = ReflectionTestUtils.getInstanceFieldValue(config.buildStdMainStringArgsLoader(), "keyValuePairs", List.class);

		assertEquals(2, kvps.size());
		assertThat(kvps.toArray(), arrayContainingInAnyOrder(args));

		// Set new values - they should replace the old
		String[] args2 = new String[]{"arg3", "arg4", "arg5"};
		config.setCmdLineArgs(args2);

		List<String> actualArgs = config.getCmdLineArgs();

		assertEquals(args2.length, actualArgs.size());
		assertThat(actualArgs.toArray(), arrayContainingInAnyOrder(args2));

		// Set empty array
		config.setCmdLineArgs(new String[0]);

		actualArgs = config.getCmdLineArgs();

		assertEquals(0, actualArgs.size());

		// Set null
		config.setCmdLineArgs(new String[]{"arg6"});
		config.setCmdLineArgs(null);

		actualArgs = config.getCmdLineArgs();

		assertEquals(0, actualArgs.size());
	}

	@Test
	public void setEnvironmentPropertiesTest() {
		MyStdConfig config = new MyStdConfig();

		Map<String, String> envVars = new HashMap<>();
		envVars.put("abc", "123");
		envVars.put("xyz", "456");

		config.setEnvironmentProperties(envVars);

		assertEquals(2, config.getEnvironmentProperties().size());
		assertTrue(envVars.equals(config.getEnvironmentProperties()));
		assertFalse(envVars == config.getEnvironmentProperties(), "Should be disconnected object");

		//Now try setting new values - they should replace the old
		Map<String, String> envVars2 = new HashMap<>();
		envVars2.put("bob", "bob_val");
		config.setEnvironmentProperties(envVars2);

		assertEquals(1, config.getEnvironmentProperties().size());
		assertTrue(envVars2.equals(config.getEnvironmentProperties()));

		//Now set to null
		config.setEnvironmentProperties(null);
		assertNull(config.getEnvironmentProperties());

	}

	@Test
	public void setClasspathPropFilePathViaStringTest() throws Exception {
		MyStdConfig config = new MyStdConfig();
		ValidatedValuesWithContextMutable vvs = new ValidatedValuesWithContextMutable();
		Class<?> vvsClass = ValidatedValuesWithContext.class;	//class of getEffectivePath argument

		assertNull(config.getClasspathPropFileProp());
		assertNull(config.getClasspathPropFilePath());
		assertEquals("/andhow.properties",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", vvs, vvsClass),
				"Loader should see the default value");

		config.setClasspathPropFilePath("/andhow.test.properties");
		assertEquals("/andhow.test.properties", config.getClasspathPropFilePath());
		assertNull(config.getClasspathPropFileProp());
		assertEquals("/andhow.test.properties",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", vvs, vvsClass),
				"Loader should see this configured value");

		config.setClasspathPropFilePath("/andhow.test.props");
		assertEquals("/andhow.test.props", config.getClasspathPropFilePath());
		assertEquals("/andhow.test.props",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", vvs, vvsClass),
				"Loader should see this configured value");

		assertThrows(IllegalArgumentException.class, () -> {
			config.setClasspathPropFilePath("andhow.test.props");
		}, "paths containing dots must start w/ a slash");

		assertEquals("/andhow.test.props", config.getClasspathPropFilePath(), "Should be unchanged");

		config.setClasspathPropFilePath("/org/comcorp/project/andhow.test.properties");
		assertEquals("/org/comcorp/project/andhow.test.properties", config.getClasspathPropFilePath());
		assertEquals("/org/comcorp/project/andhow.test.properties",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", vvs, vvsClass),
				"Loader should see this configured value");

		config.setClasspathPropFilePath((String)null);
		assertNull(config.getClasspathPropFilePath());
		assertEquals("/andhow.properties",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", vvs, vvsClass),
				"Loader should see the default value");

	}

	@Test
	public void setClasspathPropFilePathViaStrPropTest() throws Exception {
		StrProp MY_PATH_PROPERTY = StrProp.builder().build();

		MyStdConfig config = new MyStdConfig();

		ValidatedValue validatedValue = new ValidatedValue(MY_PATH_PROPERTY, "/my.prop.file");
		List<ValidatedValue> vvList = new ArrayList<>();
		vvList.add(validatedValue);

		LoaderValues loaderValues = new LoaderValues(new FixedValueLoader(), vvList, ProblemList.EMPTY_PROBLEM_LIST);
		ValidatedValuesWithContextMutable validatedValues = new ValidatedValuesWithContextMutable();
		validatedValues.addValues(loaderValues);
		Class<?> vvsClass = ValidatedValuesWithContext.class;	//class of getEffectivePath argument

		config.setClasspathPropFilePath(MY_PATH_PROPERTY);
		assertNull(config.getClasspathPropFilePath());
		assertEquals(MY_PATH_PROPERTY, config.getClasspathPropFileProp());
		assertEquals("/my.prop.file",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", validatedValues, vvsClass),
				"Loader should see this configured value");

		config.setClasspathPropFilePath((StrProp)null);
		assertNull(config.getClasspathPropFilePath());
		assertNull(config.getClasspathPropFileProp());
		assertEquals("/andhow.properties",
				ReflectionTestUtils.stringMethod(config.buildStdPropFileOnClasspathLoader(), "getEffectivePath", validatedValues, vvsClass),
				"Loader should revert to default");
	}

	@Test
	public void setClasspathPropFilePathInteractionOfStringAndStrPropTest() {
		StrProp MY_PATH_PROPERTY = StrProp.builder().build();
		MyStdConfig config = new MyStdConfig();

		config.setClasspathPropFilePath(MY_PATH_PROPERTY);
		assertThrows(IllegalArgumentException.class, () -> {
			config.setClasspathPropFilePath("/some.other.path");
		}, "Can't set via String and StrProp at the same time");
		assertNull(config.getClasspathPropFilePath(), "Should not have set the value due to error");

		config.setClasspathPropFilePath((StrProp)null);
		config.setClasspathPropFilePath("/some.other.path");	//now its OK
		assertEquals("/some.other.path", config.getClasspathPropFilePath());

		assertThrows(IllegalArgumentException.class, () -> {
			config.setClasspathPropFilePath(MY_PATH_PROPERTY);
		}, "String version is set, so now this one causes the error");
		assertNull(config.getClasspathPropFileProp(), "Should not have set the value due to error");

	}

	@Test void classpathPropertiesRequiredOrNotTest() {
		MyStdConfig config = new MyStdConfig();

		assertFalse(config.getClasspathPropFileRequired(), "False should be the default");
		assertFalse(config.buildStdPropFileOnClasspathLoader().isMissingFileAProblem(), "False should be the default");

		config.classpathPropertiesRequired();

		assertTrue(config.getClasspathPropFileRequired());
		assertTrue(config.buildStdPropFileOnClasspathLoader().isMissingFileAProblem());
	}

	@Test void filesystemPropertiesRequiredOrNotTest() {
		MyStdConfig config = new MyStdConfig();

		assertFalse(config.getFilesystemPropFileRequired(), "False should be the default");
		assertFalse(config.buildStdPropFileOnFilesystemLoader().isMissingFileAProblem(), "False should be the default");

		config.filesystemPropFileRequired();

		assertTrue(config.getFilesystemPropFileRequired());
		assertTrue(config.buildStdPropFileOnFilesystemLoader().isMissingFileAProblem());
	}

	<T> boolean containsPropertyAndValue(List<PropertyValue<?>> propertyValues, Property<T> property, T value) {
		PropertyValue pv = propertyValues.stream().filter(p -> p.getProperty().equals(property)).findFirst().get();
		return pv != null && pv.getValue().equals(value);
	}

	boolean containsNameAndValue(Map<String, Object> valueMap, String name, Object value) {
		Object valueInMap = valueMap.entrySet().stream().filter(p -> p.getKey().equals(name)).findFirst().get().getValue();
		return valueInMap != null && valueInMap.equals(value);
	}

	/**
	 * Custom StdConfig class that has access methods for fields not otherwise accessable.
	 */
	public static final class MyStdConfig extends StdConfig.StdConfigAbstract<MyStdConfig> {
		public List<PropertyValue<?>> getFixedValues() {
			return getLoaderEnvironment().getFixedPropertyValues();
		}

		public Map<String, Object> getFixedKeyObjectPairValues() {
			return getLoaderEnvironment().getFixedNamedValues();
		}

		public List<String> getCmdLineArgs() {
			return _cmdLineArgs;
		}

		public Map<String, String> getEnvironmentProperties() {
			return envProperties;
		}

		public String getClasspathPropFilePath() { return classpathPropFilePathStr; }

		public StrProp getClasspathPropFileProp() { return this.classpathPropFilePathProp; }

		public boolean getClasspathPropFileRequired() { return _missingClasspathPropFileAProblem; }

		public boolean getFilesystemPropFileRequired() { return _missingFilesystemPropFileAProblem; }
	}

}
