package yarnandtail.andhow;

/**
 *
 * @author ericeverman
 */
public abstract class Problem {

	/**
	 * A complete description of the problem w/ context and problem description.
	 * 
	 * Constructed by stringing the problem context and the problem description
	 * together:  [Context]: [Description]
	 *
	 * @return
	 */
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}
	
	/**
	 * The context for the problem, for the user.
	 * 
	 * For a construction problem for a property, this would be the Property
	 * canonical name.  For a invalid value, it might be the Property name
	 * and where it was loaded from.
	 * 
	 * 
	 * @return 
	 */
	public abstract String getProblemContext();
	
	/**
	 * The problem description, for the user.
	 * 
	 * It should be able to be tacked on to the 
	 * @return 
	 */
	public abstract String getProblemDescription();
	

	/**
	 * Location of a value, as loaded by a specific loader for a specific Property.
	 * 
	 * The actual value itself is not included b/c its type varies depending on
	 * the context of the problem and it is at least useful to unify where the
	 * value came from, if not the value itself.
	 * 
	 */
	public static class ValueCoord extends PropertyCoord {
		Loader loader;
		
		public ValueCoord(Loader loader, Class<? extends PropertyGroup> group, Property<?> prop) {
			super(group, prop);
			this.loader = loader;
		}
		
		/**
		 * The loader attempting to load this property, if that can be determined.
		 * @return May return null.
		 */
		public Loader getLoader() {
			return loader;
		}
		
	}
	
	/**
	 * Logical location of a Property (Group and Property).
	 * 
	 */
	public static class PropertyCoord {
		Property<?> property;
		Class<? extends PropertyGroup> group;
		String name;

		public PropertyCoord(Class<? extends PropertyGroup> group, Property<?> prop) {
			this.property = prop;
			this.group = group;
			
			if (group != null && property != null) {
				try {
					name = PropertyGroup.getCanonicalName(group, property);
				} catch (Exception ex) {
					name = "[[Security exception while trying to determine the property canonical name]]";
				}
				
				if (name == null) {
					name = "[[Unable to determine the property canonical name]]";
				}
			} else {
				name = "[[Unknown]]";
			}
			
		}

		/**
		 * The property, if that can be determined.
		 * @return May return null.
		 */
		public Property<?> getProperty() {
			return property;
		}

		/**
		 * The group containing the Property, if that can be determined.
		 * @return May return null.
		 */
		public Class<? extends PropertyGroup> getGroup() {
			return group;
		}

		/**
		 * The canonical name of the Property, or one of a series of placeholder
		 * names if the value can't be determined.
		 * 
		 * Placeholders are double square bracketed like this:  [[Unknown]]
		 * @return Never null
		 */
		public String getName() {
			return name;
		}
		
	}
	
}
