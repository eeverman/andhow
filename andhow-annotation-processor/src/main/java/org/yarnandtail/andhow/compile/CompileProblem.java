package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author ericevermanpersonal
 */
public abstract class CompileProblem {
	
	final String groupName;
	final String propName;
	final boolean propProblem;
	
	CompileProblem(String groupName, String propName) {
		this.groupName = groupName;
		this.propName = propName;
		propProblem = true;
	}
		
	CompileProblem() {
		groupName = null;
		propName = null;
		propProblem = false;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public String getPropertyName() {
		return propName;
	}
	
	public boolean isPropertyProblem() {
		return propProblem;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && this.getClass().isInstance(o)) {
			CompileProblem cp = (CompileProblem)o;
			
			return 
					this.groupName.equals(cp.groupName) &&
					this.propName.equals(cp.propName) &&
					this.propProblem == cp.propProblem;
		} else {
			return false;
		}
	}
	
	public abstract String getFullMessage();
	
	static class PropMissingStatic extends CompileProblem {
		
		public PropMissingStatic(String groupName, String propName) {
			super(groupName, propName);
		}
		
		@Override
		public String getFullMessage() {
			return TextUtil.format(
					"The AndHow Property '{}' in the class '{}' must be declared as static.",
					groupName, propName);
		}
	}
	
	static class PropMissingFinal extends CompileProblem {
		
		public PropMissingFinal(String groupName, String propName) {
			super(groupName, propName);
		}
		
		@Override
		public String getFullMessage() {
			return TextUtil.format(
					"The AndHow Property '{}' in the class '{}' must be declared as final.",
					groupName, propName);
		}
	}
	
	static class PropMissingStaticFinal extends CompileProblem {
		
		public PropMissingStaticFinal(String groupName, String propName) {
			super(groupName, propName);
		}
		
		@Override
		public String getFullMessage() {
			return TextUtil.format(
					"The AndHow Property '{}' in the class '{}' must be declared as final.",
					groupName, propName);
		}
	}
	
	static class TooManyInitClasses extends CompileProblem {
		final List<AndHowCompileProcessor.CauseEffect> _instances = new ArrayList();
		final String _fullInitClassName;
		
		public TooManyInitClasses(
				String fullInitClassName,
				List<AndHowCompileProcessor.CauseEffect> instances) {
			
			if (instances != null) this._instances.addAll(instances);
			this._fullInitClassName = fullInitClassName;
			
		}
		
		public List<String> getInstanceNames() {
			List<String> names = new ArrayList();

			for (AndHowCompileProcessor.CauseEffect ce : _instances) {
				names.add(ce.fullClassName);
			}

			return names;
		}

		@Override
		public String getFullMessage() {
			String impList = _instances.stream()
					.map(i -> i.fullClassName).collect(Collectors.joining( ", " ));

			return TextUtil.format("Multiple ({}) implementations of {} were found, "
					+ "but only one is allowed.  Implementations found: {}",
					String.valueOf(_instances.size()),
					_fullInitClassName, impList);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o != null && this.getClass().isInstance(o)) {
				TooManyInitClasses cp = (TooManyInitClasses)o;

				return 
						this._instances.containsAll(cp._instances) &&
						cp._instances.containsAll(this._instances) &&
						this._fullInitClassName.equals(cp._fullInitClassName);
			} else {
				return false;
			}
		}
	}
	
}
