/*
 */
package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.NonProductionConfig.NonProductionConfigImpl;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.load.std.StdFixedValueLoader;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.jupiter.api.Assertions.*;
import static org.yarnandtail.andhow.AndHowNonProductionUtil.PERMISSION_MSG;

/**
 *
 * @author ericeverman
 */
public class NonProductionConfigTest {

	StrProp MY_PROP1 = StrProp.builder().build();

	public static interface Inner {
		StrProp MY_PROP2 = StrProp.builder().build();
	}

	/**
	 * Test of instance method, of class NonProductionConfig.
	 */
	@Test
	public void testInstance() {
		assertNotNull(NonProductionConfig.instance());
		assertTrue(NonProductionConfig.instance() instanceof NonProductionConfig.NonProductionConfigImpl);
	}

	@Test
	public void addCmdLineArgsHappyPath() {
		NonProductionConfigImpl config = NonProductionConfig.instance();

		config.addCmdLineArg("one", "1");

		List<String> args = config.getLoaderEnvironment().getCmdLineArgs();
		assertEquals(1, args.size());
		assertEquals("one=1", args.get(0));

		config.addCmdLineArg("two", "2");
		config.addCmdLineArg("three", null);

		args = config.getLoaderEnvironment().getCmdLineArgs();
		assertEquals(3, args.size());
		assertEquals("one=1", args.get(0));
		assertEquals("two=2", args.get(1));
		assertEquals("three", args.get(2));
	}

	@Test
	public void addCmdLineArgWithNullShouldThrowException() {
		NonProductionConfigImpl config = NonProductionConfig.instance();

		assertThrows(IllegalArgumentException.class, () -> {
			config.addCmdLineArg(null, "one");
		});
	}

	@Test
	public void testGroup() {
		NonProductionConfigImpl config = NonProductionConfig.instance();
		config.addOverrideGroup(NonProductionConfigTest.class);
		config.addOverrideGroup(NonProductionConfigTest.Inner.class);

		List<GroupProxy> proxies = config.getRegisteredGroups();

		assertEquals(2, proxies.size());
		assertEquals(proxies.get(0).getProxiedGroup(), NonProductionConfigTest.class);
		assertEquals(proxies.get(1).getProxiedGroup(), NonProductionConfigTest.Inner.class);
	}

	@Test
	public void testGroups() {
		NonProductionConfigImpl config = NonProductionConfig.instance();
		List<Class<?>> groups = new ArrayList();
		groups.add(NonProductionConfigTest.class);
		groups.add(NonProductionConfigTest.Inner.class);
		config.addOverrideGroups(groups);

		List<GroupProxy> proxies = config.getRegisteredGroups();

		assertEquals(2, proxies.size());
		assertEquals(proxies.get(0).getProxiedGroup(), NonProductionConfigTest.class);
		assertEquals(proxies.get(1).getProxiedGroup(), NonProductionConfigTest.Inner.class);
	}

	@Test
	public void testSetLoaders() {
		NonProductionConfigImpl config = NonProductionConfig.instance();
		MapLoader ml1 = new MapLoader();
		MapLoader ml2 = new MapLoader();
		config.setLoaders(ml1, ml2);

		List<Loader> loaders = config.buildLoaders();

		assertEquals(2, loaders.size());
		assertEquals(ml1, loaders.get(0));
		assertEquals(ml2, loaders.get(1));
	}

	@Test
	public void testBuildLoaders() {
		NonProductionConfigImpl config = NonProductionConfig.instance();
		List<Loader> loaders = config.buildLoaders();	//should be std list
		assertTrue(loaders.get(0) instanceof StdFixedValueLoader);
		//and a bunch more
	}

}
