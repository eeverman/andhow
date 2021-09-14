package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.export.PropertyExport;
import org.yarnandtail.andhow.internal.NameAndProperty;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

/**
 * A service for performing a manual export.
 * <p>
 * Manual exports are initiated via {@link org.yarnandtail.andhow.AndHow#export(Class[])} - See
 * that method for usage details and examples.
 */
public class ManualExportService {

	Class<?>[] DISALLOW_EXPORT_ANNOTATIONS = {ManualExportNotAllowed.class};

	/**
	 * Perform an export for a list of Classes.
	 * <p>
	 * An export takes place as follows:
	 * <ul>
	 * <li>Each the classes in the list of exportRoots is processed sequentially</li>
	 * <li>If the class is not annotated with {@link ManualExportAllowed}, an exception is thrown.</li>
	 * <li>Calling to {@link #exportClassAndChildren}, all {@link Property}s in the class are
	 * discovered and a {@link PropertyExport} instance made for each one.</li>
	 * <li>Any inner classes are discovered and also exported, checking to see if they have a
	 *   {@link ManualExportAllowed} annotation that might set different export options.
	 * </li>
	 * </ul>
	 * A list of all classes that have been exported is kept to prevent overlapping classes from
	 * creating duplicate exports, e.g. if a class and an innerclass of that class are included in
	 * the exportRoots list, the result should still not have duplicates.
	 * <p>
	 * Only classes for which a {@link GroupProxy} exists have Properties and the proxies have the
	 * list of Properties contained in each class.
	 * <p>
	 * @param exportRoots The list of classes who's Properties should be exported.
	 * @param groupList The list of GroupProxy's, created during AndHow initialization.
	 * @return A Stream<PropertyExport> to be mapped, filtered & collected as needed.
	 * @throws IllegalAccessException If any of the exportRoots are not annotated (directly or
	 *   indirectly via a containing class) with {@link ManualExportAllowed}.
	 */
	public Stream<PropertyExport> doManualExport(List<Class<?>> exportRoots, Collection<GroupProxy> groupList) throws IllegalAccessException {

		Set<Class<?>> alreadyExportedClasses = new HashSet();
		Collection<PropertyExport> propExports = new ArrayList();

		for (Class<?> clazz : exportRoots) {

			Optional<ManualExportAllowed> allow = findEffectiveAllowAnnotation(clazz);

			if (! allow.isPresent()) {
				throw new IllegalAccessException("The class '" + clazz + "' is not annotated to allow manual export. " +
						"To export this class, annotate it with @" + ManualExportAllowed.class.getCanonicalName());
			}

			exportClassAndChildren(clazz, allow.get().useCanonicalName(),
					allow.get().useOutAliases(),	alreadyExportedClasses, groupList, propExports);
		}

		return propExports.stream();
	}

	/**
	 * Export this class and all contained, nested inner classes if permitted.
	 *
	 * The clazz and the effective canonicalOption & outAliasOption export options must match and
	 * the passed clazz and must be legal to export as defined by
	 * {@link #findEffectiveAllowAnnotation}, otherwise incorrect export options
	 * will be used and/or non-exportable (ie private) Properties exported.
	 * <p>
	 * Contained classes that are discovered and annotated with {@link ManualExportNotAllowed} will
	 * not be exported and no further search within those classes is performed.
	 * <p>
	 * The {@code alreadyExportedClasses} collection is used to prevent export of classes already
	 * exported.  This can happen when this method is called on a list of classes, some of which may
	 * overlap in their contained classes.
	 * <p>
	 * @param clazz The class (and potentially its contained classes) to export.
	 * @param canonicalOption The option as annotated on passed class or a class that contains it.
	 * @param outAliasOption The option as annotated on passed class or a class that contains it.
	 * @param alreadyExportedClasses Class which have already been exported, to prevent duplicate
	 *   exports when a user passes classes nested classes (i.e. a parent class and an inner class).
	 */
	protected void exportClassAndChildren(Class<?> clazz,
			EXPORT_CANONICAL_NAME canonicalOption, EXPORT_OUT_ALIASES outAliasOption,
			Set<Class<?>> alreadyExportedClasses, Collection<GroupProxy> groupList,
			Collection<PropertyExport> propExports) {

		//Already exported as part of a larger group of export classes?
		if (alreadyExportedClasses.contains(clazz)) return;

		exportClass(clazz, canonicalOption, outAliasOption, groupList, propExports);
		alreadyExportedClasses.add(clazz);

		for (Class<?> inner : clazz.getDeclaredClasses()) {

			if (isManualExportDisallowed(inner)) {

				continue;	//No export allowed from this class on down the hierarchy

			} else {

				//Export not disallowed for this branch of the tree

				Optional<ManualExportAllowed> mea = getAllowAnnotation(inner);

				if (mea.isPresent()) {
					//Explicit new export options
					exportClassAndChildren(inner, mea.get().useCanonicalName(),
							mea.get().useOutAliases(), alreadyExportedClasses, groupList, propExports);
				} else {
					//Inherit parent class export options
					exportClassAndChildren(inner, canonicalOption, outAliasOption, alreadyExportedClasses, groupList, propExports);
				}

			}
		}
	}

