package org.yarnandtail.andhow;

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
 * This interface is used by the user, thus it is in the root package.  It is also
 * used by several interfaces in the api package and there isn't an easy way around
 * that.
 * 
 * @author eeverman
 */
public interface PropertyGroup {
}
