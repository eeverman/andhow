package net.spacebase;

/**
 * This is an example of an old legacy service that relies on system properties
 * for its configuration.
 * 
 * Likely its hard to get a complete list of its configuration properties or find
 * complete documentation for them.  On top of that, they are all passed
 * and read as Strings, so they fragile and difficult to validate.
 * 
 * See how this is handled by AndHow in the SatelliteServiceConfig interface.
 * 
 * @author ericeverman
 */
public class ReallyOldSatelliteService {
	
	public String getQueryUrl() {
		return System.getProperty("sat.svs") + System.getProperty("sat.query");
	}
	
	public String getItemUrl() {
		return System.getProperty("sat.svs") + System.getProperty("sat.item");
	}
	
	public int getTimeout() {
		//-- Lots of legacy configuration code contains methods like this:
		//unvalidated and 'late' type conversions.
		//This may have configured as 'five', which won't be discovered until
		//this method is called, potentially blosing up your application.
		//--/--/
		//Instead, see how this problem is neatly solved in this example using
		//configuration properties in SatelliteServiceConfig.
		return Integer.parseInt(System.getProperty("sat.to"));
	}

}
