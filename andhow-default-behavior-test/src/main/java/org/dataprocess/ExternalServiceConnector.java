package org.dataprocess;

import org.yarnandtail.andhow.api.Exporter;
import java.time.LocalDateTime;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.PropertyGroup;

/**
 * This is an example minimal application configuration.
 * IT DOES NOT WORK ON INITIAL STARTUP - READ ON...
 * 
 * This example will blowup when this class is first run.  In the console out,
 * there will be a complete example properties file.  Paste this output
 * (beginning and ending with ###### lines) into a properties file named
 * 'andhow.properties' in the project resources.
 * 
 * Fill in some sample properties run again - note the problem reporting in the
 * console when requirements and validations are violated.
 * 
 * As an example of a fully functioning properties file, there is an example one
 * named 'renamed_me_to_andhow.properties'
 * 
 * @author eeverman
 */
public class ExternalServiceConnector {
	

	
	public String fetchData() {
		return "Got some data...";
	}
	
	public String getConnectionUrl() {
		return ConnectionConfig.SERVICE_URL.getValue();
	}
	
	public int getConnectionTimeout() {
		return ConnectionConfig.TIMEOUT.getValue();
	}
	
	
	//
	// Here are two configuration groups that are added to AndHow, above.
	// Normally these would be in separate files, or even better, in files
	// within the modules they configure.  

	
	@GroupInfo(name="Connection configuration to some external service", desc="Configures communication to the USGS Aquarius service")
	@GlobalPropertyGroup
	public interface ConnectionConfig {
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").mustBeNonNull().build();
		IntProp TIMEOUT = IntProp.builder().defaultValue(50).desc("Timeout in seconds") .build();
	}
	
}
