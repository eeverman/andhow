package org.yarnandtail.andhow.internal.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ManualExportServiceTest {

	ManualExportService svs;

	@BeforeEach
	void setUp() {
		svs = new ManualExportService();
	}

	@Test
	void handleManualExport() {
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

		Class<?>[] expectedClasses = {
			ExportServiceSample.AllowMe.class, ExportServiceSample.AllowMe.AllowMe1.class,
			ExportServiceSample.AllowMe.ExportMe1.class, ExportServiceSample.AllowMe.ImUnsure1.class,
			ExportServiceSample.AllowMe.ImUnsure1.AllowMe2.class, ExportServiceSample.AllowMe.ImUnsure1.ExportMe2.class,
			ExportServiceSample.AllowMe.ImUnsure1.ImUnsure2.class
		};


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

		assertThat(alreadyExportedClasses, containsInAnyOrder(expectedClasses));
	}
}