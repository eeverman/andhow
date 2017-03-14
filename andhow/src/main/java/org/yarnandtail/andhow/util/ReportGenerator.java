package org.yarnandtail.andhow.util;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.internal.*;

/**
 *
 * @author ericeverman
 */
public class ReportGenerator {
	
	public static final int DEFAULT_LINE_WIDTH = 90;
		
	public static void printProblems(PrintStream out, AppFatalException fatalException, ConstructionDefinition appDef) {
		
		try {
			printProblemHR(out);
			out.println(TextUtil.padRight("== Problem report from " + AndHow.ANDHOW_NAME + "  " + AndHow.ANDHOW_TAG_LINE + "  ", "=", DEFAULT_LINE_WIDTH));
			out.println(TextUtil.padRight(TextUtil.repeat("=", 50) + "  " + AndHow.ANDHOW_URL + " ", "=", ReportGenerator.DEFAULT_LINE_WIDTH));
			printConstructionProblems(out, fatalException.getProblems().filter(ConstructionProblem.class), appDef);
			printLoaderProblems(out, fatalException.getProblems().filter(LoaderProblem.class), appDef);
			printValueProblems(out, fatalException.getProblems().filter(ValueProblem.class), appDef);
			printRequirementProblems(out, fatalException.getProblems().filter(RequirementProblem.class), appDef);
			printProblemHR(out);
		} catch (Exception e) {
			out.println("Uh oh.  There was an Exception while printing the list of problems with the app config.");
			out.println("Error Type: " + e.getClass().getCanonicalName() + ",  Error message: " + e.getMessage());
			e.printStackTrace(out);
		}
	}
	
	public static void printConstructionProblems(PrintStream out, List<ConstructionProblem> probs, ConstructionDefinition appDef) {
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
	
	public static void printLoaderProblems(PrintStream out, List<LoaderProblem> probs, ConstructionDefinition appDef) {
		if (! probs.isEmpty()) {
			
			TextUtil.println(out, DEFAULT_LINE_WIDTH, "", 
					"LOADER PROBLEMS - Issues that prevent a Loader from loading a value to a Property.");
			out.println();
			out.println("Detailed list of Loader Problems:");
			
			for (LoaderProblem p : probs) {
				TextUtil.println(out, DEFAULT_LINE_WIDTH, "", p.getFullMessage());
			}
			
		}
	}
	
	public static void printValueProblems(PrintStream out, List<ValueProblem> probs, ConstructionDefinition appDef) {
		if (! probs.isEmpty()) {
			
			TextUtil.println(out, DEFAULT_LINE_WIDTH, "", "VALUE PROBLEMS - Values that violate validation rules.");
			out.println();
			out.println("Detailed list of Value Problems:");
			
			for (ValueProblem p : probs) {
				TextUtil.println(out, DEFAULT_LINE_WIDTH, "", p.getFullMessage());
			}
			
		}
	}
	
	public static void printRequirementProblems(PrintStream out, List<RequirementProblem> probs, ConstructionDefinition appDef) {
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
	
	/**
	 * Print configurations samples for Loaders that support it.
	 * 
	 * @param out
	 * @param appDef
	 * @param loaders
	 * @param isDueToErrors If true, the reason for these samples is b/c there was a startup error.
	 */
	public static void printConfigSamples(ConstructionDefinition appDef, PrintStream out, 
			List<Loader> loaders, boolean isDueToErrors) {
		
		out.println();
		if (isDueToErrors) {
			out.println("== Since there were startup errors, sample configuration "
					+ "will be printed for each Loader that supports it ==");
		} else {
			out.println("== As requested, sample configuration "
					+ "will be printed for each Loader that supports it ==");
		}
		out.println();
		
		//Set of loader type-dialect's that have been printed.  Skip duplicates.
		HashSet<String> printedLoaderTypes = new HashSet();
		
		int supportedLoaders = 0;
		
		for (Loader loader : loaders) {
			
			SamplePrinter printer = loader.getConfigSamplePrinter();
			
			if (printer != null) {
				
				String fullType = TextUtil.trimToEmpty(loader.getLoaderType()) + 
						"---" + TextUtil.trimToEmpty(loader.getLoaderDialect());
				
				if (! printedLoaderTypes.contains(fullType)) {
					printedLoaderTypes.add(fullType);
					
					supportedLoaders++;
					printer.printSampleStart(appDef, out);

					for (Class<? extends PropertyGroup> group : appDef.getPropertyGroups()) {

						printer.printPropertyGroupStart(appDef, out, group);

						try {
							for (Property<?> prop : appDef.getPropertiesForGroup(group)) {
								printer.printProperty(appDef, out, group, prop);
							}
						} catch (Exception ex) {
							TextUtil.println(out, DEFAULT_LINE_WIDTH, "", "SECURITY EXCEPTION TRYING TO ACCESS THIS GROUP. " +
									"ENSURE ALL Property FIELDS ARE PUBLIC IN THE PropertyGroup " +
									"AND THAT THERE IS NOT A SECURITY MANAGER BLOCKING ACCESS TO REFLECTION.");
						}

						printer.printPropertyGroupEnd(appDef, out, group);
					}

					printer.printSampleEnd(appDef, out);
				}
			}
			
			
		}
		
		if (supportedLoaders == 0) {
			out.println();
			out.println("== None of the configured Loaders support creating configuration samples ==");
			out.println();
		}
	}
	
}
