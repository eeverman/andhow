package org.yarnandtail.andhow.internal.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.GroupProxyImmutable;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.PropertyExport;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.property.StrProp;


import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ManualExportServiceTest {

	ManualExportService svs;

	//A complete set of GroupProxies w/ Properties where each
	//group has 2 properties.
	List<GroupProxy> allClassesGroupProxies;

	//
	// The entire AllowMe inner class and all its exportable children
	Class<?>[] entireAllowMeExportableClassList = {
		ExportServiceSample.AllowMe.class, ExportServiceSample.AllowMe.AllowMe1.class,
		ExportServiceSample.AllowMe.ExportMe1.class, ExportServiceSample.AllowMe.ImUnsure1.class,
		ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class, ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class,
		ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class
	};

	@BeforeEach
	void setUp() {
		svs = new ManualExportService();


		allClassesGroupProxies = new ArrayList<>();

		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.class));

		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.ImUnsure1.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.ImUnsure1.DisallowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.ImUnsure1.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.ImUnsure1.AllowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.ImUnsure1.DisallowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.ImUnsure1.ExportMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.DisallowMe.ImUnsure1.ImUnsure2.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.ImUnsure1.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.ImUnsure1.AllowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.ImUnsure1.DisallowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.ImUnsure1.ExportMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ExportMe.ImUnsure1.ImUnsure2.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ImUnsure.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ImUnsure.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ImUnsure.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ImUnsure.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample.ImUnsure.ImUnsure1.class));
	}

	GroupProxy makeGProxy(Class<?> clazz) {
		return makeGProxy(clazz, 2);
	}

	GroupProxy makeGProxy(Class<?> clazz, int propCount) {
		return new GroupProxyImmutable(clazz.getCanonicalName(), clazz.getName(),
			makeProps(clazz, propCount), true);
	}

	List<NameAndProperty> makeProps(Class<?> parent, int count) {
		List<NameAndProperty> naps = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			StrProp p = StrProp.builder().desc("Prop_" + (i + 1) + "_of_" + count + " " + parent.getCanonicalName()).build();
			String name = parent.getCanonicalName() + ".Prop_" + (i + 1) + "_of_" + count;
			NameAndProperty nap = new NameAndProperty(name, p);
			naps.add(nap);
		}

		return naps;
	}

	@Test
	void findEffectiveAllowAnnotationTest() {
		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.class).isPresent());

		// Top level class has no annotation
		ManualExportAllowed allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, allow.exportByCanonicalName());
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases());

		//
		// All nested classes in 'AllowMe'
		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.AllowMe1.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, allow.exportByCanonicalName());
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases());

		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.DisallowMe1.class).isPresent());

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.ExportMe1.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, allow.exportByCanonicalName(), "Inherit from parent");
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases(), "Inherit from parent");

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.ImUnsure1.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, allow.exportByCanonicalName(), "Inherit from parent");
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases(), "Inherit from parent");

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.NEVER, allow.exportByCanonicalName());
		assertEquals(EXPORT_OUT_ALIASES.ALWAYS, allow.exportByOutAliases());

		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.ImUnsure1.DisallowMe2.class).isPresent());

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, allow.exportByCanonicalName(), "Inherit from parent");
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases(), "Inherit from parent");

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, allow.exportByCanonicalName(), "Inherit from parent");
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases(), "Inherit from parent");

		//
		// All nested classes in 'DisallowMe'
		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.DisallowMe.class).isPresent());

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.DisallowMe.AllowMe1.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, allow.exportByCanonicalName());
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases());

		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.DisallowMe.DisallowMe1.class).isPresent());

		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.DisallowMe.ExportMe1.class).isPresent());

		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.DisallowMe.ImUnsure1.class).isPresent());

		allow = svs.findEffectiveAllowAnnotation(ExportServiceSample.DisallowMe.ImUnsure1.AllowMe2.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.NEVER, allow.exportByCanonicalName());
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases());

		//
		// All nested classes in 'ImUnsure'
		assertFalse(svs.findEffectiveAllowAnnotation(ExportServiceSample.ImUnsure.class).isPresent());

		//
		// Conflicting annotations
		assertThrows(IllegalStateException.class,
				() -> svs.findEffectiveAllowAnnotation(ExportServiceSample.ImConfused.class));
		assertThrows(IllegalStateException.class,
				() -> svs.findEffectiveAllowAnnotation(ExportServiceSample.ImConfused.ImAlsoConfused.class));
	}

	@Test
	void isManualExportDisallowedTest() {
		assertFalse(svs.isManualExportDisallowed(ExportServiceSample.class),
				"Not explicitly disallowed - no ann. present");
		assertFalse(svs.isManualExportDisallowed(ExportServiceSample.AllowMe.class));
		assertTrue(svs.isManualExportDisallowed(ExportServiceSample.AllowMe.DisallowMe1.class));
		assertFalse(svs.isManualExportDisallowed(ExportServiceSample.AllowMe.ImUnsure1.class));
	}

	@Test
	void getAllowAnnotationTest() {
		ManualExportAllowed allow = svs.getAllowAnnotation(ExportServiceSample.AllowMe.class).get();
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, allow.exportByCanonicalName());
		assertEquals(EXPORT_OUT_ALIASES.NEVER, allow.exportByOutAliases());
	}

	@Test
	void exportClassHappyPathTest() {

		Collection<PropertyExport> propExports = new ArrayList();

		svs.exportClass(ExportServiceSample.AllowMe.AllowMe1.class,
				EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.ALWAYS,
				allClassesGroupProxies, propExports);

		assertEquals(2, propExports.size());

		PropertyExport pe1 = propExports.stream()
				.filter(p -> p.getProperty().getDescription().startsWith("Prop_1_of_2"))
				.findFirst().get();
		PropertyExport pe2 = propExports.stream()
				.filter(p -> p.getProperty().getDescription().startsWith("Prop_2_of_2"))
				.findFirst().get();

		assertEquals(ExportServiceSample.AllowMe.AllowMe1.class, pe1.getContainingClass());
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, pe1.getCanonicalNameOption());
		assertEquals(EXPORT_OUT_ALIASES.ALWAYS, pe1.getOutAliasOption());
		//
		assertEquals(ExportServiceSample.AllowMe.AllowMe1.class, pe2.getContainingClass());
		assertEquals(EXPORT_CANONICAL_NAME.ALWAYS, pe2.getCanonicalNameOption());
		assertEquals(EXPORT_OUT_ALIASES.ALWAYS, pe2.getOutAliasOption());
	}

	@Test
	void exportClassWithNoMatchingGroupProxyTest() {

		Collection<PropertyExport> propExports = new ArrayList();

		// Remove the class we try to export from the GroupProxy list -
		// It now looks like a class which has no AndHow Properties
		allClassesGroupProxies = allClassesGroupProxies.stream()
				.filter(g -> ! g.getProxiedGroup().equals(ExportServiceSample.AllowMe.AllowMe1.class))
				.collect(Collectors.toList());

		svs.exportClass(ExportServiceSample.AllowMe.AllowMe1.class,
				EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.ALWAYS,
				allClassesGroupProxies, propExports);

		assertEquals(0, propExports.size());
	}

	@Test
	void exportClassAndChildrenWithASingleClass() {

		Set<Class<?>> alreadyExportedClasses = new HashSet();
		Collection<PropertyExport> propExports = new ArrayList();


		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS,
			alreadyExportedClasses, allClassesGroupProxies, propExports);

		assertEquals(2, propExports.size());

		PropertyExport pe1 = propExports.stream()
				.filter(p -> p.getProperty().getDescription().startsWith("Prop_1_of_2"))
				.findFirst().get();
		PropertyExport pe2 = propExports.stream()
				.filter(p -> p.getProperty().getDescription().startsWith("Prop_2_of_2"))
				.findFirst().get();

		assertEquals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class, pe1.getContainingClass());
		assertEquals(EXPORT_CANONICAL_NAME.NEVER, pe1.getCanonicalNameOption());
		assertEquals(EXPORT_OUT_ALIASES.ALWAYS, pe1.getOutAliasOption());
		//
		assertEquals(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class, pe2.getContainingClass());
		assertEquals(EXPORT_CANONICAL_NAME.NEVER, pe2.getCanonicalNameOption());
		assertEquals(EXPORT_OUT_ALIASES.ALWAYS, pe2.getOutAliasOption());

	}

	@Test
	void exportClassAndChildrenForAllowMe() {
		Set<Class<?>> alreadyExportedClasses = new HashSet();
		Collection<PropertyExport> propExports = new ArrayList();

		svs.exportClassAndChildren(ExportServiceSample.AllowMe.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);

		verifyAllowMeInnerClassExport(propExports);
	}

	@Test
	void exportClassAndChildrenForAllowMeAndOverlappingInnerClasses() {
		Set<Class<?>> alreadyExportedClasses = new HashSet();
		Collection<PropertyExport> propExports = new ArrayList();

		svs.exportClassAndChildren(ExportServiceSample.AllowMe.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);

		//
		// Also export duplicate inner-classes contained in AllowMe - should be ignored
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);

		verifyAllowMeInnerClassExport(propExports);

	}


	@Test
	void exportClassAndChildrenOfEntireAllowMeInnerclassByAddingEachInnerClassSeparately() {
		Set<Class<?>> alreadyExportedClasses = new HashSet();
		Collection<PropertyExport> propExports = new ArrayList();

		//
		// Export some inner-classes contained in AllowMe
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, allClassesGroupProxies, propExports);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS,
			alreadyExportedClasses, allClassesGroupProxies, propExports);

		// The top level 'AllowMe' class is not included, so there are 2 less properties
		assertEquals(12, propExports.size());

		verify(propExports, ExportServiceSample.AllowMe.AllowMe1.class, 2,
			EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER);
		verify(propExports, ExportServiceSample.AllowMe.ExportMe1.class, 2,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.class, 2,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class, 2,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS);
		verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class, 2,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class, 2,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);

	}

	@Test
	void handleManualExportForAllowMe() throws IllegalAccessException {
		Collection<PropertyExport> propExports = new ArrayList();

		List<Class<?>> exportRoots = new ArrayList();
		exportRoots.add(ExportServiceSample.AllowMe.class);

		propExports = svs.doManualExport(exportRoots, allClassesGroupProxies);

		verifyAllowMeInnerClassExport(propExports);
	}

	@Test
	void handleManualExportForAllowMeAndOverlappingInnerClasses() throws IllegalAccessException {
		Collection<PropertyExport> propExports = new ArrayList();

		List<Class<?>> exportRoots = new ArrayList();
		exportRoots.add(ExportServiceSample.AllowMe.class);
		exportRoots.add(ExportServiceSample.AllowMe.AllowMe1.class);
		exportRoots.add(ExportServiceSample.AllowMe.ExportMe1.class);
		exportRoots.add(ExportServiceSample.AllowMe.ImUnsure1.class);
		exportRoots.add(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class);

		propExports = svs.doManualExport(exportRoots, allClassesGroupProxies);

		verifyAllowMeInnerClassExport(propExports);
	}

	@Test
	void handleManualExportShouldErrorForNonExportableClasses() {
		final ArrayList<Class<?>> exportRoots = new ArrayList();
		exportRoots.add(ExportServiceSample.class);

		assertThrows(IllegalAccessException.class,
				() -> svs.doManualExport(exportRoots, allClassesGroupProxies));

		exportRoots.clear();
		exportRoots.add(ExportServiceSample.DisallowMe.class);

		assertThrows(IllegalAccessException.class,
				() -> svs.doManualExport(exportRoots, allClassesGroupProxies));

		exportRoots.clear();
		exportRoots.add(ExportServiceSample.DisallowMe.ImUnsure1.class);

		assertThrows(IllegalAccessException.class,
				() -> svs.doManualExport(exportRoots, allClassesGroupProxies));

		exportRoots.clear();
		exportRoots.add(ExportServiceSample.ImUnsure.class);

		assertThrows(IllegalAccessException.class,
				() -> svs.doManualExport(exportRoots, allClassesGroupProxies));
	}


 void verifyAllowMeInnerClassExport(final Collection<PropertyExport> propExports) {
	 assertEquals(14, propExports.size());

	 verify(propExports, ExportServiceSample.AllowMe.class, 2,
			 EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
	 verify(propExports, ExportServiceSample.AllowMe.AllowMe1.class, 2,
			 EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER);
	 verify(propExports, ExportServiceSample.AllowMe.ExportMe1.class, 2,
			 EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
	 verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.class, 2,
			 EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
	 verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class, 2,
			 EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS);
	 verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class, 2,
			 EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
	 verify(propExports, ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class, 2,
			 EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
 }

	void verify(final Collection<PropertyExport> propExports, final Class<?> expectedClass, final int expectedPropCount,
							final EXPORT_CANONICAL_NAME expectCanOpt, final EXPORT_OUT_ALIASES expectAliasOpt) {

		Collection<PropertyExport> pes = propExports.stream()
				.filter(
						p -> p.getContainingClass().equals(expectedClass) &&
										 p.getCanonicalNameOption().equals(expectCanOpt) &&
										 p.getOutAliasOption().equals(expectAliasOpt)
				)
				.collect(Collectors.toList());

		assertEquals(expectedPropCount, pes.size());

		for (int i = 1; i < expectedPropCount; i++) {
			final int ordinal = i;
			assertEquals(1, pes.stream().filter(p -> p.getProperty().getDescription().equals(
					"Prop_" + ordinal + "_of_" + expectedPropCount + " " + p.getContainingClass().getCanonicalName()
			)).count());
		}

	}


}