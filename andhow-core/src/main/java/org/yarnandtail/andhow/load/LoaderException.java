package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.Loader;

/**
 * An internal exception that a Loader can throw to be caught internally and converted to a LoaderProblem.
 * 
 * @author eeverman
 */
class LoaderException extends Exception {
	
	/** The loader. */
	Loader loader;
	
	/** The source description. */
	String sourceDescription;
			
	/**
	 * Instantiates a new loader exception.
	 *
	 * @param base the base
	 * @param loader the loader
	 * @param sourceDescription the source description
	 */
	public LoaderException(Exception base, Loader loader, String sourceDescription) {
		super(base);
		this.loader = loader;
		this.sourceDescription = sourceDescription;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getCause()
	 */
	@Override
	public synchronized Exception getCause() {
		return (Exception)super.getCause();
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		if (sourceDescription != null) {
			return "Error reading source: " + sourceDescription + "  Base error message: " + super.getMessage();
		} else {
			return super.getMessage();
		}
	}
	
	
}
