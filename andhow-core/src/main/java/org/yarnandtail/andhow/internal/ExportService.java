package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.AllowExport;
import org.yarnandtail.andhow.DisallowExport;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.AndHowLog;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class ExportService {

	static AndHowLog LOG = AndHowLog.getLogger(ExportService.class);

	/**
	 * Expands a list of {@code GroupProxy}s to all nested {@code GroupProxy}s that are allowed to be
	 * exported.
	 *
	 * @param requestedGroupProxies The initial, unexpanded collection of {@code GroupProxy}s.
	 * @param allGroupProxies The complete list of all {@code GroupProxy}s.
	 * @return Expanded set with the requestedGroupProxies plus other nested {@code GroupProxy}s
	 */
	public Set<GroupProxy> expandGroupProxySet(Collection<GroupProxy> requestedGroupProxies,
			Collection<GroupProxy> allGroupProxies) {

		final Set<GroupProxy> exportGroups = new HashSet();

		for (GroupProxy proxy : requestedGroupProxies) {
			proxy.getCanonicalName();

		}

		return exportGroups;
	}

	public Set<GroupProxy> buildExplicitGroupProxySet(Collection<Class<?>> requestedExportClasses,
			Collection<GroupProxy> allGroupProxies) throws IllegalAccessException {

		//Will exclude duplicates automatically
		final Set<GroupProxy> exportGroups = new HashSet();

		for (Class<?> clazz : requestedExportClasses) {

			Optional<Boolean> allowed = isExportAllowed(clazz);
			if (allowed.isPresent() && ! allowed.get()) {
				throw new IllegalAccessException("The class '" + clazz + "' is not annotated for export. " +
						"To export this class, annotate it with @" + AllowExport.class.getCanonicalName());
			} else {

				//Find the GroupProxy
				Optional<GroupProxy> proxy = allGroupProxies.stream().filter(
						g -> g.getProxiedGroup().equals(clazz)
				).findFirst();

				if (proxy.isPresent()) {
					exportGroups.add(proxy.get());
				} else {
					LOG.warn("The class '" + clazz.getCanonicalName() + "' is requested for " +
							"export, but this class has no AndHow Properties.");
				}
			}
		}

		return exportGroups;
	}

	public Collection<Property<?>> buildExportProperties(ValidatedValues values, List<GroupProxy> groupList,
			Collection<Class<?>> expGroups) throws IllegalAccessException {

		Collection<GroupProxy> groups = buildExportGroups(groupList, expGroups);

		List<Property<?>> props = groups.stream()
				.flatMap(g -> g.getProperties().stream())
				.map(n -> n.property).collect(Collectors.toList());

		return props;

	}

	public Collection<GroupProxy> buildExportGroups(List<GroupProxy> groupList,
			Collection<Class<?>> expGroups) throws IllegalAccessException {

		Collection<Class<?>> stemmedClasses = buildExportClasses(expGroups);

		List<GroupProxy> groups = groupList.stream()
				.filter(g -> stemmedClasses.contains(g.getProxiedGroup()))
				.collect(Collectors.toList());

		return groups;
	}

	/**
	 * Build a complete list of all classes eligible to be exported, based on the
	 * annotations on the requestedClasses, their containing classes, and the nested classes contained
	 * within them.
	 *
	 * See class javadocs for an explanation of which classes can be exported.  The list returned from
	 * this method are regardless of if the classes contain AndHow Properties.
	 *
	 * @param requestedClasses One of more classes which are requested to be exported.
	 * @return The classes eligible for export, starting with the requestedClasses and going down
	 * the hierarchy to contained classes, in no specific order.
	 * @throws IllegalAccessException If one of the requestedClasses is disallowed for export.
	 */
	protected Collection<Class<?>> buildExportClasses(Collection<Class<?>> requestedClasses) throws IllegalAccessException {
		Collection<Class<?>> exportClasses = new HashSet<Class<?>>();

		for (Class<?> clazz : requestedClasses) {
			Optional<Boolean> allow = isExportAllowed(clazz);

			if (allow.isPresent() && ! allow.get()) {
				//TODO:  Better message about nesting and disallowed
				throw new IllegalAccessException("The class '" + clazz + "' is not annotated for export. " +
						"To export this class, annotate it with @" + AllowExport.class.getCanonicalName());
			} else if (allow.isPresent() && allow.get()) {
				exportClasses.add(clazz);
			}

			//find contained allowed classes, regardless if the container was allowed or unmarked.
			exportClasses.addAll(stemInnerExportClasses(clazz));
		}

		return exportClasses;
	}

	/**
	 * Returns an {@code Optional<Boolean>} to indicate if this class or its declaring class are
	 * annotated to support export.
	 *
	 * If {@code True} is returned, either this class is annotated to allow exports, or
	 * this class is an innerclass of a class that is.
	 * If {@code False} is returned, this class is annotated to block exports, or
	 * this class is an innerclass of a class that is.
	 * <p>
	 * If {@code Optional.isPresent()} returns false, there is no export annotation on
	 * this class or a declaring class.  In that case, export for this class is not allowed,
	 * but nested innerclasses may be, so stemming from the current class should be done.
	 *
	 * @param clazz
	 * @return
	 */
	protected Optional<Boolean> isExportAllowed(Class<?> clazz) {
		Class<?>[] ALLOW_EXPORT_ANNOTATIONS = {AllowExport.class, GroupExport.class};

		Annotation[] anns = clazz.getAnnotations();

		for (Annotation a: anns) {
			for (Class<?> allow : ALLOW_EXPORT_ANNOTATIONS) {
				if (a.annotationType().equals(allow)) return Optional.of(Boolean.TRUE);
			}
			if (a.annotationType().equals(DisallowExport.class)) return Optional.of(Boolean.FALSE);
		}

		if (clazz.getDeclaringClass() != null) {
			return isExportAllowed(clazz.getDeclaringClass());
		}

		return Optional.empty();
	}

	/**
	 * Find all inner types of the clazz that can be exported.
	 *
	 * Its not an error to find {@code @DisallowExport} annotations on these inner classes,
	 * it just means they are not included.  For this reason, the caller must handle allow/
	 * disallow on the root class on it's own:  The passed class is not checked for exportability and is not
	 * added to the returned list.
	 * <p>
	 * TLDR:  Don't pass this method a class marked as {@code @DisallowExport} or whose
	 * declaring class is marked 'disallow'.  This method will return results, even though
	 * returning results in that case 'breaks the rules.
	 *
	 * @param clazz
	 * @return
	 */
	protected Collection<Class<?>> stemInnerExportClasses(Class<?> clazz) {

		final List<Class<?>> clazzList = new ArrayList<>();

		for (Class<?> c : clazz.getDeclaredClasses()) {

			Optional<Boolean> allowed = isExportAllowed(c);

			if (allowed.isPresent() && ! allowed.get()) {
				continue;	//No export allowed from here on down the hierarchy
			} else {

				if (allowed.isPresent() && allowed.get()) {
					clazzList.add(c);	//This one can be exported
				}

				//allowed or ambiguous might have nested that are allowed
				clazzList.addAll(stemInnerExportClasses(c));
			}

		}

		return clazzList;
	}
}
