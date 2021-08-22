package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ExportNotAllowed;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.export.SysPropExporter;

public class ExportServiceSample1 {

	@ManualExportAllowed
	static interface AllowMe {

		@ManualExportAllowed
		static interface AllowMe1 {

		}

		@ExportNotAllowed
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {
			@ManualExportAllowed
			static interface AllowMe2 {

			}

			@ExportNotAllowed
			static interface DisallowMe2 {

			}

			@GroupExport(exporter = SysPropExporter.class)
			static interface ExportMe2 {

			}

			static interface ImUnsure2 {

			}
		}
	}

	@ExportNotAllowed
	static interface DisallowMe {
		@ManualExportAllowed
		static interface AllowMe1 {

		}

		@ExportNotAllowed
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {
			@ManualExportAllowed
			static interface AllowMe2 {

			}

			@ExportNotAllowed
			static interface DisallowMe2 {

			}

			@GroupExport(exporter = SysPropExporter.class)
			static interface ExportMe2 {

			}

			static interface ImUnsure2 {

			}
		}
	}

	@GroupExport(exporter = SysPropExporter.class)
	static interface ExportMe {
		@ManualExportAllowed
		static interface AllowMe1 {

		}

		@ExportNotAllowed
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {
			@ManualExportAllowed
			static interface AllowMe2 {

			}

			@ExportNotAllowed
			static interface DisallowMe2 {

			}

			@GroupExport(exporter = SysPropExporter.class)
			static interface ExportMe2 {

			}

			static interface ImUnsure2 {

			}
		}
	}

	static interface ImUnsure {
		@ManualExportAllowed
		static interface AllowMe1 {

		}

		@ExportNotAllowed
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {

		}
	}
}
