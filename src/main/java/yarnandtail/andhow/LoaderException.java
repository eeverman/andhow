package yarnandtail.andhow;

import yarnandtail.andhow.Loader;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.load.ParsingException;

/**
 * A loader exception knows about its Loading context.
 * More specific exceptions like ParsingExceptions might know about the actual
 * error, but may not know the Loader context.
 * @author eeverman
 */
public class LoaderException extends Exception {
	
	Loader loader;
	ConfigPoint cp;
	String sourceDescription;
			
	public LoaderException(Exception base, Loader loader, ConfigPoint cp,
			String sourceDescription) {
		super(base);
		this.loader = loader;
		this.cp = cp;
		this.sourceDescription = sourceDescription;
	}
	
	@Override
	public synchronized Exception getCause() {
		return (Exception)super.getCause();
	}
}
