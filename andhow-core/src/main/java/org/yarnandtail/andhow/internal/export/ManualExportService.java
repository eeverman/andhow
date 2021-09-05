package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.export.PropertyExport;
import org.yarnandtail.andhow.export.PropertyExportImpl;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.util.AndHowLog;

import java.lang.annotation.Annotation;
import java.util.*;

//TODO:  The exceptions thrown should be runtime only, since the caller must be able to pass a class ref,
//the illegal access is unexpected.
public class ManualExportService {

	static AndHowLog LOG = AndHowLog.getLogger(ManualExportService.class);

	Class<?>[] DISALLOW_EXPORT_ANNOTATIONS = {ManualExportNotAllowed.class};


	public Collection<PropertyExport> doManualExport(List<Class<?>> exportRoots, Collection<GroupProxy> groupList) throws IllegalAccessException {

		Set<Class<?>> alreadyExportedClasses = new HashSet();
		Collection<PropertyExport> propExports = new ArrayList();

		for (Class<?> clazz : exportRoots) {

			Optional<ManualExportAllowed> allow = findEffectiveAllowAnnotation(clazz);

			if (! allow.isPresent()) {
				throw new IllegalAccessException("The class '" + clazz + "' is not annotated to allow manual export. " +
						"To export this class, annotate it with @" + ManualExportAllowed.class.getCanonicalName());
			}

			exportClassAndChildren(clazz, allow.get().exportByCanonicalName(),
					allow.get().exportByOutAliases(),	alreadyExportedClasses, groupList, propExports);
		}

		return propExports;
	}

	/**
	 * Export this class and all contained, nested inner classes if permitted.
	 *
	 * The clazz and the effective canonicalOption & outAliasOption export options must match and
	 * the passed clazz and must be legal to export as defined by
	 * {@link ManualExportService#findEffectiveAllowAnnotation}, otherwise incorrect export options
	 * will be used and/or non-exportable (ie private) Properties exported.
	 * <p>
	 * Contained classes that are discovered and annotated with {@link ManualExportNotAllowed} will
	 * not be exported and no further search within those classes is performed.
	 * <p>
	 * The {@code alreadyExportedClasses} collection is used to prevent export of classes already
	 * exported.  This can happen when this method is called on a list of classes, some of which may
	 * overlap in their contained classes.
	 * <p>
	 * Note that the export options (canonical and outAlias) are preferences in the annotations.
	 * The manual export handler is free to ignore preferences and form any type of export.
	 * <p>
	 * @param clazz The class (and potentially its contained classes) to export.
	 * @param canonicalOption
	 * @param outAliasOption
	 * @param alreadyExportedClasses
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
					exportClassAndChildren(inner, mea.get().exportByCanonicalName(),
							mea.get().exportByOutAliases(), alreadyExportedClasses, groupList, propExports);
				} else {
					//Inherit parent class export options
					exportClassAndChildren(inner, canonicalOption, outAliasOption, alreadyExportedClasses, groupList, propExports);
				}

			}
		}
	}

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
	 * inner class.  If no annotation is found or a {@link ManualExportNotAllowed} annnotation is found
	 * closer in the inner class hierarchy, then this class is not manually exportable.
	 *
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
	 *
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
	 *
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
