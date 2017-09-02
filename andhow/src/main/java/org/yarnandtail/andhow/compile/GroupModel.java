package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import javax.lang.model.element.*;

/**
 * First element is the most leafy enclosed element.
 */
public class GroupModel extends ArrayList<TypeElement> {

	private PackageElement pkg;

	GroupModel() {
		super();
	}

	GroupModel(TypeElement te) {
		super();
		add(te);
		Element current = te.getEnclosingElement();
		while (current != null) {
			if (current instanceof TypeElement) {
				add((TypeElement) current);
				current = current.getEnclosingElement();
			} else if (current instanceof PackageElement) {
				add((PackageElement) current);
				break;
			} else {
				AndHowCompileProcessor.error("Unexpected nested type '" + current + "' for " + te.getQualifiedName());
			}
		}
	}

	public boolean add(PackageElement e) {
		pkg = e;
		return true;
	}

	public PackageElement getPackage() {
		return pkg;
	}

	public TypeElement getLeafElement() {
		return get(0);
	}

	public ProxyNameModel buildNameModel() {
		ProxyNameModel pnm = new ProxyNameModel(pkg.getQualifiedName().toString());
		for (TypeElement te : this) {
			pnm.add(te.getSimpleName().toString());
		}
		return pnm;
	}

}
