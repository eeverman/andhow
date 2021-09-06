package org.yarnandtail.andhow.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.internal.export.ExportServiceSample;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;

import static java.util.stream.Collectors.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExportCollectorTest {

	List<PropertyExport> pes;

	@BeforeEach
	void setUp() {
		pes = new ArrayList<>();

		// Mockito spies to override methods of live Property instances
		// These Properties are the ones from ExportServiceSample, but none of the export ann.
		// from that file are in use due to how this is mocked up (not a complete AndHow env.)
		IntProp A_Int1Spy = Mockito.spy(ExportServiceSample.AllowMe.INT1);
		StrProp AA_Str1Spy = Mockito.spy(ExportServiceSample.AllowMe.AllowMe1.STR1);
		StrProp AUA_Str1Spy = Mockito.spy(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.STR1);
		StrProp EA_Str1Spy = Mockito.spy(ExportServiceSample.ExportMe.AllowMe1.STR1);


		//
		Mockito.doReturn("ExportServiceSample.AllowMe.INT1").when(A_Int1Spy).getCanonicalName();
		Mockito.doReturn(1).when(A_Int1Spy).getValue();
		Mockito.doReturn(Arrays.asList("int1out", "int1inandout")).when(A_Int1Spy).getOutAliases();
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
		Mockito.doReturn(null).when(EA_Str1Spy).getValue();
		Mockito.doReturn(Arrays.asList("e.a.str1out", "e.a.str1inandout")).when(EA_Str1Spy).getOutAliases();

		pes.add(new PropertyExportImpl(
				A_Int1Spy, ExportServiceSample.AllowMe.class,
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

	//
	// StringMap

	@Test
	void exportStreamToStringMap() {
		Map<String, String> export = pes.stream().collect(ExportCollector.stringMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToStringMapAndConvertNameCase() {
		Map<String, String> export = pes.stream()
				.map(p -> p.mapNames( p.getExportNames().stream().map(n -> n.toUpperCase()).collect(toList()) ))
				.collect(ExportCollector.stringMap());

		assertThat(export, hasEntry("EXPORTSERVICESAMPLE.ALLOWME.INT1", "1"));
		assertThat(export, hasEntry("A.U.A.STR1OUT", "a.u.a.str1_value"));
		assertThat(export, hasEntry("A.U.A.STR1INANDOUT", "a.u.a.str1_value"));
		assertThat(export, hasEntry("E.A.STR1OUT", null));
		assertThat(export, hasEntry("E.A.STR1INANDOUT", null));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToStringMapAndSetNamesToEmptyListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ an empty list - should just remove them from export
		Map<String, String> export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	? Collections.emptyList():p.getExportNames() ))
				.collect(ExportCollector.stringMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(3, export.size());
	}


	@Test
	void exportStreamToStringMapAndSetNamesToNullListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ null - should just remove them from export
		Map<String, String> export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	?null:p.getExportNames() ))
				.collect(ExportCollector.stringMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(3, export.size());
	}

	@Test
	void exportStreamToStringMapAndConvertValueAsString() {
		Map<String, String> export = pes.stream()
				.map(p -> p.mapValueAsString( p.getValueAsString() != null
																					? p.getValueAsString().toUpperCase():null) )
				.collect(ExportCollector.stringMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
		assertThat(export, hasEntry("a.u.a.str1out", "A.U.A.STR1_VALUE"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "A.U.A.STR1_VALUE"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToStringMapWithFiltering() {
		Map<String, String> export = pes.stream()
				.filter(p -> p.getContainingClass().getCanonicalName().contains("Unsure"))
				.collect(ExportCollector.stringMap());

		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertEquals(2, export.size());
	}

	@Test // The finisher is not invoked otherwise, so a separate test
	void stringMapFinisherReturnsIsIdentity() {
		Map<String, String> map = new HashMap<>();
		assertSame(map, ExportCollector.stringMap().finisher().apply(map));
	}

	//
	// StringProperties
	@Test
	void exportStreamToStringProperties() {
		Properties export = pes.stream().collect(ExportCollector.stringProperties("[null]"));

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "[null]"));
		assertThat(export, hasEntry("e.a.str1inandout", "[null]"));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToStringPropertiesAndSetNamesToEmptyListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ an empty list - should just remove them from export
		Properties export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	?Collections.emptyList():p.getExportNames() ))
				.collect(ExportCollector.stringProperties("[null]"));

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "[null]"));
		assertThat(export, hasEntry("e.a.str1inandout", "[null]"));
		assertEquals(3, export.size());
	}


	@Test
	void exportStreamToStringPropertiesAndSetNamesToNullListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ null - should just remove them from export
		Properties export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	? null:p.getExportNames() ))
				.collect(ExportCollector.stringProperties("[null]"));

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", "1"));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "[null]"));
		assertThat(export, hasEntry("e.a.str1inandout", "[null]"));
		assertEquals(3, export.size());
	}

	@Test
	void exportStreamToStringPropertiesWithNullNullValueThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> pes.stream().collect(ExportCollector.stringProperties(null))
		);
	}

	@Test // The finisher is not invoked otherwise, so a separate test
	void stringPropertiesFinisherIsIdentity() {
		Properties props = new Properties();
		assertSame(props, ExportCollector.stringProperties("").finisher().apply(props));
	}


	//
	// ObjectMap
	@Test
	void exportStreamToObjectMap() {
		Map<String, Object> export = pes.stream().collect(ExportCollector.objectMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 1));	//1 is an Integer, not Str.
		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToObjectMapAndConvertValue() {
		Map<String, Object> export = pes.stream()
				.map(p -> p.mapValue( (p.getValue() != null && p.getValue() instanceof Integer)
																	? (Integer)p.getValue() * 2:p.getValue()) )
				.collect(ExportCollector.objectMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 2));	// Times 2X
		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToObjectMapAndConvertNameAndValue() {
		Map<String, Object> export = pes.stream()
				.map(p -> p.mapNames( p.getExportNames().stream().map(n -> n.toUpperCase()).collect(toList()) ))
				.map(p -> p.mapValue( (p.getValue() != null && p.getValue() instanceof Integer)
																	? (Integer)p.getValue() * 2:p.getValue()) )
				.collect(ExportCollector.objectMap());

		assertThat(export, hasEntry("EXPORTSERVICESAMPLE.ALLOWME.INT1", 2)); // Times 2X
		assertThat(export, hasEntry("A.U.A.STR1OUT", "a.u.a.str1_value"));
		assertThat(export, hasEntry("A.U.A.STR1INANDOUT", "a.u.a.str1_value"));
		assertThat(export, hasEntry("E.A.STR1OUT", null));
		assertThat(export, hasEntry("E.A.STR1INANDOUT", null));

	}

	@Test
	void exportStreamToObjectMapAndConvertNameAndValueTwice() {
		Map<String, Object> export = pes.stream()
				.map(p -> p.mapNames( p.getExportNames().stream().map(n -> n.toUpperCase()).collect(toList()) ))
				.map(p -> p.mapValue( (p.getValue() != null && p.getValue() instanceof Integer) ? (Integer)p.getValue() * 2:p.getValue()) )
				.map(p -> p.mapValue( (p.getValue() != null && p.getValue() instanceof String) ? String.valueOf(p.getValue()).toUpperCase():p.getValue()) )
				.collect(ExportCollector.objectMap());

		assertThat(export, hasEntry("EXPORTSERVICESAMPLE.ALLOWME.INT1", 2)); // Times 2X
		assertThat(export, hasEntry("A.U.A.STR1OUT", "A.U.A.STR1_VALUE"));
		assertThat(export, hasEntry("A.U.A.STR1INANDOUT", "A.U.A.STR1_VALUE"));
		assertThat(export, hasEntry("E.A.STR1OUT", null));
		assertThat(export, hasEntry("E.A.STR1INANDOUT", null));
		assertEquals(5, export.size());
	}

	@Test
	void exportStreamToObjectMapAndSetNamesToEmptyListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ an empty list - should just remove them from export
		Map<String, Object> export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	? Collections.emptyList():p.getExportNames() ))
				.collect(ExportCollector.objectMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 1));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(3, export.size());
	}


	@Test
	void exportStreamToObjectMapAndSetNamesToNullListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ null - should just remove them from export
		Map<String, Object> export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	? null:p.getExportNames() ))
				.collect(ExportCollector.objectMap());

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 1));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", null));
		assertThat(export, hasEntry("e.a.str1inandout", null));
		assertEquals(3, export.size());
	}


	@Test // The finisher is not invoked otherwise, so a separate test
	void objectMapFinisherIsIdentity() {
		Map<String, Object> map = new HashMap<>();
		assertSame(map, ExportCollector.objectMap().finisher().apply(map));
	}

	//
	// ObjectProperties
	@Test
	void exportStreamToObjectProperties() {
		Properties export = pes.stream().collect(ExportCollector.objectProperties("[null]"));

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 1));	//1 is an Integer, not Str.
		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "[null]"));
		assertThat(export, hasEntry("e.a.str1inandout", "[null]"));
		assertEquals(5, export.size());
	}


	@Test
	void exportStreamToObjectPropertiesAndSetNamesToEmptyListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ an empty list - should just remove them from export
		Properties export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)
																	? Collections.emptyList():p.getExportNames() ))
				.collect(ExportCollector.objectProperties("[null]"));

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 1));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "[null]"));
		assertThat(export, hasEntry("e.a.str1inandout", "[null]"));
		assertEquals(3, export.size());
	}

	@Test
	void exportStreamToObjectPropertiesAndSetNamesToNullListShouldRemoveExport() {

		//Replace the 'a.u.a' Property names w/ null - should just remove them from export
		Properties export = pes.stream()
				.map(p -> p.mapNames( p.getContainingClass().equals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class)?null:p.getExportNames() ))
				.collect(ExportCollector.objectProperties("[null]"));

		assertThat(export, hasEntry("ExportServiceSample.AllowMe.INT1", 1));
//		assertThat(export, hasEntry("a.u.a.str1out", "a.u.a.str1_value"));
//		assertThat(export, hasEntry("a.u.a.str1inandout", "a.u.a.str1_value"));
		assertThat(export, hasEntry("e.a.str1out", "[null]"));
		assertThat(export, hasEntry("e.a.str1inandout", "[null]"));
		assertEquals(3, export.size());
	}

	@Test
	void exportStreamToObjectPropertiesWithNullNullValueThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> pes.stream().collect(ExportCollector.objectProperties(null))
		);
	}

}