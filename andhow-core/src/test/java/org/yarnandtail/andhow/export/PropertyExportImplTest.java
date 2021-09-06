package org.yarnandtail.andhow.export;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.property.IntProp;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PropertyExportImplTest {

	IntProp IP;
	List<String> outAliases;

	@BeforeEach
	void setUp() {
		IP = IntProp.builder().build();
		IP = Mockito.spy(IP);

		outAliases = new ArrayList<>();
		outAliases.add("a1");
		outAliases.add("a2");

		Mockito.doReturn(99).when(IP).getValue();
		Mockito.doReturn("99").when(IP).getValueAsString();
		Mockito.doReturn(outAliases).when(IP).getOutAliases();
		Mockito.doReturn("canonical.name.IP").when(IP).getCanonicalName();
	}

	@Test
	void outerClassTest() {
		PropertyExportImpl pe = new PropertyExportImpl(IP, PropertyExportImplTest.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.ALWAYS);

		assertEquals(IP, pe.getProperty());
		assertEquals(PropertyExportImplTest.class, pe.getContainingClass());
		assertEquals(Exporter.EXPORT_CANONICAL_NAME.ALWAYS, pe.getCanonicalNameOption());
		assertEquals(Exporter.EXPORT_OUT_ALIASES.ALWAYS, pe.getOutAliasOption());
		assertEquals(99, pe.getValue());
		assertEquals("99", pe.getValueAsString());
		assertThat(pe.getExportNames(), Matchers.containsInAnyOrder("a1", "a2", "canonical.name.IP"));
	}

	@Test
	void innerClassViaMapNamesTest() {
		PropertyExportImpl pe = new PropertyExportImpl(IP, PropertyExportImplTest.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.ALWAYS);

		List<String> newNames = new ArrayList();
		newNames.add("newName1");
		newNames.add("newName2");

		PropertyExport ipe = pe.mapNames(newNames);

		assertEquals(IP, ipe.getProperty());
		assertEquals(PropertyExportImplTest.class, ipe.getContainingClass());
		assertEquals(Exporter.EXPORT_CANONICAL_NAME.ALWAYS, ipe.getCanonicalNameOption());
		assertEquals(Exporter.EXPORT_OUT_ALIASES.ALWAYS, ipe.getOutAliasOption());
		assertEquals(99, ipe.getValue());
		assertEquals("99", ipe.getValueAsString());
		assertThat(ipe.getExportNames(), Matchers.containsInAnyOrder("newName1", "newName2"));
	}

	@Test
	void innerClassViaMapValueTest() {
		PropertyExportImpl pe = new PropertyExportImpl(IP, PropertyExportImplTest.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.ALWAYS);

		PropertyExport ipe = pe.mapValue(Integer.valueOf(11));

		assertEquals(IP, ipe.getProperty());
		assertEquals(PropertyExportImplTest.class, ipe.getContainingClass());
		assertEquals(Exporter.EXPORT_CANONICAL_NAME.ALWAYS, ipe.getCanonicalNameOption());
		assertEquals(Exporter.EXPORT_OUT_ALIASES.ALWAYS, ipe.getOutAliasOption());
		assertEquals(11, ipe.getValue());
		assertEquals("99", ipe.getValueAsString());	//This not modified
		assertThat(ipe.getExportNames(), Matchers.containsInAnyOrder("a1", "a2", "canonical.name.IP"));
	}

	@Test
	void innerClassViaMapValueAsStringTest() {
		PropertyExportImpl pe = new PropertyExportImpl(IP, PropertyExportImplTest.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.ALWAYS);

		PropertyExport ipe = pe.mapValueAsString("11");

		assertEquals(IP, ipe.getProperty());
		assertEquals(PropertyExportImplTest.class, ipe.getContainingClass());
		assertEquals(Exporter.EXPORT_CANONICAL_NAME.ALWAYS, ipe.getCanonicalNameOption());
		assertEquals(Exporter.EXPORT_OUT_ALIASES.ALWAYS, ipe.getOutAliasOption());
		assertEquals(99, ipe.getValue());	//This not modified
		assertEquals("11", ipe.getValueAsString());
		assertThat(ipe.getExportNames(), Matchers.containsInAnyOrder("a1", "a2", "canonical.name.IP"));
	}

	@Test
	void innerClassViaTwoLevelsTest() {
		PropertyExportImpl pe = new PropertyExportImpl(IP, PropertyExportImplTest.class,
				Exporter.EXPORT_CANONICAL_NAME.ALWAYS, Exporter.EXPORT_OUT_ALIASES.ALWAYS);

		List<String> newNames = new ArrayList();
		newNames.add("newName1");
		newNames.add("newName2");

		PropertyExport ipe = pe.mapNames(newNames);

		ipe = ipe.mapValueAsString("11");

		assertEquals(IP, ipe.getProperty());
		assertEquals(PropertyExportImplTest.class, ipe.getContainingClass());
		assertEquals(Exporter.EXPORT_CANONICAL_NAME.ALWAYS, ipe.getCanonicalNameOption());
		assertEquals(Exporter.EXPORT_OUT_ALIASES.ALWAYS, ipe.getOutAliasOption());
		assertEquals(99, ipe.getValue());	//This not modified
		assertEquals("11", ipe.getValueAsString());
		assertThat(ipe.getExportNames(), Matchers.containsInAnyOrder("newName1", "newName2"));
	}

}