	/**
	 * Export the Properties contained in a single class with no recursion.
	 * <p>
	 * @param clazz The class to export
	 * @param canNameOpt canonical name option
	 * @param outAliasOpt out alias names option
	 * @param groupList The {@link GroupProxy} collection, created during AndHow initialization.
	 * @param propExports The collection to add the exports to, one per Property.
	 */
	protected void exportClass(Class<?> clazz,
			EXPORT_CANONICAL_NAME canNameOpt, EXPORT_OUT_ALIASES outAliasOpt,
			Collection<GroupProxy> groupList, Collection<PropertyExport> propExports) {

		Optional<GroupProxy> proxy = groupList.stream().filter(g -> g.getProxiedGroup().equals(clazz)).findFirst();

		if (proxy.isPresent()) {
			for (NameAndProperty nap : proxy.get().getProperties()) {
				propExports.add(
						new PropertyExportImpl(nap.property, clazz, canNameOpt, outAliasOpt));
			}
		}
	}


	/**
	 * Returns the effective {@link ManualExportAllowed} annotation instance if it exists.
	 * <p>
	 * The effective annotation may be on the clazz itself or a containing class if the clazz is an
	 * inner class.  If no annotation is found or a {@link ManualExportNotAllowed} annotation is found
	 * closer in the inner class hierarchy, then this class is not manually exportable.
	 * <p>
	 * @param clazz Determine if this class or its container is annotated for manual export.
	 * @return The Optional ManualExportAllowed annotation, if it exists.
	 * @throws IllegalStateException If this class is annotated to both allow and disallow manual exports.
	 */
	protected Optional<ManualExportAllowed> findEffectiveAllowAnnotation(Class<?> clazz) throws IllegalStateException {

		Optional<ManualExportAllowed> allow = Optional.empty();
		Optional<Boolean> disallow = Optional.empty();

		if (clazz.isAnnotationPresent(ManualExportAllowed.class)) allow = getAllowAnnotation(clazz);
		if (isManualExportDisallowed(clazz)) disallow = Optional.of(true);

		if (allow.isPresent() && disallow.isPresent()) {

			throw new IllegalStateException("A class cannot be annotated with both a '"
					+ ManualExportAllowed.class.getCanonicalName() + "' annotation and a disallow annotation.");

		} else if (allow.isPresent() || disallow.isPresent()) {
			return allow;	//One or the other is present - The state of allow will indicate which
		} else {

			//Not annotated - does it have a containing class?
			if (clazz.getDeclaringClass() != null) {
				return findEffectiveAllowAnnotation(clazz.getDeclaringClass());
			} else {
				return allow;
			}
		}

	}

	/**
	 * Gets the {@link ManualExportAllowed} annotation directly on this class.
	 * <p>
	 * @param clazz The class to find the annotation on.
	 * @return An Optional<ManualExportAllowed> if the annotation exists.
	 */
	protected Optional<ManualExportAllowed> getAllowAnnotation(Class<?> clazz) {
		return Optional.ofNullable(clazz.getAnnotation(ManualExportAllowed.class));
	}


	/**
	 * Returns {@code true} if manual export is explicitly annotated to not be allowed
	 * on this class.
	 * <p>
	 * This does not consider containing classes, it only checks if there is a 'not allowed'
	 * annotation on this class exclusively.
	 * <p>
	 * @param clazz The class to check
	 * @return True if manual exports are explicitly not allowed on this class.
	 */
	protected boolean isManualExportDisallowed(Class<?> clazz) {
		for (Class<?> da : DISALLOW_EXPORT_ANNOTATIONS) {
			if (clazz.isAnnotationPresent((Class<? extends Annotation>)da)) return true;
		}
		return false;
	}

}
