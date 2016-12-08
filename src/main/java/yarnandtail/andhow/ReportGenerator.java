package yarnandtail.andhow;

import java.io.PrintStream;
import java.util.List;
import yarnandtail.andhow.appconfig.AppConfigDefinition;

/**
 *
 * @author ericeverman
 */
public class ReportGenerator {
	
	public static final int DEFAULT_LINE_WIDTH = 90;
	
	public static final String ANDHOW_NAME = "AndHow!";
	public static final String ANDHOW_URL = "https://github.com/eeverman/andhow";
	public static final String ANDHOW_TAG_LINE = "strong.valid.simple.App_Configuration";
	
	public static void printProblems(PrintStream out, AppFatalException fatalException, AppConfigDefinition appDef) {
		
		try {
			printProblemHR(out);
			out.println(TextUtil.padRight("== Problem report from " + ANDHOW_NAME + "  " + ANDHOW_TAG_LINE + "  ", "=", DEFAULT_LINE_WIDTH));
			out.println(TextUtil.padRight(TextUtil.repeat("=", 50) + "  " + ANDHOW_URL + " ", "=", ReportGenerator.DEFAULT_LINE_WIDTH));
			printConfigurationProblems(out, fatalException.getConstructionProblems(), appDef);
			printPointValueProblems(out, fatalException.getPointValueProblems(), appDef);
			printRequirementProblems(out, fatalException.getRequirementsProblems(), appDef);
			printProblemHR(out);
		} catch (Exception e) {
			out.println("Uh oh.  There was an Exception while printing the list of problems with the app config.");
			out.println("Error Type: " + e.getClass().getCanonicalName() + ",  Error message: " + e.getMessage());
			e.printStackTrace(out);
		}
	}
	
	public static void printConfigurationProblems(PrintStream out, List<ConstructionProblem> probs, AppConfigDefinition appDef) {
		if (probs.isEmpty()) {
			
//			printProblemHR(out);
//			out.println("CONSTRUCTION PROBLEMS:  Excellent!  There are no construction problems");
//			printProblemHR(out);
			
		} else {
			
			out.println("CONSTRUCTION PROBLEMS  ~Foundational problems with the App Configuration.");
			out.println("~The AppConfig may have been built in an inconsistent way, such as default values that violate validation rules.");
			out.println("~When there are construction problems, no attempt is made to load configuration values.");
			out.println();
			out.println("Defailed list of Construction Problems:");
			
			for (ConstructionProblem p : probs) {
				out.println(p.getMessage());
			}
			
		}
	}
	
	public static void printPointValueProblems(PrintStream out, List<PointValueProblem> probs, AppConfigDefinition appDef) {
		if (probs.isEmpty()) {
			
//			printProblemHR(out);
//			out.println("VALUE PROBLEMS:  Nice! There are no value problems.");
//			printProblemHR(out);
			
		} else {
			
			out.println("VALUE PROBLEMS  ~Values that violate validation rules, "
					+ "or source values that cannot be converted to their destination type.");
			out.println();
			out.println("Defailed list of Value Problems:");
			
			for (PointValueProblem p : probs) {
				TextUtil.println(out, "ConfigPoint {} loaded from {}: {}", 
						appDef.getCanonicalName(p.getPoint()), 
						p.getLoader().getSpecificLoadDescription(),
						p.getMessageWithinFullContext());
			}
			
		}
	}
	
	public static void printRequirementProblems(PrintStream out, List<RequirementProblem> probs, AppConfigDefinition appDef) {
		if (probs.isEmpty()) {
			
//			printProblemHR(out);
//			out.println("REQUIRMENT PROBLEMS:  Well done!  There are no requirment problems");
//			printProblemHR(out);
			
		} else {

			out.println("REQUIRMENT PROBLEMS  ~When a required configuration point is not provided");
			out.println("~Note:  Prior issues that prevente individual values from loading may also result in requirements problems.");
			out.println();
			out.println("Defailed list of Requirements Problems:");
			
			for (RequirementProblem p : probs) {
				if (p.getPoint() != null) {
					TextUtil.println(out, "ConfigPoint {}: {}", 
						appDef.getCanonicalName(p.getPoint()), 
						p.getMessageWithinFullContext());
				} else {
					TextUtil.println(out, "ConfigPointGroup {}: {}", 
						p.getGroup().getCanonicalName(), 
						p.getMessageWithinFullContext());
				}

			}
			
		}
	}
	
	public static void printProblemHR(PrintStream out) {
		out.println(TextUtil.repeat("=", DEFAULT_LINE_WIDTH));
	}
	
	public static void printConfigSamples(PrintStream out, AppConfigDefinition appDef, List<Loader> loaders) {
		
		for (Loader loader : loaders) {
			if (loader instanceof ConfigSamplePrinter) {
				ConfigSamplePrinter printer = (ConfigSamplePrinter)loader;
				
				printer.printSampleStart(out);
				
				for (Class<? extends ConfigPointGroup> group : appDef.getGroups()) {
					
					printer.printConfigGroupStart(out, group);
					
					try {
						for (ConfigPoint<?> point : appDef.getPointsForGroup(group)) {
							printer.printConfigPoint(out, group, point);
						}
					} catch (Exception ex) {
						out.println("SECURITY EXCEPTION TRYING TO ACCESS THIS GROUP. " +
								"ENSURE ALL ConfigPoint FIELDS ARE PUBLIC IN THE ConfigPointGroup " +
								"AND THAT THERE IS NOT A SECURITY MANAGER BLOCKING ACCESS TO REFLECTION.");
					}
					
					printer.printConfigGroupEnd(out, group);
				}
				
				printer.printSampleEnd(out);
			}
			
			
		}
	}
	
}
