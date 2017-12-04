package net.spacebase;

import java.time.LocalDateTime;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.*;

/**
 * A ficticious space themed application.
 * 
 * @author eeverman
 */
public class SpaceBaseApplication {
	
	//To similate how a larger application would run, this is a singleton.
	//In a web application, this might be a main 'run on startup' servlet,
	//In a desktop app, this would probably something similar to this.
	public static SpaceBaseApplication singleton;
	
	public SpaceBaseApplication() {

	}
	
	/**
	 * A central entry point.
	 * @param args 
	 */
	public static void main(String[] args) {
		
		//findConfig() will find the active AndHowConfiguration that would be used.
		//If an AndHowTestInit class is on the classpath, it will be used to
		//provide that configuration, which would be the case during testing.
		AndHow.findConfig().setCmdLineArgs(args).build();

		System.out.println("Spacebase App is started!");
		singleton = new SpaceBaseApplication();
	}
	
	public static SpaceBaseApplication instance() {
		return singleton;
	}
	
	
	public String getAppName() {
		return AppInfo.APP_NAME.getValue();
	}
	
	public String getAppUrl() {
		return AppInfo.APP_PUBLIC_URL.getValue();
	}
	
	public LocalDateTime getInceptionDate() {
		return AppInfo.INCEPTION_DATE.getValue();
	}
	
	
	//
	// Here are two configuration groups that are added to AndHow, above.
	// Normally these would be in separate files, or even better, in files
	// within the modules they configure.  
	
	@GroupInfo(name="Application Information", desc="Basic app info for display in the UI")
	public interface AppInfo {
		StrProp APP_NAME = StrProp.builder().mustBeNonNull().defaultValue("Space Base").aliasIn("APP_NAME").build();
		StrProp APP_PUBLIC_URL = StrProp.builder().mustBeNonNull().mustStartWith("http://").defaultValue("http://spacebase.net").build();
		LocalDateTimeProp INCEPTION_DATE = 
				LocalDateTimeProp.builder().mustBeNonNull().defaultValue(LocalDateTime.parse("2017-10-01T00:00")).build();
	}
	
}
