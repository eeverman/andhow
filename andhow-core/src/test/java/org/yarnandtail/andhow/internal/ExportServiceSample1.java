package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.AllowExport;
import org.yarnandtail.andhow.DisallowExport;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.export.SysPropExporter;

public class ExportServiceSample1 {

	@AllowExport
	static interface AllowMe {

		@AllowExport
		static interface AllowMe1 {

		}

		@DisallowExport
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {
			@AllowExport
			static interface AllowMe2 {

			}

			@DisallowExport
			static interface DisallowMe2 {

			}

			@GroupExport(exporter = SysPropExporter.class)
			static interface ExportMe2 {

			}

			static interface ImUnsure2 {

			}
		}
	}

	@DisallowExport
	static interface DisallowMe {
		@AllowExport
		static interface AllowMe1 {

		}

		@DisallowExport
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {
			@AllowExport
			static interface AllowMe2 {

			}

			@DisallowExport
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
		@AllowExport
		static interface AllowMe1 {

		}

		@DisallowExport
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {
			@AllowExport
			static interface AllowMe2 {

			}

			@DisallowExport
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
		@AllowExport
		static interface AllowMe1 {

		}

		@DisallowExport
		static interface DisallowMe1 {

		}

		@GroupExport(exporter = SysPropExporter.class)
		static interface ExportMe1 {

		}

		static interface ImUnsure1 {

		}
	}
}
