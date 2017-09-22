package org.yarnandtail.andhow.compile;

/**
 * Incrementally determines is a Property variable points to a new Property instance.
 * 
 * Used by the AndHowTreeScanner to incrementally discover if a variable of
 * a type that is an AndHow Property, points to a new property or is just a
 * reference to a property constructed somewhere else.  The name of the variable
 * at the time of Property creation becomes part of the canonical name of the
 * Property.
 * 
 * @author ericeverman
 */
public class PropertyMarker {

	private boolean builder = false;
	private boolean build = false;
	private boolean direct = false;
	
	/**
	 * Marks a Property variable as being assigned to a Property class that
	 * has invoked the .builder() method.
	 */
	public void markBuilder() {
		builder = true;
	}
	
	/**
	 * Marks a Property variable as being assigned to a Property class that
	 * has invoked the .build() method.
	 */
	public void markBuild() {
		build = true;
	}
	
	/**
	 * Marks a Property variable as being assigned to a Property that was
	 * directly constructed with a call to new XXXProperty();
	 */
	public void markDirectConstruct() {
		direct = true;
	}

	/**
	 * If true, this Property variable points to a newly constructed property,
	 * not just a reference to an existing property.
	 * @return 
	 */
	public boolean isNewProperty() {
		return builder && build || direct;
	}

}
