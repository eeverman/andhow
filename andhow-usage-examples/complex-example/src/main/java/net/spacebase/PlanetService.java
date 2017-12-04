package net.spacebase;

import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.property.*;

/**
 *
 * @author ericeverman
 */
@GroupInfo(name="Planet Service Configuration",
		desc="Configures communication to a remote service that returns stats and current locations of planets")
public class PlanetService {
	
	//private properties are OK - they just need to be static final
	private static final StrProp SERVICE_URL = StrProp.builder().mustBeNonNull().mustEndWith("/").build();
	private static final IntProp TIMEOUT = IntProp.builder().defaultValue(50).mustBeNonNull().build();
	private static final StrProp QUERY_ENDPOINT = StrProp.builder().mustBeNonNull().build();
	private static final StrProp ITEM_ENDPOINT = StrProp.builder().mustBeNonNull().build();
	
	//
	//System logging and cache configuration for this class
	public static final BolProp ENABLE_CACHE = BolProp.builder()
			.defaultValue(true).mustBeNonNull().build();
	
	public static final BolProp BROADCAST_LOG_EVENTS = BolProp.builder()
			.aliasIn("PS.Broadcast").defaultValue(true).mustBeNonNull()
			.desc("If true, logs events are sent to the central logging server").build();
	
	public static final StrProp LOG_SERVER = StrProp.builder().aliasIn("PS.LogServer")
			.mustStartWith("http://").mustEndWith("/").mustBeNonNull()
			.desc("The logging server to send events to").build();	
	
	public String getQueryUrl() {
		return SERVICE_URL.getValue() + QUERY_ENDPOINT.getValue();
	}
	
	public String getItemUrl() {
		return SERVICE_URL.getValue() + ITEM_ENDPOINT.getValue();
	}
	
	public int getTimeout() {
		return TIMEOUT.getValue(); //-- Note the strong typing of the return value
	}
	
	public boolean isCacheEnabled() {
		return ENABLE_CACHE.getValue();
	}
	
	public String getLogServer() {
		return LOG_SERVER.getValue();
	}
	
	public boolean isBroadcastLogEvents() {
		return BROADCAST_LOG_EVENTS.getValue();
	}
	
	
	/**
	 * In real life, there would be methods that actually use the configuration...
	 * @param planetName
	 * @return 
	 */
	public String fetchData(String planetName) {
		return "I found a bunch of data for " + planetName + " ";
	}
}
