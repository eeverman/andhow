/*
 */
package org.yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.StdConfig.StdConfigImpl;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.DblProp;
import org.yarnandtail.andhow.property.LngProp;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author ericeverman
 */
public class StdConfigTest {
	
	public StdConfigTest() {
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
		
		std.setStandardLoaders(BaseConfig.getDefaultLoaderList());
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

		//These properties don't really exist - no checking is done until loading, when
		//the Loader attempts to match up the name w/ a property.  For now this just tests
		//the logic in StdConfig.
		config.addFixedValue("MY_STR_1", "ABC");
		config.addFixedValue("MY_LNG_2", 23L);

		assertEquals(2, config.getFixedKeyObjectPairValues().size());
		assertTrue(containsNameAndValue(config.getFixedKeyObjectPairValues(), "MY_STR_1", "ABC"));
		assertTrue(containsNameAndValue(config.getFixedKeyObjectPairValues(), "MY_LNG_2", 23L));

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

		String[] args = new String[] {"abc=123", "xyz='456"};
		config.setCmdLineArgs(args);
		assertThat(config.getCmdLineArgs().toArray(), arrayContainingInAnyOrder(args));
	}

	<T> boolean containsPropertyAndValue(List<PropertyValue> propertyValues, Property<T> property, T value) {
		PropertyValue pv = propertyValues.stream().filter(p -> p.getProperty().equals(property)).findFirst().get();
		return pv != null && pv.getValue().equals(value);
	}

	boolean containsNameAndValue(List<KeyObjectPair> keyObjectPairs, String name, Object value) {
		KeyObjectPair kop = keyObjectPairs.stream().filter(p -> p.getName().equals(name)).findFirst().get();
		return kop != null && kop.getValue().equals(value);
	}

	/**
	 * Custom StdConfig class that has access methods for fields not otherwise accessable.
	 */
	public static final class MyStdConfig extends StdConfig.StdConfigAbstract<MyStdConfig> {
		public List<PropertyValue> getFixedValues() {
			return _fixedVals;
		}

		public List<KeyObjectPair> getFixedKeyObjectPairValues() {
			return _fixedKeyObjectPairVals;
		}

		public List<String> getCmdLineArgs() {
			return _cmdLineArgs;
		}
	}
	
}
