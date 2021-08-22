package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.export.AutoExportNotAllowed;
import org.yarnandtail.andhow.export.ExportNotAllowed;
import org.yarnandtail.andhow.export.AutoExport;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * Visitor that goes down up the hierarchy to find all classes eligable for export.
 *
 */
public class AutoExportDownwardVisitor {
	private Collection<ExportClassInfo> info;
	private Exporter exporter;

	public Collection<ExportClassInfo> getExportClassInfos() {
		return info;
	}

	Collection<ExportClassInfo> visitDownwards(Class<?> clazz) {
		info = new HashSet<>();

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

		ExportClassInfo info = new ExportClassInfo(clazz);

		processDisallowAnn(clazz, info);
		processAllowedAnn(clazz, info);
	}

	private void processDisallowAnn(Class<?> clazz, ExportClassInfo info) {
		AutoExportNotAllowed mna = clazz.getAnnotation(AutoExportNotAllowed.class);
		ExportNotAllowed ena = clazz.getAnnotation(ExportNotAllowed.class);

		//TODO: Don't keep search up thru NA even if we are allowed but have no prefs.
		if ((mna != null || ena != null) && ! info.allowed.isPresent()) {
			info.allowed = Optional.of(false);
		}
	}

	private void processAllowedAnn(Class<?> clazz, ExportClassInfo info) {
		AutoExport a = clazz.getAnnotation(AutoExport.class);

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
