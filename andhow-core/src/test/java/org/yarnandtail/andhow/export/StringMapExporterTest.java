package org.yarnandtail.andhow.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.export.ExportServiceSample;
import org.yarnandtail.andhow.property.StrProp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class StringMapExporterTest {
	List<PropertyExport> pes;



	@BeforeEach
	void setUp() {
		pes = new ArrayList<>();

		pes.add(new TestPropertyExport(
				ExportServiceSample.AllowMe.STR1, ExportServiceSample.AllowMe.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.NEVER,
				"ExportServiceSample.AllowMe.STR1", "a.str1_value"));
		//This one gets skipped entirely
		pes.add(new TestPropertyExport(
				ExportServiceSample.AllowMe.AllowMe1.STR1, ExportServiceSample.AllowMe.AllowMe1.class,
				Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, Exporter.EXPORT_OUT_ALIASES.NEVER,
				"ExportServiceSample.AllowMe.AllowMe1.STR1", null));
		pes.add(new TestPropertyExport(
				ExportServiceSample.AllowMe.ExportMe1.STR1, ExportServiceSample.AllowMe.ExportMe1.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.NEVER,
				"ExportServiceSample.AllowMe.ExportMe1.STR1", "a.e.str1_value"));
	}

	@Test
	void test() {
		Map<String, String> export = pes.stream().collect(new StringMapExporter());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.STR1", "a.str1_value"));
		assertThat(export, hasEntry("a.e.str1out", "a.e.str1_value"));
		assertThat(export, hasEntry("a.e.str1inandout", "a.e.str1_value"));
	}

	static class TestPropertyExport extends PropertyExportImpl {

		String canName;
		Object value;

		public TestPropertyExport(Property<?> property, Class<?> containingClass,
				Exporter.EXPORT_CANONICAL_NAME canonicalNameOpt,
				Exporter.EXPORT_OUT_ALIASES outAliasOpt,
				String canName, Object value) {
			super(property, containingClass, canonicalNameOpt, outAliasOpt);

			this.canName = canName;
			this.value = value;
		}

		@Override
		public String getCanonicalName() {
			return canName;
		}

		@Override
		public Object getValue() {
			return value;
		}


	}
}