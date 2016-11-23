package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class FatalLoaderException extends LoaderException {
	public FatalLoaderException(Exception base, Loader loader, ConfigPoint cp,
			String sourceDescription) {
		super(base, loader, cp, sourceDescription);

	}
}
