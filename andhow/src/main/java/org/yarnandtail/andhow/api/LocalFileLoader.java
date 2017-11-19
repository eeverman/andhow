package org.yarnandtail.andhow.api;

/**
 * A Loader that loads values from some type of file locally.
 * 
 * @author ericeverman
 */
public interface LocalFileLoader {
	
	/**
	 * Sets the path to the file to be loaded.
	 * 
	 * Setting this non-null is mutually exclusive with setting the Property<String>
	 * based method of the same name.
	 * 
	 * @param path 
	 */
	void setFilePath(String path);
	
	/**
	 * Sets the path to the file to be loaded.
	 * 
	 * Setting this non-null is mutually exclusive with setting the String
	 * based method of the same name.
	 * 
	 * @param path 
	 */
	void setFilePath(Property<String> path);
	
	/**
	 * If set true, a file path that does not point to a file is a Problem/error.
	 * 
	 * A null file path is not a problem, effectively turning off the loader.
	 * 
	 * @param isAProblem 
	 */
	void setMissingFileAProblem(boolean isAProblem);
	
	/**
	 * If true, a loader that loads from a file will report a Problem that will
	 * stop application startup if it cannot find the configured file.
	 * 
	 * @return 
	 */
	boolean isMissingFileAProblem();

}
