package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.compile.AndHowCompileProcessor.CauseEffect;
import org.yarnandtail.andhow.util.AndHowLog;

/**
 *
 * @author ericeverman
 */
public class TooManyInitClassesException extends RuntimeException {
	protected List<CauseEffect> _instances = new ArrayList();
	protected String _fullInitClassName;
			
	public TooManyInitClassesException(
			String fullInitClassName,
			List<CauseEffect> instances) {
		if (instances != null) this._instances.addAll(instances);
		this._fullInitClassName = fullInitClassName;
	}
	
	public List<String> getInstanceNames() {
		List<String> names = new ArrayList();

		for (CauseEffect ce : _instances) {
			names.add(ce.fullClassName);
		}
		
		return names;
	}
	
	public void writeDetails(AndHowLog log) {
		log.error("Multiple ({0}) {1} implementation classes were found, but only "
				+ "one is allowed.  List follows:",
				Integer.valueOf(_instances.size()).toString(), _fullInitClassName);
		
		for (String name : getInstanceNames()) {
			log.error("\t* " + name);
		}
	}

	@Override
	public String getMessage() {
		return "Multiple " + _fullInitClassName + " implementations were found - "
				+ "only one is allowed. See System.err for complete list";
	}
	
	
}
