package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.Problem;

/**
 * Base class for all Property related Problems.
 */
public abstract class PropertyProblem implements Problem {

	/**
	 * The coordinate of the Property that has the problem
	 */
	protected PropertyCoord propertyCoord;

	/**
	 * The coordinate of the property that has the problem
	 *
	 * @return A PropertyCoord instance
	 */
	public PropertyCoord getPropertyCoord() {
		return propertyCoord;
	}

	@Override
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}
}
