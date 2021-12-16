package org.yarnandtail.andhow.internal;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHowTestConfig;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.internal.AndHowIllegalStateException.UnrecognizedPropertyException;
import org.yarnandtail.andhow.api.EffectiveName;
import org.yarnandtail.andhow.api.Exporter.EXPORT_CANONICAL_NAME;
import org.yarnandtail.andhow.api.Exporter.EXPORT_OUT_ALIASES;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.junit5.RestoreSysPropsAfterThisTest;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

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
				config.getLoaderEnvironment(),
				config.getRegisteredGroups());

		return core;
	}

	@Test
	@RestoreSysPropsAfterThisTest
	public void happyPathWithAllValuesAssigned() {
		config.addOverrideGroup(Conf1.class).addOverrideGroup(Conf2.class);

		config.addFixedValue(Conf1.PROP_0, "ab");
		config.addFixedValue(Conf1.PROP_20, "starting");
		config.addFixedValue(Conf1.PROP_30, "STARting");
		config.addFixedValue(Conf1.PROP_100, "anything");

		config.addFixedValue(Conf2.PROP_0, 0);
		config.addFixedValue(Conf2.PROP_20, 20);

		core = initCore(config);

		// Confirm export happened
		assertEquals("0", System.getProperty(Conf2.class.getCanonicalName() + ".PROP_0"));
		assertEquals("0", System.getProperty("IntPropProps.PROP_0"), "Export by alias should happen as well");
		assertEquals("20", System.getProperty(Conf2.class.getCanonicalName() + ".PROP_20"));

		// Confirm export did not happen for other properties / names
		assertNull(System.getProperty(Conf1.class.getCanonicalName() + ".PROP_0"));
		assertNull(System.getProperty("StrPropProps.PROP_0.io"));
		assertNull(System.getProperty("IntPropProps.PROP_20"), "This alias is in-only and should not export");

		// Validated Values Interface

		assertEquals("ab", core.getExplicitValue(Conf1.PROP_0));
		assertEquals("starting", core.getExplicitValue(Conf1.PROP_20));
		assertEquals("STARting", core.getExplicitValue(Conf1.PROP_30));
		assertEquals("anything", core.getExplicitValue(Conf1.PROP_100));
		assertEquals(0, core.getExplicitValue(Conf2.PROP_0));
		assertEquals(20, core.getExplicitValue(Conf2.PROP_20));

		assertTrue(core.isExplicitlySet(Conf1.PROP_0));
		assertTrue(core.isExplicitlySet(Conf1.PROP_20));
		assertTrue(core.isExplicitlySet(Conf1.PROP_30));
		assertTrue(core.isExplicitlySet(Conf1.PROP_100));
		assertTrue(core.isExplicitlySet(Conf2.PROP_0));
		assertTrue(core.isExplicitlySet(Conf2.PROP_20));


		assertEquals("ab", core.getValue(Conf1.PROP_0));
		assertEquals("starting", core.getValue(Conf1.PROP_20));
		assertEquals("STARting", core.getValue(Conf1.PROP_30));
		assertEquals("anything", core.getValue(Conf1.PROP_100));
		assertEquals(0, core.getValue(Conf2.PROP_0));
		assertEquals(20, core.getValue(Conf2.PROP_20));

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


	@Test
	@RestoreSysPropsAfterThisTest
	public void happyPathWithNonRequiredValuesUnassigned() {
		config.addOverrideGroup(Conf1.class).addOverrideGroup(Conf2.class);

		// This is the only required property
		config.addFixedValue(Conf1.PROP_100, "anything");

		core = initCore(config);

		// Validated Values Interface

		assertNull(core.getExplicitValue(Conf1.PROP_0));
		assertNull(core.getExplicitValue(Conf1.PROP_20));
		assertNull(core.getExplicitValue(Conf1.PROP_30));
		assertEquals("anything", core.getExplicitValue(Conf1.PROP_100));
		assertNull(core.getExplicitValue(Conf2.PROP_0));
		assertNull(core.getExplicitValue(Conf2.PROP_20));

		assertFalse(core.isExplicitlySet(Conf1.PROP_0));
		assertFalse(core.isExplicitlySet(Conf1.PROP_20));
		assertFalse(core.isExplicitlySet(Conf1.PROP_30));
		assertTrue(core.isExplicitlySet(Conf1.PROP_100));
		assertFalse(core.isExplicitlySet(Conf2.PROP_0));
		assertFalse(core.isExplicitlySet(Conf2.PROP_20));


		assertNull(core.getValue(Conf1.PROP_0));
		assertNull(core.getValue(Conf1.PROP_20));
		assertEquals("Star t ing", core.getValue(Conf1.PROP_30));
		assertEquals("anything", core.getValue(Conf1.PROP_100));
		assertNull(core.getValue(Conf2.PROP_0));
		assertNull(core.getValue(Conf2.PROP_20));

	}

	@Test
	@RestoreSysPropsAfterThisTest
	public void getValueForAnUnregisteredPropertyIsAnIllegalArg() {
		config.addOverrideGroup(Conf1.class);
		config.addFixedValue(Conf1.PROP_100, "anything");
		core = initCore(config);

		UnrecognizedPropertyException upe = assertThrows(UnrecognizedPropertyException.class,
				() -> core.getValue(Conf2.PROP_0)
		);

		assertSame(Conf2.PROP_0, upe.getProperty());

	}

}