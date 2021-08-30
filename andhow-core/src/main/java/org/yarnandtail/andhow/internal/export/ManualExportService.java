package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.util.AndHowLog;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class ManualExportService {

	static AndHowLog LOG = AndHowLog.getLogger(ManualExportService.class);

	List<Class<? extends Annotation>> DISALLOW_EXPORT_ANNOTATIONS = List.of(ManualExportNotAllowed.class);

	void handleManualExport(List<Class<?>> exportRoots, ExportPropertyHandler handler,
			Collection<GroupProxy> groupList) throws IllegalAccessException {

		ExportCallback callback = new ExportCallback(handler, groupList);
		Set<Class<?>> alreadyExportedClasses = new HashSet();

		for (Class<?> clazz : exportRoots) {

			Optional<ManualExportAllowed> allow = findAllowAnnotation(clazz);

			if (! allow.isPresent()) {
				throw new IllegalAccessException("The class '" + clazz + "' is not annotated to allow manual export. " +
						"To export this class, annotate it with @" + ManualExportAllowed.class.getCanonicalName());
			}

			stemInnerExportClasses(clazz, allow.get().exportByCanonicalName(),
					allow.get().exportByOutAliases(),	alreadyExportedClasses, callback);
		}
	}

	/**
	 * Returns {@code true} if this class or its declaring class are annotated to allow manual export.
	 * <p><ul>
	 * <li>If {@code true} is returned, either this class is annotated with {@link ManualExportAllowed}
	 * or this class is an innerclass of a class that is.  The default is to not allow exports,
	 * so it requires an explicit annotation to allow.
	 * <li>If {@code false} is returned, there is no 'allow' annotation, or there is an explicit
	 * {@link ManualExportNotAllowed} on this class or parent class.
	 * </ul><p>
	 *
	 * @param clazz
	 * @return
	 */
	protected Optional<ManualExportAllowed> findAllowAnnotation(Class<?> clazz) {

		Optional<ManualExportAllowed> allow = Optional.empty();
		Optional<Boolean> disallow = Optional.empty();

		if (clazz.isAnnotationPresent(ManualExportAllowed.class)) allow = Optional.of(clazz.getAnnotation(ManualExportAllowed.class));
		if (isManualExportDisallowed(clazz)) disallow = Optional.of(true);

		if (allow.isPresent() && disallow.isPresent()) {

			throw new IllegalStateException("A class cannot be annotated with both a '"
					+ ManualExportAllowed.class.getCanonicalName() + "' annotation and a disallow annotation.");

		} else if (allow.isPresent() || disallow.isPresent()) {
			return allow;	//One or the other is present - The state of allow will indicate which
		} else {

			//Not annotated - does it have a containing class?
			if (clazz.getDeclaringClass() != null) {
				return findAllowAnnotation(clazz.getDeclaringClass());
			} else {
				return allow;
			}
		}

	}


	/**
	 * Returns {@code false} if manual export is explicitly annotated as not allowed
	 * on this class.
	 *
	 * @param clazz The class to check
	 * @return
	 */
	protected boolean isManualExportDisallowed(Class<?> clazz) {
		for (Class<? extends Annotation> da : DISALLOW_EXPORT_ANNOTATIONS) {
			if (clazz.isAnnotationPresent(da)) return true;
		}
		return false;
	}

	protected Optional<ManualExportAllowed> getManualExportAnnotation(Class<?> clazz) {
		return Optional.of(clazz.getAnnotation(ManualExportAllowed.class));
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
	 * NOTE:  Assuming here that this is called on a portion of the tree that is explicitly
	 * allowed.  Don't call on a tree that is disallowed or non-explicit.
	 * TODO:  Disallow disallow and allow on the same class.
	 * @param clazz
	 * @return
	 */
	protected void stemInnerExportClasses(Class<?> clazz,
			Exporter.EXPORT_CANONICAL_NAME canonicalOption, Exporter.EXPORT_OUT_ALIASES outAliasOption,
			Set<Class<?>> alreadyExportedClasses, ExportCallback callback) {

		//Already exported as part of a larger group of export classes?
		if (alreadyExportedClasses.contains(clazz)) return;

		callback.handleGroup(clazz, canonicalOption, outAliasOption);
		alreadyExportedClasses.add(clazz);

		for (Class<?> c : clazz.getDeclaredClasses()) {

			if (isManualExportDisallowed(c)) {

				continue;	//No export allowed from this class on down the hierarchy

			} else {

				//Export not disallowed for this branch of the tree

				Optional<ManualExportAllowed> mea = getManualExportAnnotation(clazz);

				if (mea.isPresent()) {
					//Explicit new export options
					stemInnerExportClasses(c, mea.get().exportByCanonicalName(),
							mea.get().exportByOutAliases(), alreadyExportedClasses, callback);
				} else {
					//Inherit parent class export options
					stemInnerExportClasses(c, canonicalOption, outAliasOption, alreadyExportedClasses, callback);
				}

			}
		}
	}

	protected static class ExportCallback {

		final Collection<GroupProxy> groupList;
		final ExportPropertyHandler handler;

		ExportCallback(ExportPropertyHandler handler, Collection<GroupProxy> groupList) {
			this.groupList = groupList;
			this.handler = handler;
		}

		void handleGroup(Class<?> clazz,
				Exporter.EXPORT_CANONICAL_NAME canonicalOption, Exporter.EXPORT_OUT_ALIASES outAliasOption) {

			Optional<GroupProxy> proxy = groupList.stream().filter(g -> g.getProxiedGroup().equals(clazz)).findFirst();

			if (proxy.isPresent()) {
				for (NameAndProperty nap : proxy.get().getProperties()) {
					handler.handleProperty(nap.property, canonicalOption, outAliasOption);
				}
			}

		}
	}

	public static interface ExportPropertyHandler {
		void handleProperty(Property property,
				Exporter.EXPORT_CANONICAL_NAME canonicalOption, Exporter.EXPORT_OUT_ALIASES outAliasOption);
	}
}
