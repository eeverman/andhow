package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.Property;

/**
 * Simple way to pass the canonical name and associated property around
 */
public class NameAndProperty {

	public String fieldName;
	public Property<?> property;

	public NameAndProperty(String fieldName, Property<?> prop) {
		this.fieldName = fieldName;
		this.property = prop;
	}
	
}
