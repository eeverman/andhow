package org.yarnandtail.andhow.internal.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.GroupProxyImmutable;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

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
	void exportClassAndChildrenWithASingleClass() {
		ManualExportService.ExportCallback callback = Mockito.mock(ManualExportService.ExportCallback.class);
		Set<Class<?>> alreadyExportedClasses = new HashSet();


		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS,
			alreadyExportedClasses, callback);

		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS);
		verifyNoMoreInteractions(callback);
		assertThat(alreadyExportedClasses, containsInAnyOrder(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class));
	}

	@Test
	void exportClassAndChildrenWithTheEntireAllowMeInnerclass() {
		ManualExportService.ExportCallback callback = Mockito.mock(ManualExportService.ExportCallback.class);
		Set<Class<?>> alreadyExportedClasses = new HashSet();

		svs.exportClassAndChildren(ExportServiceSample.AllowMe.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);

		verify(callback).handleGroup(ExportServiceSample.AllowMe.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verifyNoMoreInteractions(callback);

		assertThat(alreadyExportedClasses, containsInAnyOrder(entireAllowMeExportableClassList));
	}

	@Test
	void exportClassAndChildrenWithTheEntireAllowMeInnerclassAndOverlappingInnerClass() {
		ManualExportService.ExportCallback callback = Mockito.mock(ManualExportService.ExportCallback.class);
		Set<Class<?>> alreadyExportedClasses = new HashSet();

		svs.exportClassAndChildren(ExportServiceSample.AllowMe.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);

		//
		// Also export some inner-classes contained in AllowMe
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);

		//The actual callback calls and exported classes should be the same
		verify(callback).handleGroup(ExportServiceSample.AllowMe.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verifyNoMoreInteractions(callback);

		assertThat(alreadyExportedClasses, containsInAnyOrder(entireAllowMeExportableClassList));
	}


	@Test
	void exportClassAndChildrenOfEntireAllowMeInnerclassByAddingEachInnerClassSeparately() {
		ManualExportService.ExportCallback callback = Mockito.mock(ManualExportService.ExportCallback.class);
		Set<Class<?>> alreadyExportedClasses = new HashSet();


		//
		// Export some inner-classes contained in AllowMe
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER,
			alreadyExportedClasses, callback);
		svs.exportClassAndChildren(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS,
			alreadyExportedClasses, callback);

		//The actual callback calls and exported classes should be the same except AllowMe not included
		verify(callback).handleGroup(ExportServiceSample.AllowMe.AllowMe1.class,
			EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ExportMe1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class,
			EXPORT_CANONICAL_NAME.NEVER, EXPORT_OUT_ALIASES.ALWAYS);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verify(callback).handleGroup(ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class,
			EXPORT_CANONICAL_NAME.ALWAYS, EXPORT_OUT_ALIASES.NEVER);
		verifyNoMoreInteractions(callback);

		//Remove AllowMe from the class list
		List<Class<?>> classList = new ArrayList(Arrays.asList(entireAllowMeExportableClassList));
		classList.remove(ExportServiceSample.AllowMe.class);

		assertThat(alreadyExportedClasses, containsInAnyOrder(classList.toArray(new Class<?>[0])));
	}

	@Test
	void handleManualExportForAllowMe() throws IllegalAccessException {

		ManualExportService.ExportPropertyHandler propHandler =
			mock(ManualExportService.ExportPropertyHandler.class);
		List<Class<?>> exportRoots = new ArrayList();
		exportRoots.add(ExportServiceSample.AllowMe.class);

		svs.handleManualExport(exportRoots, propHandler, allClassesGroupProxies);

		// Verify calls to the property handler

		//Full detail for the 1st two...
		ArgumentCaptor<Property<?>> propCapture = ArgumentCaptor.forClass(Property.class);
		verify(propHandler, times(2)).handleProperty(propCapture.capture(),
			eq(ExportServiceSample.AllowMe.class),
			eq(EXPORT_CANONICAL_NAME.ALWAYS), eq(EXPORT_OUT_ALIASES.NEVER));
		List<String> propDescs = propCapture.getAllValues().stream().map(p -> p.getDescription())
			.collect(Collectors.toList());
		assertThat(propDescs, containsInAnyOrder(
			"Prop_1_of_2 org.yarnandtail.andhow.internal.export.ExportServiceSample.AllowMe",
						"Prop_2_of_2 org.yarnandtail.andhow.internal.export.ExportServiceSample.AllowMe"
			));

		propCapture = ArgumentCaptor.forClass(Property.class);
		verify(propHandler, times(2)).handleProperty(propCapture.capture(),
			eq(ExportServiceSample.AllowMe.AllowMe1.class),
			eq(EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS), eq(EXPORT_OUT_ALIASES.NEVER));
		propDescs = propCapture.getAllValues().stream().map(p -> p.getDescription())
			.collect(Collectors.toList());
		assertThat(propDescs, containsInAnyOrder(
			"Prop_1_of_2 org.yarnandtail.andhow.internal.export.ExportServiceSample.AllowMe.AllowMe1",
			"Prop_2_of_2 org.yarnandtail.andhow.internal.export.ExportServiceSample.AllowMe.AllowMe1"
		));

		verify(propHandler, times(2)).handleProperty(any(),
			eq(ExportServiceSample.AllowMe.ExportMe1.class),
			eq(EXPORT_CANONICAL_NAME.ALWAYS), eq(EXPORT_OUT_ALIASES.NEVER));
		verify(propHandler, times(2)).handleProperty(any(),
			eq(ExportServiceSample.AllowMe.ImUnsure1.class),
			eq(EXPORT_CANONICAL_NAME.ALWAYS), eq(EXPORT_OUT_ALIASES.NEVER));
		verify(propHandler, times(2)).handleProperty(any(),
			eq(ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class),
			eq(EXPORT_CANONICAL_NAME.NEVER), eq(EXPORT_OUT_ALIASES.ALWAYS));
		verify(propHandler, times(2)).handleProperty(any(),
			eq(ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class),
			eq(EXPORT_CANONICAL_NAME.ALWAYS), eq(EXPORT_OUT_ALIASES.NEVER));
		verify(propHandler, times(2)).handleProperty(any(),
			eq(ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class),
			eq(EXPORT_CANONICAL_NAME.ALWAYS), eq(EXPORT_OUT_ALIASES.NEVER));
		verifyNoMoreInteractions(propHandler);
	}

	void testinvoke() {
//
//
//		Map<String, String> props = AndHow.exportBuilder()
//			.add(ExportServiceSample.AllowMe.class)
//			.add(ExportServiceSample.ImUnsure.AllowMe1.class)
//			.toStringStringMap();
//
//		Map<String, Object> customProps = new HashMap<>();
//		AndHow.exportBuilder()
//			.add(ExportServiceSample.AllowMe.class)
//			.toCustom(prefName, value -> customProps.put(prefName, value));
//
//		Map<String, String> props = AndHow.export(ExportServiceSample.AllowMe.class)
//																	.stream().collect(MapStringStringCollector);
	}
}