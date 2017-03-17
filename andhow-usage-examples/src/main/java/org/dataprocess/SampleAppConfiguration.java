package org.dataprocess;

import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.api.Exporter;
import java.time.LocalDateTime;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.export.SysPropExporter;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.property.*;

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
public class SampleAppConfiguration {
	
	public static void main(String[] args) {
		AndHow.builder()
				.loader(new SystemPropertyLoader())	//Look for props in System.properties
				.loader(new JndiLoader())		//Look in container provided JNDI context
				.loader(new PropertyFileOnClasspathLoader(AppInfo.CLASSPATH_PROP_FILE))	//And in a prop file on classpath
				.loader(new PropertyFileOnFilesystemLoader(AppInfo.FILESYSTEM_PROP_FILE))	//And in the file system
				.group(AquariusConfig.class)
				.group(NwisConfig.class)
				.group(AppInfo.class)
				.build();
		
	
		//Fetching some properties from the AquariusConfig.
		//Note that the return values are strongly typed
		String queryUrl = AquariusConfig.SERVICE_URL.getValue() + AquariusConfig.QUERY_ENDPOINT.getValue();
		String itemUrl = AquariusConfig.SERVICE_URL.getValue() + AquariusConfig.ITEM_ENDPOINT.getValue();
		Integer timeout = AquariusConfig.TIMEOUT.getValue();
		
		System.out.println("The query url is: " + queryUrl);
		System.out.println("The query url is: " + itemUrl);
		System.out.println("Timeout is : " + timeout);
		
	}
	
	
	//
	// Here are two configuration groups that are added to AndHow, above.
	// Normally these would be in separate files, or even better, in files
	// within the modules they configure.  

	
	@GroupInfo(name="Aquarius Configuration", desc="Configures communication to the USGS Aquarius service")
	public interface AquariusConfig extends PropertyGroup {
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").build();
		IntProp TIMEOUT = IntProp.builder().defaultValue(50).build();
		StrProp QUERY_ENDPOINT = StrProp.builder().build();
		StrProp ITEM_ENDPOINT = StrProp.builder().required().build();
	}
	
	@GroupExport(
		exporter=SysPropExporter.class,
		exportByCanonicalName=Exporter.EXPORT_CANONICAL_NAME.NEVER,
		exportByOutAliases=Exporter.EXPORT_OUT_ALIASES.ALWAYS
	)
	@GroupInfo(name="NWIS Configuration", desc="Configures communication to the USGS NWIS service")
	public interface NwisConfig extends PropertyGroup {
		/*	This group demonstrates how to use aliases for compatibility with
			legacy code.  aliasInAndOut("legacy.name") adds an alternate
			name which will be recognized in all configuration sources - that is 
			the 'in' portion of aliasInAndOut.

			'out' means the alias can be used as a name when an exporting, 
			which is done with the GroupExport annotation, above.  All
			properties in this group will be written to System.properties
			using 'out' aliases.
		*/
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").aliasInAndOut("nwis.svs").build();
		IntProp TIMEOUT = IntProp.builder().defaultValue(20).aliasInAndOut("nwis.to").build();
		StrProp QUERY_ENDPOINT = StrProp.builder().aliasInAndOut("nwis.query").build();
		StrProp ITEM_ENDPOINT = StrProp.builder().required().aliasInAndOut("nwis.item").build();
	}
	
	@GroupInfo(name="Application Information", desc="Basic app info for display in the UI")
	public interface AppInfo extends PropertyGroup {
		StrProp APP_NAME = StrProp.builder().required().defaultValue("Super Nifty App").build();
		StrProp APP_PUBLIC_URL = StrProp.builder().required().mustStartWith("http://").defaultValue("http://supercool.org/niftyapp").build();
		LocalDateTimeProp INCEPTION_DATE = 
				LocalDateTimeProp.builder().required().defaultValue(LocalDateTime.parse("2017-01-01T00:00")).build();
		StrProp CLASSPATH_PROP_FILE = StrProp.builder().desc("Classpath location of a properties file for config.").build();
		StrProp FILESYSTEM_PROP_FILE = StrProp.builder().desc("Filesystem location of a properties file for config.").build();
	}
	
}
