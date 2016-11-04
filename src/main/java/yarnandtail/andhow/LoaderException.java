package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class LoaderException extends Exception {
	
	Loader loader;
	ConfigPointUsage cpu;
	String sourceDescription;
			
	public LoaderException(ParsingException base, Loader loader, ConfigPointUsage cpu,
			String sourceDescription) {
		super(base);
		this.loader = loader;
		this.cpu = cpu;
		this.sourceDescription = sourceDescription;
	}
	
	@Override
	public synchronized ParsingException getCause() {
		return (ParsingException) super.getCause();
	}
}
