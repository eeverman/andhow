package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Implementation represent specific problems with AndHow Property usage or
 * initiation, discovered during compilation.
 */
public abstract class CompileProblem {
	
	final String groupName;
	final String propName;
	final boolean propProblem;
	
	/**
	 * Base constructor for Property related problems.
	 * 
	 * @param groupName Full name of the class containing the {@code Property}.
	 * If an inner class, should be the complete name w/ the inner class.
	 * @param propName The declared name of the {@code Property} w/ the problem.
	 */
	CompileProblem(String groupName, String propName) {
		this.groupName = groupName;
		this.propName = propName;
		propProblem = true;
	}
		
	/**
	 * Base constructor for non-Property related problems, such as Initiation
	 * errors.
	 */
	CompileProblem() {
		groupName = null;
		propName = null;
		propProblem = false;
	}
	
	/**
	 * Full name of the class containing the {@code Property}.
	 * In the case of an inner class, this should be the complete name including
	 * that inner class.
	 * 
	 * @return May be null if this is not a property related problem.
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/**
	 * The declared name of the {@code Property} that has the problem.
	 * 
	 * @return May be null if this is not a property related problem.
	 */
	public String getPropertyName() {
		return propName;
	}
	
	/**
	 * If true, the problem is related to a specific {@code Property}.
	 * @return 
	 */
	public boolean isPropertyProblem() {
		return propProblem;
	}
	
	/**
	 * Logical equals implementation based on equality of fields.
	 * 
	 * This is only used for comparisons during testing.
	 * 
	 * @param o Compared to me.
	 * @return True if logically equal.
	 */
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
	
	/**
	 * The complete message that will be logged during compilation.
	 * 
	 * Intended to be user readable w/ enough info for the user to understand
	 * and find the issue.
	 * 
	 * @return A String explanation of the problem.
	 */
	public abstract String getFullMessage();
	
	/**
	 * An AndHow Property is missing the {@code static} modifier.
	 */
	static class PropMissingStatic extends CompileProblem {
		
		/**
		 * New instance.
		 * 
		 * @param groupName Full name of the class containing the {@code Property}.
		 * If an inner class, should be the complete name w/ the inner class.
		 * @param propName The declared name of the {@code Property} w/ the problem.
		 */
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
	
	/**
	 * An AndHow Property is missing the {@code final} modifier.
	 */
	static class PropMissingFinal extends CompileProblem {
		
		/**
		 * New instance.
		 * 
		 * @param groupName Full name of the class containing the {@code Property}.
		 * If an inner class, should be the complete name w/ the inner class.
		 * @param propName The declared name of the {@code Property} w/ the problem.
		 */
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
	
	/**
	 * An AndHow Property is missing the {@code static} and {@code final} modifiers.
	 */
	static class PropMissingStaticFinal extends CompileProblem {
		
		/**
		 * New instance.
		 * 
		 * @param groupName Full name of the class containing the {@code Property}.
		 * If an inner class, should be the complete name w/ the inner class.
		 * @param propName The declared name of the {@code Property} w/ the problem.
		 */
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
	
	/**
	 * More than a single Init class was found on the classpath.
	 */
	static class TooManyInitClasses extends CompileProblem {
		private final List<AndHowCompileProcessor.CauseEffect> _instances = new ArrayList();
		private final String _fullInitClassName;
		
		/**
		 * New instance.
		 * 
		 * @param fullInitClassName The Init interface name
		 * @param instances  A list of instances that implement the interface.
		 */
		public TooManyInitClasses(
				String fullInitClassName,
				List<AndHowCompileProcessor.CauseEffect> instances) {
			
			if (instances != null) this._instances.addAll(instances);
			this._fullInitClassName = fullInitClassName;
			
		}
		
		/**
		 * A list of full class names that implement the InitClassName interface.
		 * 
		 * @return A List of class names.  Never null.
		 */
		public List<String> getInstanceNames() {
			List<String> names = new ArrayList();

			for (AndHowCompileProcessor.CauseEffect ce : _instances) {
				names.add(ce.fullClassName);
			}

			return names;
		}
		
		/**
		 * The Init interface name.
		 * 
		 * @return A class name.
		 */
		public String getInitClassName() {
			return _fullInitClassName;
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
		
		/**
		 * Logical equals implementation based on equality of fields.
		 * 
		 * This is only used for comparisons during testing.
		 * 
		 * @param o Compared to me.
		 * @return True if logically equal.
		 */
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
