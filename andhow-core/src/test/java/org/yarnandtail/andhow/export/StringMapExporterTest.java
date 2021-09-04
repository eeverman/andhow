package org.yarnandtail.andhow.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.internal.export.ExportServiceSample;
import org.yarnandtail.andhow.property.StrProp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class StringMapExporterTest {
	List<PropertyExport> pes;



	@BeforeEach
	void setUp() {
		pes = new ArrayList<>();

		// Mockito spies to override methods of live Property instances
		// These Properties are the ones from ExportServiceSample, but none of the export ann.
		// from that file are in use due to how this is mocked up (not a complete AndHow env.)
		StrProp A_Str1Spy = Mockito.spy(ExportServiceSample.AllowMe.STR1);
		StrProp AA_Str1Spy = Mockito.spy(ExportServiceSample.AllowMe.AllowMe1.STR1);
		StrProp AUA_Str1Spy = Mockito.spy(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.STR1);
		StrProp EA_Str1Spy = Mockito.spy(ExportServiceSample.ExportMe.AllowMe1.STR1);


		//
		Mockito.doReturn("ExportServiceSample.AllowMe.STR1").when(A_Str1Spy).getCanonicalName();
		Mockito.doReturn("a.str1_value").when(A_Str1Spy).getValue();
		Mockito.doReturn(Arrays.asList("str1out", "str1inandout")).when(A_Str1Spy).getOutAliases();
		//
		Mockito.doReturn("ExportServiceSample.AllowMe.AllowMe1.STR1").when(AA_Str1Spy).getCanonicalName();
		Mockito.doReturn(null).when(AA_Str1Spy).getValue();
		Mockito.doReturn(Arrays.asList("a.a.str1out", "a.a.str1inandout")).when(AA_Str1Spy).getOutAliases();
		//
		Mockito.doReturn("ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.STR1").when(AUA_Str1Spy).getCanonicalName();
		Mockito.doReturn("a.u.a.str1_value").when(AUA_Str1Spy).getValue();
		Mockito.doReturn(Arrays.asList("a.u.a.str1out", "a.u.a.str1inandout")).when(AUA_Str1Spy).getOutAliases();
		//
		Mockito.doReturn("ExportServiceSample.ExportMe.AllowMe1.STR1").when(EA_Str1Spy).getCanonicalName();
		Mockito.doReturn("e.a.str1_value").when(EA_Str1Spy).getValue();
		Mockito.doReturn(Arrays.asList("e.a.str1out", "e.a.str1inandout")).when(EA_Str1Spy).getOutAliases();


		pes.add(new PropertyExportImpl(
				A_Str1Spy, ExportServiceSample.AllowMe.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.NEVER));
		pes.add(new PropertyExportImpl(  //This one gets skipped entirely
				AA_Str1Spy, ExportServiceSample.AllowMe.AllowMe1.class,
				Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, Exporter.EXPORT_OUT_ALIASES.NEVER));
		pes.add(new PropertyExportImpl(
				AUA_Str1Spy, ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
				Exporter.EXPORT_CANONICAL_NAME.NEVER, Exporter.EXPORT_OUT_ALIASES.ALWAYS));
		pes.add(new PropertyExportImpl(
				EA_Str1Spy, ExportServiceSample.ExportMe.AllowMe1.class,
				Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, Exporter.EXPORT_OUT_ALIASES.ALWAYS));
	}

	@Test
	void test() {
		Map<String, String> export = pes.stream().collect(new StringMapExporter());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.STR1", "a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "e.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1inandout", "e.a.str1_value"));
		assertEquals(5, export.size());
	}

	@Test
	void testConvertingCase() {
		Map<String, String> export = pes.stream().map(p -> p.clone( p.getExportNames().stream().map(n -> n.toUpperCase()).collect(toList()) )).collect(new StringMapExporter());

		assertThat(export, hasEntry("EXPORTSERVICESAMPLE.ALLOWME.STR1", "a.str1_value"));
		assertThat(export, hasEntry("A.U.A.STR1OUT", "a.u.a.str1_value"));
		assertThat(export, hasEntry("A.U.A.STR1INANDOUT", "a.u.a.str1_value"));
		assertThat(export, hasEntry("E.A.STR1OUT", "e.a.str1_value"));
		assertThat(export, hasEntry("E.A.STR1INANDOUT", "e.a.str1_value"));
		assertEquals(5, export.size());
	}
	@Test
	void testFiltering() {
		Map<String, String> export = pes.stream().filter(p -> p.getContainingClass().getCanonicalName().contains("Unsure")).collect(new StringMapExporter());

		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertEquals(2, export.size());

	}

}