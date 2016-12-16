package yarnandtail.andhow;

import java.io.PrintStream;
import java.util.List;
import static yarnandtail.andhow.TextUtil.SECOND_LINE_INDENT;
import yarnandtail.andhow.internal.RuntimeDefinition;

/**
 *
 * @author ericeverman
 */
public class ReportGenerator {
	
	public static final int DEFAULT_LINE_WIDTH = 90;
		
	public static void printProblems(PrintStream out, AppFatalException fatalException, RuntimeDefinition appDef) {
		
		try {
			printProblemHR(out);
			out.println(TextUtil.padRight("== Problem report from " + AndHow.ANDHOW_NAME + "  " + AndHow.ANDHOW_TAG_LINE + "  ", "=", DEFAULT_LINE_WIDTH));
			out.println(TextUtil.padRight(TextUtil.repeat("=", 50) + "  " + AndHow.ANDHOW_URL + " ", "=", ReportGenerator.DEFAULT_LINE_WIDTH));
			printConstructionProblems(out, fatalException.getConstructionProblems(), appDef);
			printValueProblems(out, fatalException.getValueProblems(), appDef);
			printRequirementProblems(out, fatalException.getRequirementProblems(), appDef);
			printProblemHR(out);
		} catch (Exception e) {
			out.println("Uh oh.  There was an Exception while printing the list of problems with the app config.");
			out.println("Error Type: " + e.getClass().getCanonicalName() + ",  Error message: " + e.getMessage());
			e.printStackTrace(out);
		}
	}
	
	public static void printConstructionProblems(PrintStream out, List<ConstructionProblem> probs, RuntimeDefinition appDef) {
		if (! probs.isEmpty()) {
			
			out.println("CONSTRUCTION PROBLEMS  - Basic problems configuring and starting up the " + AndHow.ANDHOW_INLINE_NAME + " frameowork.");
			out.println(AndHow.ANDHOW_INLINE_NAME + " may have been built in an inconsistent way, such as a default value that violates its own validation rules.");
			out.println("When there are construction problems, no attempt is made to load configuration values.");
			out.println();
			out.println("Detailed list of Construction Problems:");
			
			for (ConstructionProblem p : probs) {
				TextUtil.println(out, DEFAULT_LINE_WIDTH, "", p.getFullMessage());
			}
			
		}
	}
	
	public static void printValueProblems(PrintStream out, List<ValueProblem> probs, RuntimeDefinition appDef) {
		if (! probs.isEmpty()) {
			
			TextUtil.println(out, DEFAULT_LINE_WIDTH, "", "VALUE PROBLEMS - Values that violate validation rules, "
					+ "or source values that cannot be converted to their destination type.");
			out.println();
			out.println("Detailed list of Value Problems:");
			
			for (ValueProblem p : probs) {
				TextUtil.println(out, DEFAULT_LINE_WIDTH, "", p.getFullMessage());
			}
			
		}
	}
	
	public static void printRequirementProblems(PrintStream out, List<RequirementProblem> probs, RuntimeDefinition appDef) {
		if (! probs.isEmpty()) {

			out.println("REQUIRMENT PROBLEMS - When a required property is not provided");
			out.println("Note:  Issues, above, preventing values from loading may also result in requirements problems.");
			out.println();
			out.println("Detailed list of Requirements Problems:");

			for (RequirementProblem p : probs) {
				TextUtil.println(out, DEFAULT_LINE_WIDTH, "", p.getFullMessage());
			}
			
		}
	}
	
	public static void printProblemHR(PrintStream out) {
		out.println(TextUtil.repeat("=", DEFAULT_LINE_WIDTH));
	}
	
	public static void printConfigSamples(PrintStream out, RuntimeDefinition appDef, List<Loader> loaders) {
		
		for (Loader loader : loaders) {
			if (loader instanceof ConfigSamplePrinter) {
				ConfigSamplePrinter printer = (ConfigSamplePrinter)loader;
				
				printer.printSampleStart(out);
				
				for (Class<? extends PropertyGroup> group : appDef.getPropertyGroups()) {
					
					printer.printPropertyGroupStart(out, group);
					
					try {
						for (Property<?> prop : appDef.getPropertiesForGroup(group)) {
							printer.printProperty(out, group, prop);
						}
					} catch (Exception ex) {
						TextUtil.println(out, DEFAULT_LINE_WIDTH, "", "SECURITY EXCEPTION TRYING TO ACCESS THIS GROUP. " +
								"ENSURE ALL Property FIELDS ARE PUBLIC IN THE PropertyGroup " +
								"AND THAT THERE IS NOT A SECURITY MANAGER BLOCKING ACCESS TO REFLECTION.");
					}
					
					printer.printPropertyGroupEnd(out, group);
				}
				
				printer.printSampleEnd(out);
			}
			
			
		}
	}
	
}
