package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.PropertyGroup;

/**
 * Logical location of a Property (Group and Property).
 *
 */
public class PropertyCoord {

	Property<?> property;
	Class<? extends PropertyGroup> group;
	String name;

	public PropertyCoord(Class<? extends PropertyGroup> group, Property<?> prop) {
		this.property = prop;
		this.group = group;
		if (group != null && property != null) {
			try {
				name = AndHowUtil.getCanonicalName(group, property);
			} catch (Exception ex) {
				name = "[[Security exception while trying to determine the property canonical name]]";
			}
			if (name == null) {
				name = "[[Unable to determine the property canonical name]]";
			}
		} else {
			name = Problem.UNKNOWN;
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
	 * The canonical name of the group, or some form of [[Unknown]] if it is
	 * null.
	 *
	 * @return
	 */
	public String getGroupName() {
		if (group != null) {
			return group.getCanonicalName();
		} else {
			return Problem.UNKNOWN;
		}
	}

	/**
	 * The canonical name of the Property, or one of a series of placeholder
	 * names if the value can't be determined.
	 *
	 * Placeholders are double square bracketed like this:  [[Unknown]]
	 * @return Never null
	 */
	public String getPropName() {
		return name;
	}
	
}
