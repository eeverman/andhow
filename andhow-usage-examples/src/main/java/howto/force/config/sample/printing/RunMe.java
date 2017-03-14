package howto.force.config.sample.printing;

/**
 * Run this class to force the ReallySimpleApp to print sample configuration.
 * @author eeverman
 */
public class RunMe {
	public static void main(String[] args) {
		
		//Start the ReallySimpleApp, passing a flag forcing AndHow to print a
		//configuration sample.
		ReallySimpleApp.main(new String[] {"AHForceCreateSamples"});
		
			
		//The flag name above is an alias, for the fully qualified name, below.
		//ReallySimpleApp.main(new String[] {"org.yarnandtail.andhow.Options.CREATE_SAMPLES"});
		
		//Flag type properties are considered true just be being present in the
		//main arguments.  For other configuration sources that use key-value
		//pairs, it could be set true, as in 'AHForceCreateSamples=true'
	}
}
