package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.export.*;
import static org.yarnandtail.andhow.api.Exporter.*;

import java.util.Optional;

/**
 * Visitor that climbs up the hierarchy to determine the export info of this
 * the passed class.
 *
 * This never needs to be done for an auto-export because it is discovered by the annotation
 * during processing, so we never need to 'climb up' to find the settings.
 */
public class ManualExportClassUpwardVisitor {
	private ExportClassInfo info;

	public ExportClassInfo getExportClassInfo() {
		return info;
	}

	ExportClassInfo visitUpwards(Class<?> clazz) {
		info = new ExportClassInfo(clazz);

		Class<?> currentClass = clazz;

		while (true) {

			visit(currentClass);

			if (! isFinished() && clazz.getDeclaringClass() != null) {
				currentClass = clazz.getDeclaringClass();
			} else {
				break;
			}
		}

		return info;
	}

	void visit(Class<?> clazz) {

		if (info == null) info = new ExportClassInfo(clazz);

		processDisallowAnn(clazz);
		processAllowedAnn(clazz);
	}

	private void processDisallowAnn(Class<?> clazz) {
		ManualExportNotAllowed mna = clazz.getAnnotation(ManualExportNotAllowed.class);
		ExportNotAllowed ena = clazz.getAnnotation(ExportNotAllowed.class);

		//TODO: Don't keep search up thru NA even if we are allowed but have no prefs.
		if ((mna != null || ena != null) && ! info.allowed.isPresent()) {
			info.allowed = Optional.of(false);
		}
	}

	private void processAllowedAnn(Class<?> clazz) {
		ManualExportAllowed a = clazz.getAnnotation(ManualExportAllowed.class);

		if (a != null) {
			if (! info.allowed.isPresent()) {
				info.allowed = Optional.of(true);
			}

			info.canonicalOption = Optional.of(a.exportByCanonicalName());
			info.outAliasOption = Optional.of(a.exportByOutAliases());
		}
	}

	boolean isFinished() {
		return info.allowed.isPresent();
	}

}
