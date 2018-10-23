package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author ericevermanpersonal
 */
public abstract class CompileProblem {
	
	String groupName;
	
	String propName;
	
	public String getGroupName() {
		return groupName;
	}
	
	public String getPropertyName() {
		return propName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && this.getClass().isInstance(o)) {
			CompileProblem cp = (CompileProblem)o;
			
			return 
					this.groupName.equals(cp.groupName) &&
					this.propName.equals(cp.propName);
		} else {
			return false;
		}
	}
	
	public abstract String getFullMessage();
	
	static class PropMissingStatic extends CompileProblem {
		
		public PropMissingStatic(String groupName, String propName) {
			this.groupName = groupName;
			this.propName = propName;
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
			this.groupName = groupName;
			this.propName = propName;
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
			this.groupName = groupName;
			this.propName = propName;
		}
		
		@Override
		public String getFullMessage() {
			return TextUtil.format(
					"The AndHow Property '{}' in the class '{}' must be declared as final.",
					groupName, propName);
		}
	}
	
}
