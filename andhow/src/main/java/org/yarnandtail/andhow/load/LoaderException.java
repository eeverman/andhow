package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.Loader;

/**
 * An internal exception that a Loader can throw to be caught internally and converted to a LoaderProblem.
 * 
 * @author eeverman
 */
class LoaderException extends Exception {
	
	Loader loader;
	String sourceDescription;
			
	public LoaderException(Exception base, Loader loader, String sourceDescription) {
		super(base);
		this.loader = loader;
		this.sourceDescription = sourceDescription;
	}
	
	@Override
	public synchronized Exception getCause() {
		return (Exception)super.getCause();
	}

	@Override
	public String getMessage() {
		if (sourceDescription != null) {
			return "Error reading source: " + sourceDescription + "  Base error message: " + super.getMessage();
		} else {
			return super.getMessage();
		}
	}
	
	
}
