package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

public class ExportServiceSample {

	@ManualExportAllowed(useCanonicalName = EXPORT_CANONICAL_NAME.ALWAYS,
			useOutAliases = EXPORT_OUT_ALIASES.NEVER)
	public interface AllowMe {

		IntProp INT1 = IntProp.builder()
				.aliasIn("int1in").aliasOut("int1out").aliasInAndOut("int1inandout").build();

		@ManualExportAllowed(useCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				useOutAliases = EXPORT_OUT_ALIASES.NEVER)
		interface AllowMe1 {
			StrProp STR1 = StrProp.builder()
					.aliasIn("a.a.str1in").aliasOut("a.a.str1out").aliasInAndOut("a.a.str1inandout").build();
		}

		@ManualExportNotAllowed
		interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		interface ExportMe1 {	}

		interface ImUnsure1 {
			@ManualExportAllowed(useCanonicalName = EXPORT_CANONICAL_NAME.NEVER,
					useOutAliases = EXPORT_OUT_ALIASES.ALWAYS)
			interface AllowMe2 {
				StrProp STR1 = StrProp.builder()
						.aliasIn("a.u.a.str1in").aliasOut("a.u.a.str1out").aliasInAndOut("a.u.a.str1inandout").build();
			}

			@ManualExportNotAllowed
			interface DisallowMe2 { }

			@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
			interface ExportMe2 { }

			interface ImUnsure2 { }
		}
	}

	////////
	////////

	@ManualExportNotAllowed
	interface DisallowMe {

		@ManualExportAllowed(useCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				useOutAliases = EXPORT_OUT_ALIASES.NEVER)
		interface AllowMe1 { }

		@ManualExportNotAllowed
		interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		class ExportMe1 { }

		interface ImUnsure1 {
			@ManualExportAllowed(useCanonicalName = EXPORT_CANONICAL_NAME.NEVER,
					useOutAliases = EXPORT_OUT_ALIASES.NEVER)
			interface AllowMe2 { }

			@ManualExportNotAllowed
			interface DisallowMe2 { }

			@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
			interface ExportMe2 { }

			interface ImUnsure2 { }
		}
	}

	////////
	////////

	@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
			exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
	public interface ExportMe {
		@ManualExportAllowed
		interface AllowMe1 {
			StrProp STR1 = StrProp.builder()
					.aliasIn("e.a.str1in").aliasOut("e.a.str1out").aliasInAndOut("e.a.str1inandout").build();
		}

		@ManualExportNotAllowed
		interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		interface ExportMe1 { }

		interface ImUnsure1 {
			@ManualExportAllowed
			interface AllowMe2 { }

			@ManualExportNotAllowed
			interface DisallowMe2 { }

			@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
					exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
			interface ExportMe2 { }

			interface ImUnsure2 { }
		}
	}

	interface ImUnsure {
		@ManualExportAllowed
		interface AllowMe1 { }

		@ManualExportNotAllowed
		interface DisallowMe1 { }

		@GroupExport(exportByCanonicalName = EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
				exportByOutAliases = EXPORT_OUT_ALIASES.ALWAYS, exporter = SysPropExporter.class)
		interface ExportMe1 { }

		interface ImUnsure1 { }
	}

	@ManualExportAllowed
	@ManualExportNotAllowed
	interface ImConfused {
		interface ImAlsoConfused { }
	}
}
