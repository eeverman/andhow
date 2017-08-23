package org.yarnandtail.andhow.compile;

import java.util.ArrayList;

/**
 * First element is the most leafy enclosed element.
 */
public class ProxyNameModel extends ArrayList<String> {

	private String pkg;

	public ProxyNameModel(String pkgName) {
		pkg = pkgName;
	}

	public String getPackageName() {
		if (pkg != null) {
			return pkg;
		} else {
			return "";
		}
	}

	public String getQualifiedName() {
		if (pkg != null && pkg.length() > 0) {
			return pkg + "." + getClassName();
		} else {
			return getClassName();
		}
	}

	public String getClassName() {
		StringBuffer sb = new StringBuffer();
		sb.append(AndHowCompileProcessor.GENERATED_CLASS_PREFIX);
		for (int i = this.size() - 1; i >= 0; i--) {
			sb.append(AndHowCompileProcessor.GENERATED_CLASS_NESTED_SEP).append(this.get(i));
		}
		return sb.toString();
	}

}
