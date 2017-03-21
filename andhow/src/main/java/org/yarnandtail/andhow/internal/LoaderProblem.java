package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;
import org.yarnandtail.andhow.api.BasePropertyGroup;

/**
 * A problem bootstrapping the AndHow, prior to attempting to load any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class LoaderProblem implements Problem {
	
	/** The Property that actually has the problem */
	protected LoaderValueCoord badValueCoord;
	
	/**
	 * The Property that has the problem.
	 * 
	 * @return May return null if not applicable.
	 */
	public PropertyCoord getBadValueCoord() {
		return badValueCoord;
	}
	
		
	@Override
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}
	
	@Override
	public String getProblemContext() {
		
		String loadDesc = null;
		String loadName = null;
		String propName = null;
		
		if (badValueCoord != null) {
			
			if (badValueCoord.getGroup() != null && badValueCoord.getProperty() != null) {
				propName = badValueCoord.getPropName();
			}
			
			if (badValueCoord.getLoader() != null) {
				
				loadName = badValueCoord.getLoader().getClass().getCanonicalName();
				
				if (badValueCoord.getLoader().getSpecificLoadDescription() != null) {
					loadDesc = badValueCoord.getLoader().getSpecificLoadDescription();
				}
			}

			if (loadDesc != null) {
				if (propName != null) {
					return TextUtil.format("Reading property {} from {}", propName, loadDesc);	
				} else {
					return TextUtil.format("Reading from {}", loadDesc);	
				}
			} else if (loadName != null) {
				if (propName != null) {
					return TextUtil.format("Reading property {} via loader {}", propName, loadName);	
				} else {
					return TextUtil.format("Reading via loader {}", loadDesc);	
				}
			} else if (propName != null) {
				return TextUtil.format("Reading property {}", loadDesc);
			} else {
				return UNKNOWN + " context";
			}
		} else {
			return UNKNOWN + " context";
		}
	}
	

	public static class IOLoaderProblem extends LoaderProblem {

		Exception exception;
		String resourcePath;
		
		public IOLoaderProblem(Loader loader, Exception exception, String resourcePath) {
			badValueCoord = new LoaderValueCoord(loader, null, null);
			this.exception = exception;
		}
		
		@Override
		public String getProblemDescription() {
			return "There was an IO error while reading from: " + resourcePath + " Original error message: " + exception.getMessage();
		}
	}
	
	public static class ParsingLoaderProblem extends LoaderProblem {

		Exception exception;
		
		public ParsingLoaderProblem(
				Loader loader, Class<? extends BasePropertyGroup> group, Property prop, 
				Exception exception) {
			badValueCoord = new LoaderValueCoord(loader, group, prop);
			this.exception = exception;
		}
		
		@Override
		public String getProblemDescription() {
			return exception.getMessage();
		}
	}
	
	public static class DuplicatePropertyLoaderProblem extends LoaderProblem {
		
		public DuplicatePropertyLoaderProblem(
				Loader loader, Class<? extends BasePropertyGroup> group, Property prop) {
			badValueCoord = new LoaderValueCoord(loader, group, prop);
		}
		
		@Override
		public String getProblemDescription() {
			return "There are multiple values assigned to this property";
		}
	}
	
	public static class UnknownPropertyLoaderProblem extends LoaderProblem {
		
		private String unknownPropName;
		
		public UnknownPropertyLoaderProblem(
				Loader loader, String unknownPropName) {
			badValueCoord = new LoaderValueCoord(loader, null, null);
		}

		public String getUnknownPropertyName() {
			return unknownPropName;
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format("The property '{}' is not recognized", unknownPropName);
		}
	}
	
	public static class SourceNotFoundLoaderProblem extends LoaderProblem {

		String message;
		
		public SourceNotFoundLoaderProblem(Loader loader, String message) {
			badValueCoord = new LoaderValueCoord(loader, null, null);
			this.message = message;
		}
		
		@Override
		public String getProblemDescription() {
			return "Expected for find data for this loader to load from: " + message;
		}
	}
	
	public static class JndiContextLoaderProblem extends LoaderProblem {

		public JndiContextLoaderProblem(Loader loader) {
			badValueCoord = new LoaderValueCoord(loader, null, null);
		}
		
		@Override
		public String getProblemDescription() {
			return "Constructing the JNDI Context for the JndiLoader threw an Exception.  " +
					"If there is no JNDI Context availabler for this entry point to the application, " +
					"remove the JndiLoader from the list of Loaders.";
		}
	}
	
	public static class ObjectConversionValueProblem extends LoaderProblem {
		Object obj;
		
		public ObjectConversionValueProblem(
				Loader loader, Class<? extends BasePropertyGroup> group, Property prop, 
				Object obj) {
			badValueCoord = new LoaderValueCoord(loader, group, prop);
			this.obj = obj;
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format("The object '{}' could not be converted to type {}",
					(obj!=null)?obj:TextUtil.NULL_PRINT,
					getBadValueCoord().getProperty().getValueType().getDestinationType().getSimpleName());
		}
	}
	

	public static class StringConversionLoaderProblem extends LoaderProblem {
		String str;
		
		public StringConversionLoaderProblem(
				Loader loader, Class<? extends BasePropertyGroup> group, Property prop, 
				String str) {
			
			badValueCoord = new LoaderValueCoord(loader, group, prop);
			this.str = str;
		}

		@Override
		public String getProblemDescription() {
			return TextUtil.format("The string '{}' could not be converted to type {}",
					(str!=null)?str:TextUtil.NULL_PRINT, 
					getBadValueCoord().getProperty().getValueType().getDestinationType().getSimpleName());
		}
	}
	
}
