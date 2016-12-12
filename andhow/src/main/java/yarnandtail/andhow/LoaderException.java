package yarnandtail.andhow;

/**
 * A loader exception knows about its Loading context.
 * More specific exceptions like ParsingExceptions might know about the actual
 * error, but may not know the Loader context.
 * @author eeverman
 */
public class LoaderException extends Exception {
	
	Loader loader;
	Property cp;
	String sourceDescription;
			
	public LoaderException(Exception base, Loader loader, Property cp,
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
