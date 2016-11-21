package yarnandtail.andhow;

import java.util.List;

/**
 * Master Exception that can be thrown by the AppConfig to Blowup and stop the
 * application start.
 * 
 * This exception will contain lists of all exceptions that may have contributed
 * to the failure to proceed w/ the configuration.
 * 
 * In general, the AppConfig will try to proceed as far as it can so that it can
 * report all issues at once, rather than piece-by-piece as they are fixed.
 * 
 * @author eeverman
 */
public class ConfigurationException extends RuntimeException {

	private final List<NamingException> namingExceptions;
	private final List<LoaderException> loadingExceptions;
	private final List<ValidationException> validationExceptions;
	
	
	public ConfigurationException(String message) {
		super(message);
		namingExceptions = null;
		loadingExceptions = null;
		validationExceptions = null;
	}

	public ConfigurationException(String message, List<NamingException> namingExceptions,
			List<LoaderException> loadingExceptions, List<ValidationException> validationExceptions) {
		super(message);
		
		this.namingExceptions = namingExceptions;
		this.loadingExceptions = loadingExceptions;
		this.validationExceptions = validationExceptions;
	}
	
	public ConfigurationException(List<NamingException> namingExceptions,
			List<LoaderException> loadingExceptions, List<ValidationException> validationExceptions) {
		super();
		
		this.namingExceptions = namingExceptions;
		this.loadingExceptions = loadingExceptions;
		this.validationExceptions = validationExceptions;
	}

	public List<NamingException> getNamingExceptions() {
		return namingExceptions;
	}

	public List<LoaderException> getLoadingExceptions() {
		return loadingExceptions;
	}

	public List<ValidationException> getValidationExceptions() {
		return validationExceptions;
	}
	
}
