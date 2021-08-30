package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.export.SysPropExporter;

public class ExportServiceSample {

	@ManualExportAllowed(exportByCanonicalName = EXPORT_CANONICAL_NAME.ALWAYS,
			exportByOutAliases = EXPORT_OUT_ALIASES.NEVER)
	static interface AllowMe {

		@ManualExportAllowed(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.NEVER)
		static interface AllowMe1 { }

		@ManualExportNotAllowed
		static interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		static interface ExportMe1 { }

		static interface ImUnsure1 {
			@ManualExportAllowed(exportByCanonicalName = EXPORT_CANONICAL_NAME.NEVER,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS)
			static interface AllowMe2 { }

			@ManualExportNotAllowed
			static interface DisallowMe2 { }

			@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
			static interface ExportMe2 { }

			static interface ImUnsure2 { }
		}
	}

	////////
	////////

	@ManualExportNotAllowed
	static interface DisallowMe {

		@ManualExportAllowed(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.NEVER)
		static interface AllowMe1 { }

		@ManualExportNotAllowed
		static interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		static interface ExportMe1 { }

		static interface ImUnsure1 {
			@ManualExportAllowed(exportByCanonicalName = EXPORT_CANONICAL_NAME.NEVER,
					exportByOutAliases = EXPORT_OUT_ALIASES.NEVER)
			static interface AllowMe2 { }

			@ManualExportNotAllowed
			static interface DisallowMe2 { }

			@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
			static interface ExportMe2 { }

			static interface ImUnsure2 { }
		}
	}

	////////
	////////

	@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
			exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
	static interface ExportMe {
		@ManualExportAllowed
		static interface AllowMe1 { }

		@ManualExportNotAllowed
		static interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		static interface ExportMe1 { }

		static interface ImUnsure1 {
			@ManualExportAllowed
			static interface AllowMe2 { }

			@ManualExportNotAllowed
			static interface DisallowMe2 { }

			@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
			static interface ExportMe2 { }

			static interface ImUnsure2 { }
		}
	}

	static interface ImUnsure {
		@ManualExportAllowed
		static interface AllowMe1 { }

		@ManualExportNotAllowed
		static interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		static interface ExportMe1 { }

		static interface ImUnsure1 { }
	}

	@ManualExportAllowed
	@ManualExportNotAllowed
	static interface ImConfused {
		static interface ImAlsoConfused { }
	}
}
