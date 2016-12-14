/*
 */
package yarnandtail.andhow;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ericeverman
 */
public abstract class Problem {
	
	protected PropertyDef refPropertyDef;
	protected PropertyDef badPropertyDef;

	/**
	 * For Properties that have some type of duplication w/ other points, this is the
	 * Property that is duplicated (the earlier of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PropertyDef getRefProperty() {
		return refPropertyDef;
	}

	/**
	 * For Properties that have some type of duplication w/ other points, this is the
	 * property that is the duplicate one (the later of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PropertyDef getBadProperty() {
		return badPropertyDef;
	}

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
	 */
	public static class PropertyValueDef extends PropertyDef {
		Loader loader;
		
		public PropertyValueDef(Loader loader, Property<?> prop, Class<? extends PropertyGroup> group) {
			super(prop, group);
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

		public PropertyDef(Property<?> prop, Class<? extends PropertyGroup> group) {
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
