package org.yarnandtail.andhow.internal;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.load.std.StdEnvVarLoader;
import org.yarnandtail.andhow.load.std.StdFixedValueLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.api.Exporter.EXPORT_CANONICAL_NAME;
import org.yarnandtail.andhow.api.Exporter.EXPORT_OUT_ALIASES;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AndHowCoreTest {
	AndHowTestConfig.AndHowTestConfigImpl config;
	AndHowCore core;

	// FYI: StrProp values copied from StrPropProps in andhow-core-integration-test
	public interface Conf1 {
		// Don't add to the list w/o updating tests that check for the complete list
		StrProp PROP_0 = StrProp.builder().aliasIn("StrPropProps.PROP_0").aliasInAndOut("StrPropProps.PROP_0.io").build();
		StrProp PROP_20 = StrProp.builder().startsWithIgnoringCase("star").endsWith("ing")
				.matches("[starSTARing \t]+").aliasIn("StrPropProps.PROP_20").build();
		StrProp PROP_30 = StrProp.builder().defaultValue("Star t ing")
				.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+")
				.aliasIn("StrPropProps.PROP_30").build();
		StrProp PROP_100 = StrProp.builder().notNull().aliasIn("StrPropProps.PROP_100").build();
	}

	@GroupExport(
			exportByCanonicalName = EXPORT_CANONICAL_NAME.ALWAYS,
			exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
	public interface Conf2 {
		// Don't add to the list w/o updating tests that check for the complete list
		IntProp PROP_0 = IntProp.builder().aliasInAndOut("IntPropProps.PROP_0").build();
		IntProp PROP_20 = IntProp.builder().greaterThanOrEqualTo(-5).lessThan(1000)
				.aliasIn("IntPropProps.PROP_20").build();
	}



	@BeforeEach
	public void init() throws Exception {
		config = AndHowTestConfig.instance();

	}

	public AndHowCore initCore(AndHowTestConfig.AndHowTestConfigImpl config) {
		core = new AndHowCore(
				config.getNamingStrategy(),
				config.buildLoaders(),
				config.getRegisteredGroups());

		return core;
	}

	@Test
	public void happyPathTest() {
		config.addOverrideGroup(Conf1.class).addOverrideGroup(Conf2.class);

		config.addFixedValue(Conf1.PROP_0, "ab");
		config.addFixedValue(Conf1.PROP_20, "starting");
		config.addFixedValue(Conf1.PROP_30, "STARting");
		config.addFixedValue(Conf1.PROP_100, "anything");

		config.addFixedValue(Conf2.PROP_0, 0);
		config.addFixedValue(Conf2.PROP_20, 20);

		core = initCore(config);

		//
		// ConstructionDefinition Interface

		// Check name and aliases on Conf1.PROP_0
		List<EffectiveName> p0Names = core.getAliases(Conf1.PROP_0);
		assertEquals(2, p0Names.size());
		assertEquals(1, p0Names.stream().filter(n -> n.isOut() && n.getEffectiveOutName().equals("StrPropProps.PROP_0.io")).count());
		assertEquals(1, p0Names.stream().filter(n -> n.isIn() && n.getEffectiveInName().equalsIgnoreCase("StrPropProps.PROP_0.io")).count());
		assertEquals(1, p0Names.stream().filter(n -> n.isIn() && n.getEffectiveInName().equalsIgnoreCase("StrPropProps.PROP_0")).count());
		assertEquals(Conf1.class.getCanonicalName() + ".PROP_0", core.getCanonicalName(Conf1.PROP_0));


		assertSame(Conf1.class, core.getGroupForProperty(Conf1.PROP_0).getProxiedGroup());

		List<Property<?>> conf2Props = core.getPropertiesForGroup(core.getGroupForProperty(Conf2.PROP_0));
		assertThat(conf2Props, Matchers.containsInAnyOrder(Conf2.PROP_0, Conf2.PROP_20));

		assertSame(Conf1.PROP_0, core.getProperty(Conf1.class.getCanonicalName() + ".PROP_0"));
		assertSame(Conf1.PROP_0, core.getProperty("StrPropProps.PROP_0"));
		assertSame(Conf1.PROP_0, core.getProperty("StrPropProps.PROP_0.io"));

		List<Class<?>> groupClasses = core.getPropertyGroups().stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList());

		// This will contain other as well, such as AndHow Options and Loader options
		assertThat(groupClasses, Matchers.hasItems(Conf1.class, Conf2.class));

		assertTrue(core.containsUserGroups());

		// This will contain others as well, such as Props from AndHow Options and Loader options
		assertThat(core.getProperties(),
				Matchers.hasItems(Conf1.PROP_0, Conf1.PROP_20, Conf1.PROP_30, Conf1.PROP_100,
						Conf2.PROP_0, Conf2.PROP_20));

		assertThat(core.getExportGroups().stream().map(e -> e.getGroup().getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(Conf2.class));

		assertTrue(core.getNamingStrategy() instanceof CaseInsensitiveNaming);
	}





	//TODO Move prop not found to separate exception class


}