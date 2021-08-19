package org.yarnandtail.andhow.internal;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.GroupProxyImmutable;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.property.StrProp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest {

	ExportService es;

	//A complete set of GroupProxies w/ Properties where each
	//group has 2 proxies.
	List<GroupProxy> allClassesGroupProxies;


	//
	// The classes that are allowed to be exported, if requested from the top level class,
	// broken up by the 1st level inner classes.

	Class<?>[] allowMeAllowedClasses = {
			ExportServiceSample1.AllowMe.class,
			ExportServiceSample1.AllowMe.AllowMe1.class,
			ExportServiceSample1.AllowMe.ExportMe1.class,
			ExportServiceSample1.AllowMe.ImUnsure1.class,
			ExportServiceSample1.AllowMe.ImUnsure1.AllowMe2.class,
			ExportServiceSample1.AllowMe.ImUnsure1.ExportMe2.class,
			ExportServiceSample1.AllowMe.ImUnsure1.ImUnsure2.class
	};


	Class<?>[] exportMeAllowedClasses = {
			ExportServiceSample1.ExportMe.class,
			ExportServiceSample1.ExportMe.AllowMe1.class,
			ExportServiceSample1.ExportMe.ExportMe1.class,
			ExportServiceSample1.ExportMe.ImUnsure1.class,
			ExportServiceSample1.ExportMe.ImUnsure1.AllowMe2.class,
			ExportServiceSample1.ExportMe.ImUnsure1.ExportMe2.class,
			ExportServiceSample1.ExportMe.ImUnsure1.ImUnsure2.class
	};

	Class<?>[] imUnsureAllowedClasses = {
			ExportServiceSample1.ImUnsure.AllowMe1.class,
			ExportServiceSample1.ImUnsure.ExportMe1.class
	};

	// The joined list of all the allowed 1st level inner classes.  Exporting the
	// root class should generate this list of export classes.
	Class<?>[] rootAllowedClasses = (Class<?>[]) Stream.of(new Class<?>[][]{
			allowMeAllowedClasses, exportMeAllowedClasses, imUnsureAllowedClasses}
	).flatMap(Stream::of).toArray(Class<?>[]::new);


	@BeforeEach
	void setup() {

		es = new ExportService();


		allClassesGroupProxies = new ArrayList<>();

		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.class));

		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.ImUnsure1.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.ImUnsure1.AllowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.ImUnsure1.DisallowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.ImUnsure1.ExportMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.AllowMe.ImUnsure1.ImUnsure2.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.ImUnsure1.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.ImUnsure1.AllowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.ImUnsure1.DisallowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.ImUnsure1.ExportMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.DisallowMe.ImUnsure1.ImUnsure2.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.ImUnsure1.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.ImUnsure1.AllowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.ImUnsure1.DisallowMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.ImUnsure1.ExportMe2.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ExportMe.ImUnsure1.ImUnsure2.class));
		//
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ImUnsure.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ImUnsure.AllowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ImUnsure.DisallowMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ImUnsure.ExportMe1.class));
		allClassesGroupProxies.add(makeGProxy(ExportServiceSample1.ImUnsure.ImUnsure1.class));







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
			StrProp p = StrProp.builder().desc("Prop_" + (i + 1) + "_of_ " + count + " " + parent.getCanonicalName()).build();
			String name = parent.getCanonicalName() + ".Prop_" + (i + 1) + "_of_" + count;
			NameAndProperty nap = new NameAndProperty(name, p);
			naps.add(nap);
		}

		return naps;
	}

	@Test
	void isExportAllowed() {

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

		Class<?>[] allowMeAllowedClasses = {
				//ExportServiceSample1.AllowMe.class, Not included b/c caller adds
				ExportServiceSample1.AllowMe.AllowMe1.class,
				ExportServiceSample1.AllowMe.ExportMe1.class,
				ExportServiceSample1.AllowMe.ImUnsure1.class,
				ExportServiceSample1.AllowMe.ImUnsure1.AllowMe2.class,
				ExportServiceSample1.AllowMe.ImUnsure1.ExportMe2.class,
				ExportServiceSample1.AllowMe.ImUnsure1.ImUnsure2.class
		};


		Class<?>[] exportMeAllowedClasses = {
				//ExportServiceSample1.ExportMe.class, Not included b/c caller adds
				ExportServiceSample1.ExportMe.AllowMe1.class,
				ExportServiceSample1.ExportMe.ExportMe1.class,
				ExportServiceSample1.ExportMe.ImUnsure1.class,
				ExportServiceSample1.ExportMe.ImUnsure1.AllowMe2.class,
				ExportServiceSample1.ExportMe.ImUnsure1.ExportMe2.class,
				ExportServiceSample1.ExportMe.ImUnsure1.ImUnsure2.class
		};

		Class<?>[] imUnsureAllowedClasses = {
				ExportServiceSample1.ImUnsure.AllowMe1.class,
				ExportServiceSample1.ImUnsure.ExportMe1.class,
		};

		// At the top level, all the other lists plus the containers:
		// ExportServiceSample1.AllowMe & ExportServiceSample1.ExportMe.
		// The other two containers are not added b/c they are either disallowed or not explicitly
		// allowed.
		Class<?>[][] joinedAllowedClasses = {
				allowMeAllowedClasses, exportMeAllowedClasses, imUnsureAllowedClasses,
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
				Matchers.containsInAnyOrder(imUnsureAllowedClasses));

		//
		// classes within DisallowMe
		assertThat(
				"Degenerate case:  This method should never be called with a class directly or " +
						"indirectly marked to disallow export.  This method explicitly does not check for " +
						"allowability of the class it is called on, only going towards the leaf of the tree.",
				es.stemInnerExportClasses(ExportServiceSample1.DisallowMe.ImUnsure1.class),
				Matchers.containsInAnyOrder( new Class<?>[]{
						ExportServiceSample1.DisallowMe.ImUnsure1.AllowMe2.class,
						ExportServiceSample1.DisallowMe.ImUnsure1.ExportMe2.class}));

	}

	@Test
	void buildExportClassesTest() throws IllegalAccessException {

		//
		// The whole sample class
		assertThat(
				es.buildExportClasses(toColl(ExportServiceSample1.class)),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		assertThat(
				"Requesting all of the allowed 1st level inner classes same result as the parent",
				es.buildExportClasses(toColl(
						ExportServiceSample1.AllowMe.class,
						ExportServiceSample1.ExportMe.class,
						ExportServiceSample1.ImUnsure.class)),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		assertThat(
				"Overlapping nested classes removes duplicates (parent + top level inner classes)",
				es.buildExportClasses(toColl(
						ExportServiceSample1.class,
						ExportServiceSample1.AllowMe.class,
						ExportServiceSample1.ExportMe.class,
						ExportServiceSample1.ImUnsure.class)),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		assertThat(
				"Lots of duplicates gives the same result",
				es.buildExportClasses(toColl(
						ExportServiceSample1.class,
						ExportServiceSample1.AllowMe.class,
						ExportServiceSample1.AllowMe.class,
						ExportServiceSample1.ExportMe.class,
						ExportServiceSample1.ExportMe.class,
						ExportServiceSample1.ImUnsure.class,
						ExportServiceSample1.ImUnsure.class,
						ExportServiceSample1.class)),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		//
		// The first level inner classes
		assertThat(
				es.buildExportClasses(toColl(ExportServiceSample1.AllowMe.class)),
				Matchers.containsInAnyOrder(allowMeAllowedClasses));

		assertThrows(IllegalAccessException.class,
				() -> es.buildExportClasses(toColl(ExportServiceSample1.DisallowMe.class)));

		assertThat(
				es.buildExportClasses(toColl(ExportServiceSample1.ExportMe.class)),
				Matchers.containsInAnyOrder(exportMeAllowedClasses));

		assertThat(
				es.buildExportClasses(toColl(ExportServiceSample1.ImUnsure.class)),
				Matchers.containsInAnyOrder(imUnsureAllowedClasses));

		//
		// ImUnsure inside DisallowMe
		assertThrows(IllegalAccessException.class,
				() -> es.buildExportClasses(toColl(ExportServiceSample1.DisallowMe.ImUnsure1.class)));

		//
		// ImUnsure inside AllowMe

		Class<?>[] allowThenUnsureClasses = {
				ExportServiceSample1.AllowMe.ImUnsure1.class,
				ExportServiceSample1.AllowMe.ImUnsure1.AllowMe2.class,
				ExportServiceSample1.AllowMe.ImUnsure1.ExportMe2.class,
				ExportServiceSample1.AllowMe.ImUnsure1.ImUnsure2.class
		};

		assertThat(
				es.buildExportClasses(toColl(ExportServiceSample1.AllowMe.ImUnsure1.class)),
				Matchers.containsInAnyOrder(allowThenUnsureClasses));
	}

	@Test
	void buildExportGroupsTest() throws IllegalAccessException {

		//
		assertThat("The whole sample class",
				es.buildExportGroups(toColl(ExportServiceSample1.class), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		//
		assertThat(
				"Requesting all of the allowed 1st level inner classes same result as the parent",
				es.buildExportGroups(toColl(
							ExportServiceSample1.AllowMe.class,
							ExportServiceSample1.ExportMe.class,
							ExportServiceSample1.ImUnsure.class
						), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		//
		assertThat("Overlapping nested classes removes duplicates (parent + top level inner classes)",
				es.buildExportGroups(toColl(
							ExportServiceSample1.class,
							ExportServiceSample1.AllowMe.class,
							ExportServiceSample1.ExportMe.class,
							ExportServiceSample1.ImUnsure.class
						), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		//
		assertThat("Double the whole sample class should have same result",
				es.buildExportGroups(toColl(ExportServiceSample1.class, ExportServiceSample1.class), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(rootAllowedClasses));

		//
		// The first level inner classes
		assertThat(
				es.buildExportGroups(toColl(ExportServiceSample1.AllowMe.class), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(allowMeAllowedClasses));

		assertThrows(IllegalAccessException.class,
				() -> es.buildExportGroups(toColl(ExportServiceSample1.DisallowMe.class), allClassesGroupProxies));

		assertThat(
				es.buildExportGroups(toColl(ExportServiceSample1.ExportMe.class), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(exportMeAllowedClasses));

		assertThat(
				es.buildExportGroups(toColl(ExportServiceSample1.ImUnsure.class), allClassesGroupProxies)
						.stream().map(g -> g.getProxiedGroup()).collect(Collectors.toList()),
				Matchers.containsInAnyOrder(imUnsureAllowedClasses));

		//
		assertThrows(IllegalAccessException.class,
				() -> es.buildExportGroups(toColl(ExportServiceSample1.DisallowMe.ImUnsure1.class), allClassesGroupProxies),
				"This unmarked class should inherit 'disallow' from its containing class.");

	}

	@Test
	void buildExportPropertiesTest() throws IllegalAccessException {

		assertThat(
				"The 'leafy-est' sample test classes have two Properties, so an easy test case",
				es.buildExportProperties(toColl(ExportServiceSample1.ImUnsure.AllowMe1.class), allClassesGroupProxies),
				Matchers.containsInAnyOrder(
						propertiesFromClasses(new Class<?>[]{ExportServiceSample1.ImUnsure.AllowMe1.class}, allClassesGroupProxies)
				));

		//
		// Exporting the whole root class
		assertThat("The whole sample class",
				es.buildExportProperties(toColl(ExportServiceSample1.class), allClassesGroupProxies),
				Matchers.containsInAnyOrder(propertiesFromClasses(rootAllowedClasses, allClassesGroupProxies)));

		//
		assertThat(
				"Requesting all of the allowed 1st level inner classes same result as the parent",
				es.buildExportProperties(toColl(
								ExportServiceSample1.AllowMe.class,
								ExportServiceSample1.ExportMe.class,
								ExportServiceSample1.ImUnsure.class
						), allClassesGroupProxies),
				Matchers.containsInAnyOrder(propertiesFromClasses(rootAllowedClasses, allClassesGroupProxies)));

		//
		assertThrows(IllegalAccessException.class,
				() -> es.buildExportProperties(toColl(ExportServiceSample1.DisallowMe.ImUnsure1.class), allClassesGroupProxies),
				"This unmarked class should inherit 'disallow' from its containing class.");

		assertThat("Overlapping nested classes removes duplicates (parent + top level inner classes)",
				es.buildExportProperties(toColl(
								ExportServiceSample1.class,
								ExportServiceSample1.AllowMe.class,
								ExportServiceSample1.ExportMe.class,
								ExportServiceSample1.ImUnsure.class
						), allClassesGroupProxies),
				Matchers.containsInAnyOrder(propertiesFromClasses(rootAllowedClasses, allClassesGroupProxies)));

		//
		assertThat("Double the whole sample class should have same result",
				es.buildExportProperties(toColl(ExportServiceSample1.class, ExportServiceSample1.class), allClassesGroupProxies),
				Matchers.containsInAnyOrder(propertiesFromClasses(rootAllowedClasses, allClassesGroupProxies)));

	}

	protected Property[] propertiesFromClasses(Class<?>[] clazzes, Collection<GroupProxy> allProxies) {
		Collection<GroupProxy> proxies = classesToProxies(clazzes, allProxies);
		return propertiesFromGroups(proxies);
	}

	protected Property[] propertiesFromGroups(Collection<GroupProxy> groupProxies) {
		return groupProxies.stream().flatMap(g -> g.getProperties().stream())
				.map(n -> n.property).collect(Collectors.toList()).stream().toArray(Property[]::new);
	}
	/**
	 * Convert an array of classes to GroupProxies, based on a collection of GroupProxies.
	 * @param clazzes
	 * @param allProxies
	 * @return
	 */
	protected Collection<GroupProxy> classesToProxies(Class<?>[] clazzes, Collection<GroupProxy> allProxies) {
		return Arrays.stream(clazzes).map(
						c -> allProxies.stream().filter(g -> g.getProxiedGroup().equals(c)).findFirst().get()
				)
				.collect(Collectors.toList());
	}

	/**
	 * Convert an array or individual items to a Collection.
	 * @param items
	 * @param <T>
	 * @return
	 */
	protected <T> Collection<T> toColl(T... items) {
		return Arrays.stream(items).collect(Collectors.toList());
	}

}