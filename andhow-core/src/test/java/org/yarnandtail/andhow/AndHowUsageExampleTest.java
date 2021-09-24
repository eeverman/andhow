package org.yarnandtail.andhow;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.internal.RequirementProblem;
import static org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.property.*;

import org.yarnandtail.andhow.export.ExportCollector;

import java.util.Map;
import java.util.stream.Collectors;

public class AndHowUsageExampleTest extends AndHowTestBase {

	AndHowConfiguration config;

	@BeforeEach
	public void setup() {

		config = AndHowTestConfig.instance()
				//addOverrideGroup is a special test-only method that explicitly sets the classes/groups
				//in use for a test so auto-discovery of groups is not done.
				.addOverrideGroup(UI_CONFIG.class)
				.addOverrideGroup(SERVICE_CONFIG.class)
				//Set the values
				.addFixedValue(UI_CONFIG.DISPLAY_NAME, "My App")
				.addFixedValue(UI_CONFIG.BACKGROUP_COLOR, "ffffff")
				.addFixedValue(SERVICE_CONFIG.REST_ENDPOINT_URL, "google.com")
				.addFixedValue(SERVICE_CONFIG.RETRY_COUNT, 4)
				.addFixedValue(SERVICE_CONFIG.TIMEOUT_SECONDS, 10)
				.addFixedValue(SERVICE_CONFIG.GRAVITY, 9.8)
				.addFixedValue(SERVICE_CONFIG.PIE, 3.14159);
	}

	@Test
	public void testAllValuesAreSet() {

		AndHow.setConfig(config);

		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertEquals("ffffff", UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("google.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(4, SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(10, SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
		assertEquals(9.8, SERVICE_CONFIG.GRAVITY.getValue(), .00000001d);
		assertEquals(3.14159, SERVICE_CONFIG.PIE.getValue(), .00000001d);
	}

	@Test
	public void testOptionalValuesAreNotset() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(UI_CONFIG.class)
				.addOverrideGroup(SERVICE_CONFIG.class)
				.addFixedValue(UI_CONFIG.DISPLAY_NAME, "My App")
				.addFixedValue(SERVICE_CONFIG.REST_ENDPOINT_URL, "yahoo.com")
				.addFixedValue(SERVICE_CONFIG.TIMEOUT_SECONDS, 99);

		AndHow.setConfig(config);

		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertNull(UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("yahoo.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(3, SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(99, SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}

	@Test
	public void testMissingValuesException() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(UI_CONFIG.class)
				.addOverrideGroup(SERVICE_CONFIG.class);

			AndHow.setConfig(config);

		AppFatalException ex = assertThrows(AppFatalException.class, () -> AndHow.instance());

		assertEquals(3, ex.getProblems().filter(RequirementProblem.class).size());
		assertEquals(UI_CONFIG.DISPLAY_NAME, ex.getProblems().filter(RequirementProblem.class).get(0).getPropertyCoord().getProperty());
		assertEquals(SERVICE_CONFIG.REST_ENDPOINT_URL, ex.getProblems().filter(RequirementProblem.class).get(1).getPropertyCoord().getProperty());
		assertEquals(SERVICE_CONFIG.TIMEOUT_SECONDS, ex.getProblems().filter(RequirementProblem.class).get(2).getPropertyCoord().getProperty());

	}

	@Test
	public void testExportToStringMapWithAllValuesSet() throws IllegalAccessException {

		AndHow.setConfig(config);

		Map<String, String> export = AndHow.instance().export(UI_CONFIG.class, SERVICE_CONFIG.class).collect(ExportCollector.stringMap());

		//UI
		assertThat(export, hasEntry(UI_CONFIG.class.getCanonicalName() + ".DISPLAY_NAME", "My App"));
		assertThat(export, hasEntry(UI_CONFIG.class.getCanonicalName() + ".BACKGROUP_COLOR", "ffffff"));

		//SVS
		assertThat(export, hasEntry("re_url", "google.com"));
		assertThat(export, hasEntry("rc_1", "4"));
		assertThat(export, hasEntry("rc_2", "4"));
		assertThat(export, hasEntry("ts", "10"));
		assertThat(export, hasEntry("g", "9.8"));
		assertThat(export, hasEntry(SERVICE_CONFIG.class.getCanonicalName() + ".PIE", "3.14159"));
		assertEquals(8, export.size());
	}

	@Test
	public void testExportToObjectMapWithAllValuesSet() throws IllegalAccessException {

		AndHow.setConfig(config);

		Map<String, Object> export = AndHow.instance().export(UI_CONFIG.class, SERVICE_CONFIG.class).collect(ExportCollector.objectMap());

		//UI
		assertThat(export, hasEntry(UI_CONFIG.class.getCanonicalName() + ".DISPLAY_NAME", "My App"));
		assertThat(export, hasEntry(UI_CONFIG.class.getCanonicalName() + ".BACKGROUP_COLOR", "ffffff"));

		//SVS
		assertThat(export, hasEntry("re_url", "google.com"));
		assertThat(export, hasEntry("rc_1", 4)); //These are object values now instead of strings
		assertThat(export, hasEntry("rc_2", 4));
		assertThat(export, hasEntry("ts", 10));
		assertThat(export, hasEntry("g", 9.8d));
		assertThat(export, hasEntry(SERVICE_CONFIG.class.getCanonicalName() + ".PIE", 3.14159d));
		assertEquals(8, export.size());
	}

	@Test
	public void testExportToObjectMapWithAllValuesSetModifyNames() throws IllegalAccessException {

		AndHow.setConfig(config);

		Map<String, Object> export = AndHow.instance().export(SERVICE_CONFIG.class)
				.map(p -> p.mapNames(p.getExportNames().stream().map(n -> "aaa_" + n).collect(Collectors.toList())))
				.collect(ExportCollector.objectMap());


		//SVS
		assertThat(export, hasEntry("aaa_re_url", "google.com"));
		assertThat(export, hasEntry("aaa_rc_1", 4)); //These are object values now instead of strings
		assertThat(export, hasEntry("aaa_rc_2", 4));
		assertThat(export, hasEntry("aaa_ts", 10));
		assertThat(export, hasEntry("aaa_g", 9.8d));
		assertThat(export, hasEntry("aaa_" + SERVICE_CONFIG.class.getCanonicalName() + ".PIE", 3.14159d));
		assertEquals(6, export.size());
	}

	@ManualExportAllowed(useCanonicalName = EXPORT_CANONICAL_NAME.ALWAYS, useOutAliases = EXPORT_OUT_ALIASES.NEVER)
	interface UI_CONFIG {
		StrProp DISPLAY_NAME = StrProp.builder().mustBeNonNull().build();
		StrProp BACKGROUP_COLOR = StrProp.builder().build();
	}

	@ManualExportAllowed //Defaults to EXPORT_OUT_ALIASES.ALWAYS & EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS
	interface SERVICE_CONFIG {
		StrProp REST_ENDPOINT_URL = StrProp.builder().mustBeNonNull().aliasOut("re_url").build();
		IntProp RETRY_COUNT = IntProp.builder().defaultValue(3).aliasOut("rc_1").aliasInAndOut("rc_2").build();
		IntProp TIMEOUT_SECONDS = IntProp.builder().mustBeNonNull().aliasIn("ignoreForExport").aliasOut("ts").build();
		DblProp GRAVITY = DblProp.builder().greaterThan(9.1d).aliasInAndOut("g").lessThan(10.2d).build();
		DblProp PIE = DblProp.builder().greaterThanOrEqualTo(3.1).lessThanOrEqualTo(3.2).build();
	}

}
