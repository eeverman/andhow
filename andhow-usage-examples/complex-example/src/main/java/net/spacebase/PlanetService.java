package net.spacebase;

import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

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

	
	public String getQueryUrl() {
		return SERVICE_URL.getValue() + QUERY_ENDPOINT.getValue();
	}
	
	public String getItemUrl() {
		return SERVICE_URL.getValue() + ITEM_ENDPOINT.getValue();
	}
	
	public int getTimeout() {
		return TIMEOUT.getValue(); //-- Note the strong typing of the return value
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
