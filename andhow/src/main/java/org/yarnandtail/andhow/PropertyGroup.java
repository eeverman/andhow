package org.yarnandtail.andhow;

import org.yarnandtail.andhow.api.BasePropertyGroup;

/**
 * A logical and/or functional grouping of Properties.
 * 
 * This interface is used to designate groups of custom configuration properties.
 * Individual Properties are declared as constants in your custom interface extending
 * this interface, as shown in the example below, which also uses the GroupInfo
 * annotation to provide a name and description of the group.
 * 
 * Note that fields of interfaces are implicitly <code>public static final</code>,
 * so there is no need to add those keywords.
 *
 * <pre>
 * {@code
 * @GroupInfo(name="Example 'car' configuration group", desc="All the properties to configure a car")
 * public interface MyCarProps extends PropertyGroup {
 *		StrProp CAR_NAME = StrProp.builder().mustStartWith("Z").required().build();
 *		IntProp TOP_SPEED = IntProp.builder().defaultValue(150).build();
 * }
 * }
 * </pre>
 * 
 * 
 * @author eeverman
 */
@GlobalPropertyGroup
public interface PropertyGroup extends BasePropertyGroup {

}
