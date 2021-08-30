package org.yarnandtail.andhow.internal.export;

import java.util.Optional;

import static org.yarnandtail.andhow.api.Exporter.EXPORT_CANONICAL_NAME;
import static org.yarnandtail.andhow.api.Exporter.EXPORT_OUT_ALIASES;

/**
 * All the needed info about a class that may be exported.
 *
 * There should never be more than one instance of a given Class, or if there is,
 * only created to find an existing one in a Map.
 */
public class ExportClassInfo {
	final Class<?> clazz;
	Optional<Boolean> allowed = Optional.empty();
	Optional<EXPORT_CANONICAL_NAME> canonicalOption = Optional.empty();
	Optional<EXPORT_OUT_ALIASES> outAliasOption = Optional.empty();

	public ExportClassInfo(Class<?> clazz) {
		if (clazz == null) throw new RuntimeException("The class cannot be null");

		this.clazz = clazz;
	}

	boolean isAllowExport() {
		return allowed.isPresent() && allowed.get();
	}

	/**
	 * Meaningless if isAllowExport is false.
	 *
	 * @return
	 */
	EXPORT_CANONICAL_NAME exportByCanonicalName() {
		if (canonicalOption.isPresent()) {
			return canonicalOption.get();
		} else {
			return EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS;
		}
	}

	/**
	 * Meaningless if isAllowExport is false.
	 *
	 * @return
	 */
	EXPORT_OUT_ALIASES exportByOutAliases() {
		if (outAliasOption.isPresent()) {
			return outAliasOption.get();
		} else {
			return EXPORT_OUT_ALIASES.ALWAYS;
		}
	}

	/**
	 * Based on the clazz instance and ExportClassInfo.class only.
	 * @return
	 */
	@Override
	public int hashCode() {
		return clazz.hashCode() + ExportClassInfo.class.hashCode();
	}

	/**
	 * Equality based on the clazz instance and ExportClassInfo.class only.
	 *
	 * There should never be more than one instance for the same clazz.
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ExportClassInfo) {
			return clazz.equals(((ExportClassInfo)obj).clazz);
		} else {
			return false;
		}
	}
}
