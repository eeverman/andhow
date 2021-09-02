package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyExportImpl implements PropertyExport {

	private final Property<?> property;
	private final Class<?> containingClass;
	private final EXPORT_CANONICAL_NAME canNameOpt;
	private final EXPORT_OUT_ALIASES aliasOpt;

	public PropertyExportImpl(Property<?> property, Class<?> containingClass,
			EXPORT_CANONICAL_NAME canonicalNameOpt, EXPORT_OUT_ALIASES outAliasOpt) {

		this.property = property;
		this.containingClass = containingClass;
		this.canNameOpt = canonicalNameOpt;
		this.aliasOpt = outAliasOpt;
	}

	@Override
	public Property<?> getProperty() {
		return property;
	}

	@Override
	public Class<?> getContainingClass() {
		return containingClass;
	}

	@Override
	public EXPORT_CANONICAL_NAME getCanonicalNameOption() {
		return canNameOpt;
	}

	@Override
	public EXPORT_OUT_ALIASES getOutAliasOption() {
		return aliasOpt;
	}

	@Override
	public List<String> getPreferedNames() {
		List<String> names = new ArrayList(1);

		List<String> aliasNames = getOutAliasNames();

		if (aliasOpt.equals(EXPORT_OUT_ALIASES.ALWAYS)) {
			names.addAll(aliasNames);
		}

		if (
				canNameOpt.equals(EXPORT_CANONICAL_NAME.ALWAYS) ||
				(canNameOpt.equals(EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS) && aliasNames.isEmpty())) {
			names.add(getCanonicalName());
		}

		return names;
	}

	@Override
	public List<String> getOutAliasNames() {
		return AndHow.instance().getAliases(property).stream().filter(n -> n.isOut())
				.map(n -> n.getEffectiveOutName()).collect(Collectors.toList());
	}

	@Override
	public String getCanonicalName() {
		return AndHow.instance().getCanonicalName(property);
	}

	@Override
	public Object getValue() {
		return property.getValue();
	}

	@Override
	public String getValueAsString() {
		Object o = getValue();
		return (o != null)? o.toString() : null;
	}
}
