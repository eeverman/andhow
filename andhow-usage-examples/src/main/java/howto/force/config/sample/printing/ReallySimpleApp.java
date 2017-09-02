package howto.force.config.sample.printing;

import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.PropertyGroup;

/**
 * A simple app to demonstrate that sample configuration can be forced at startup.
 * To try it out, see the RunMe class.
 * 
 * @author eeverman
 */
public class ReallySimpleApp {
	
	public static void main(String[] args) {
		AndHow.builder()
				.cmdLineArgs(args) /* Implicitly adds a loader for these cmd line args */
				.loader(new PropertyFileOnClasspathLoader(MySetOfProps.CLASSPATH_PROP))
				.group(MySetOfProps.class) /* MySetOfProps defined below */
				.build();
	
		System.out.println("Examples of using the configured properties (they initially have default values)");
		System.out.println("The query url is: " + MySetOfProps.SERVICE_URL.getValue());
		System.out.println("The timeout is : " + MySetOfProps.TIMEOUT.getValue());
		
		//Implicit global property
		System.out.println("Was sample creation requested: " + Options.CREATE_SAMPLES.getValue());
		
		
		System.out.println("To override default values, copy the generated "
				+ "sample output from the console to a file on the classpath."); 
		System.out.println("Then pass the CLASSPATH_PROP in the main args to this class, "
				+ "eg: 'propFile=/path/to/my/config.properties'  OR:");
		System.out.println("Add a default classpath to the CLASSPATH_PROP at the bottom of the ReallySimpleApp file, "
				+ "eg: '.defaultValue(\"/path/to/my/config.properties\")'");
	}
	
	//Normally PropertyGroups would be in separate file - combined here for simplicity
	@GroupInfo(name="Example Property group", desc="One logical set of properties - all are optional")
	@GlobalPropertyGroup
	public interface MySetOfProps extends PropertyGroup {
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").aliasIn("url").defaultValue("http://server.com/").build();
		IntProp TIMEOUT = IntProp.builder().defaultValue(50).build();
		StrProp CLASSPATH_PROP = StrProp.builder().desc("Classpath location of properties file")
				.aliasIn("propFile").build();
	}
}
