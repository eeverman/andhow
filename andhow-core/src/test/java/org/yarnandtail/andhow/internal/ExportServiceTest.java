package org.yarnandtail.andhow.internal;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest {

	@Test
	void isExportAllowed() {
		ExportService es = new ExportService();

		//
		// Root (has no annotation)
		assertFalse(es.isExportAllowed(ExportServiceSample1.class).isPresent(),
				"The top class has no annotation, so 'unknown' is right. " +
						"This allows exports of inner classes that are marked as allowed.");

		//
		// First level down
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.DisallowMe.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.ExportMe.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.ImUnsure.class).isPresent(),
				"similar to root - no explicit allow or disallow");

		//
		// Inside 'AllowMe'
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.AllowMe1.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.AllowMe.DisallowMe1.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.ExportMe1.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.ImUnsure1.class).get(),
				"The containing class is 'allow', so applies to this contained, unmarked class");

		//
		// Inside 'AllowMe.ImUnsure'
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.ImUnsure1.AllowMe2.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.AllowMe.ImUnsure1.DisallowMe2.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.ImUnsure1.ExportMe2.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.AllowMe.ImUnsure1.ImUnsure2.class).get(),
				"The containing class is 'allow' (2 lvls up), so applies to this contained, unmarked class");

		//
		// Inside 'DisallowMe'
		assertTrue(es.isExportAllowed(ExportServiceSample1.DisallowMe.AllowMe1.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.DisallowMe.DisallowMe1.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.DisallowMe.ExportMe1.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.DisallowMe.ImUnsure1.class).get(),
				"The containing class is 'disallow', so applies to this contained, unmarked class");

		//
		// Inside 'DisallowMe.ImUnsure'
		assertTrue(es.isExportAllowed(ExportServiceSample1.DisallowMe.ImUnsure1.AllowMe2.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.DisallowMe.ImUnsure1.DisallowMe2.class).get());
		assertTrue(es.isExportAllowed(ExportServiceSample1.DisallowMe.ImUnsure1.ExportMe2.class).get());
		assertFalse(es.isExportAllowed(ExportServiceSample1.DisallowMe.ImUnsure1.ImUnsure2.class).get(),
				"The containing class is 'disallow' (2 lvls up), so applies to this contained, unmarked class");

	}

	@Test
	void stemInnerExportClassesTest() {
		ExportService es = new ExportService();

		Class<?>[] allowMeAllowedClasses = {
				//ExportServiceSample1.AllowMe.class, Not included b/c caller adds
				ExportServiceSample1.AllowMe.AllowMe1.class,
				ExportServiceSample1.AllowMe.ExportMe1.class,
				ExportServiceSample1.AllowMe.ImUnsure1.class,
				ExportServiceSample1.AllowMe.ImUnsure1.AllowMe2.class,
				ExportServiceSample1.AllowMe.ImUnsure1.ExportMe2.class,
				ExportServiceSample1.AllowMe.ImUnsure1.ImUnsure2.class
		};

		Class<?>[] disallowMeAllowedClasses = {};

		Class<?>[] exportMeAllowedClasses = {
				//ExportServiceSample1.AllowMe.class, Not included b/c caller adds
				ExportServiceSample1.ExportMe.AllowMe1.class,
				ExportServiceSample1.ExportMe.ExportMe1.class,
				ExportServiceSample1.ExportMe.ImUnsure1.class,
				ExportServiceSample1.ExportMe.ImUnsure1.AllowMe2.class,
				ExportServiceSample1.ExportMe.ImUnsure1.ExportMe2.class,
				ExportServiceSample1.ExportMe.ImUnsure1.ImUnsure2.class
		};

		Class<?>[] imUnsureMeAllowedClasses = {
				//ExportServiceSample1.AllowMe.class, Not included b/c caller adds
				ExportServiceSample1.ImUnsure.AllowMe1.class,
				ExportServiceSample1.ImUnsure.ExportMe1.class,
		};

		// At the top level, all the other lists plus the containers:
		// ExportServiceSample1.AllowMe & ExportServiceSample1.ExportMe.
		// The other two containers are not added b/c they are either disallowed or not explicitly
		// allowed.
		Class<?>[][] joinedAllowedClasses = {
				allowMeAllowedClasses, exportMeAllowedClasses, imUnsureMeAllowedClasses,
				{ExportServiceSample1.AllowMe.class, ExportServiceSample1.ExportMe.class}};
		//
		// The whole sample class
		assertThat(
				es.stemInnerExportClasses(ExportServiceSample1.class),
				Matchers.containsInAnyOrder(
						Stream.of(joinedAllowedClasses).flatMap(Stream::of).toArray()));

		//
		// The first level inner classes
		assertThat(
				es.stemInnerExportClasses(ExportServiceSample1.AllowMe.class),
				Matchers.containsInAnyOrder(allowMeAllowedClasses));
		assertThat(
				"Degenerate case:  This method should never be called with a class directly or " +
						"indirectly marked to disallow export.  This method explicitly does not check for " +
						"allowability of the class it is called on, only going towards the leaf of the tree.",
				es.stemInnerExportClasses(ExportServiceSample1.DisallowMe.class),
				Matchers.containsInAnyOrder(
						ExportServiceSample1.DisallowMe.AllowMe1.class,
						ExportServiceSample1.DisallowMe.ExportMe1.class));
		assertThat(
				es.stemInnerExportClasses(ExportServiceSample1.ExportMe.class),
				Matchers.containsInAnyOrder(exportMeAllowedClasses));
		assertThat(
				es.stemInnerExportClasses(ExportServiceSample1.ImUnsure.class),
				Matchers.containsInAnyOrder(imUnsureMeAllowedClasses));

	}
}