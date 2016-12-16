package yarnandtail.andhow;

/**
 * A problem bootstrapping the AndHow, prior to attempting to load any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class LoaderProblem extends Problem {
	
	/** The Property that actually has the problem */
	protected ValueCoord badValueCoord;
	
	/**
	 * The Property that has the problem.
	 * 
	 * @return May return null if not applicable.
	 */
	public PropertyCoord getBadValueCoord() {
		return badValueCoord;
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
		//String msg;
		
		public IOLoaderProblem(
				Loader loader, Class<? extends PropertyGroup> group, Property prop, 
				Exception exception) {
			badValueCoord = new ValueCoord(loader, group, prop);
			this.exception = exception;
			//this.msg = msg;
		}
		
		@Override
		public String getProblemDescription() {
			return exception.getMessage();
		}
	}
	
	public static class ParsingLoaderProblem extends LoaderProblem {

		Exception exception;
		
		public ParsingLoaderProblem(
				Loader loader, Class<? extends PropertyGroup> group, Property prop, 
				Exception exception) {
			badValueCoord = new ValueCoord(loader, group, prop);
			this.exception = exception;
		}
		
		@Override
		public String getProblemDescription() {
			return exception.getMessage();
		}
	}
	
	public static class SourceNotFoundLoaderProblem extends LoaderProblem {

		String message;
		
		public SourceNotFoundLoaderProblem(Loader loader, String message) {
			badValueCoord = new ValueCoord(loader, null, null);
			this.message = message;
		}
		
		@Override
		public String getProblemDescription() {
			return "Expected for find data for this loader to load from: " + message;
		}
	}
}
