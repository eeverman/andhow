package yarnandtail.andhow.staticparam.load;

import yarnandtail.andhow.staticparam.ConfigPoint;
import yarnandtail.andhow.staticparam.ParsingException;

/**
 *
 * @author eeverman
 */
public class LoaderException extends Exception {
	
	Loader loader;
	ConfigPoint cp;
	String sourceDescription;
			
	public LoaderException(ParsingException base, Loader loader, ConfigPoint cp,
			String sourceDescription) {
		super(base);
		this.loader = loader;
		this.cp = cp;
		this.sourceDescription = sourceDescription;
	}
	
	@Override
	public synchronized ParsingException getCause() {
		return (ParsingException) super.getCause();
	}
}
