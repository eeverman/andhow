package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;

import java.util.List;

public interface PropertyExport {
	Property<?> getProperty();
	Class<?> getContainingClass();
	EXPORT_CANONICAL_NAME getCanonicalNameOption();
	EXPORT_OUT_ALIASES getOutAliasOption();
	List<String> getPreferedNames();
	List<String> getOutAliasNames();
	String getCanonicalName();
	Object getValue();
	String getValueAsString();
}
