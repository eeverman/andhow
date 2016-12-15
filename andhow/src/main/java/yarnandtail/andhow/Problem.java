package yarnandtail.andhow;

/**
 *
 * @author ericeverman
 */
public abstract class Problem {

	/**
	 * A detailed description of the problem.
	 *
	 * @return
	 */
	public abstract String getMessage();
	

	/**
	 * Full definition of where a value for a property would be loaded from, 
	 * which is a PropertyDef plus a Loader.
	 * 
	 * The actual value itself is not included b/c its type varies depending on
	 * the context of the problem and it is at least useful to unify where the
	 * value came from, if not the value itself.
	 * 
	 * TODO:  Remame to LoadedPropertyCoord??
	 */
	public static class PropertyValueDef extends PropertyDef {
		Loader loader;
		
		public PropertyValueDef(Loader loader, Class<? extends PropertyGroup> group, Property<?> prop) {
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
	 * Full definition of a Property, including its group.
	 * 
	 */
	public static class PropertyDef {
		Property<?> property;
		Class<? extends PropertyGroup> group;
		String name;

		public PropertyDef(Class<? extends PropertyGroup> group, Property<?> prop) {
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
