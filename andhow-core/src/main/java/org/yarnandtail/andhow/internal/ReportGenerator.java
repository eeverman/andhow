package org.yarnandtail.andhow.internal;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.Options;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem.UnknownPropertyLoaderProblem;
import org.yarnandtail.andhow.util.*;

/**
 *
 * @author ericeverman
 */
public class ReportGenerator {

	public static final int DEFAULT_LINE_WIDTH = 90;

	public static void printProblems(PrintStream out, AppFatalException fatalException, PropertyConfigurationInternal appDef) {

		try {
			printProblemHR(out);
			out.println(TextUtil.padRight("== Problem report from " + AndHow.ANDHOW_NAME + "  " + AndHow.ANDHOW_TAG_LINE + "  ", "=", DEFAULT_LINE_WIDTH));
			out.println(TextUtil.padRight(TextUtil.repeat("=", 50) + "  " + AndHow.ANDHOW_URL + " ", "=", ReportGenerator.DEFAULT_LINE_WIDTH));

			if (! appDef.containsUserGroups()) {
				if (! fatalException.getProblems().filter(UnknownPropertyLoaderProblem.class).isEmpty()) {
					TextUtil.println(out, DEFAULT_LINE_WIDTH, "",
						"No AndHow Properties are registered and there were some "
						+ "values for unrecognized property names. It may be that "
						+ "org.yarnandtail:andhow-annotation-processor is not on the classpath "
						+ "at compile time.  If it was not on the classpath, "
						+ "AndHow Properties in source code were not discovered and registered. "
						+ "To resolve, add org.yarnandtail:andhow-annotation-processor "
						+ "as a dependency at least at compile time."
					);
				}
			}

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

	public static void printConstructionProblems(PrintStream out, List<ConstructionProblem> probs, PropertyConfigurationInternal appDef) {
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

	public static void printLoaderProblems(PrintStream out, List<LoaderProblem> probs, PropertyConfigurationInternal appDef) {
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

	public static void printValueProblems(PrintStream out, List<ValueProblem> probs, PropertyConfigurationInternal appDef) {
		if (! probs.isEmpty()) {

			TextUtil.println(out, DEFAULT_LINE_WIDTH, "", "VALUE PROBLEMS - Values that violate validation rules.");
			out.println();
			out.println("Detailed list of Value Problems:");

			for (ValueProblem p : probs) {
				TextUtil.println(out, DEFAULT_LINE_WIDTH, "", p.getFullMessage());
			}

		}
	}

	public static void printRequirementProblems(PrintStream out, List<RequirementProblem> probs, PropertyConfigurationInternal appDef) {
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
	 * Writes sample configuration files for all Loaders that support it.
	 *
	 * Sample files are written to the the directory specified by:
	 * Options.SAMPLES_DIRECTORY, which defaults to something under the java.io.temp
	 * directory.
	 *
	 * @param appDef
	 * @param loaders
	 * @param isDueToErrors
	 * @return The File directory the sample configuration files were written to.
	 */
	public static File printConfigSamples(PropertyConfigurationInternal appDef,
  		List<Loader> loaders, boolean isDueToErrors) {

		AndHowLog log = AndHowLog.getLogger(ReportGenerator.class);

		String sampleDirPath = Options.SAMPLES_DIRECTORY.getDefaultValue();

		if (AndHow.isInitialized()) {
			sampleDirPath = Options.SAMPLES_DIRECTORY.getValue();
		}

		sampleDirPath = IOUtil.expandDirectoryPath(sampleDirPath);
		File sampleDir = new File(sampleDirPath);

		if (isDueToErrors) {
			log.error("Drat! There are AndHow startup errors. "
					+ "As a result, a set of sample configuration files will be written to ''{0}''",
					sampleDirPath);
		} else {
			log.mandatoryNote("As 'requested', sample configuration files will be created for each " +
					"Loader that supports it and written to ''{0}''", sampleDirPath);
		}

		try {

			sampleDir.mkdirs();

			//Test creation and deletion of a sample file
			File testFile = File.createTempFile("test", "tmp", sampleDir);
			testFile.deleteOnExit();
			testFile.delete();

			printConfigSamples(appDef, sampleDir, loaders, isDueToErrors);

			return sampleDir;

		} catch (Exception e) {
			log.error("Normally AndHow samples are written as files to a temp directory, "
					+ "however, there was an error writing to the temp directory '"
					+ sampleDirPath + "'.  Giving up writing samples.", e);

			return null;
		}

	}

	/**
	 * Print configurations samples for Loaders that support it.
	 *
	 * @param sampleDir The directory to write configuration samples to
	 * @param appDef
	 * @param loaders
	 * @param isDueToErrors If true, the reason for these samples is b/c there was a startup error.
	 */
	public static void printConfigSamples(PropertyConfigurationInternal appDef, File sampleDir,
                                          List<Loader> loaders, boolean isDueToErrors) {

		AndHowLog log = AndHowLog.getLogger(ReportGenerator.class);

		//Set of loader type-dialect's that have been printed.  Skip duplicates.
		HashSet<String> printedLoaderTypes = new HashSet();

		int supportedLoaders = 0;

		for (Loader loader : loaders) {

			SamplePrinter printer = loader.getConfigSamplePrinter();

			if (printer != null) {

				String fullType = TextUtil.trimToEmpty(loader.getLoaderType());

				if (TextUtil.trimToNull(loader.getLoaderDialect()) != null) {
					fullType += "_" + TextUtil.trimToEmpty(loader.getLoaderDialect());
				}

				if (! printedLoaderTypes.contains(fullType)) {
					printedLoaderTypes.add(fullType);

					supportedLoaders++;

					File singleSample = new File(sampleDir, fullType + "." + printer.getSampleFileExtension());

					try (PrintStream printWriter = new PrintStream(new BufferedOutputStream(new FileOutputStream(singleSample, false)))) {
						printSingleLoader(appDef, printWriter, printer);
					} catch (IOException ex) {
						log.error("Unable to create sample configuration file '" +
								singleSample.getAbsolutePath() + "'", ex);
					}

				}
			}


		}

		if (supportedLoaders == 0) {
			log.error("== None of the configured Loaders support creating configuration samples ==");
		}
	}

	public static void printSingleLoader(PropertyConfigurationInternal appDef, PrintStream out, SamplePrinter printer) {
		printer.printSampleStart(appDef, out);

		for (GroupProxy group : appDef.getPropertyGroups()) {

			printer.printPropertyGroupStart(appDef, out, group);

			try {
				for (Property<?> prop : appDef.getPropertiesForGroup(group)) {
					printer.printProperty(appDef, out, group, prop);
				}
			} catch (Exception ex) {

				TextUtil.println(out, DEFAULT_LINE_WIDTH, "",
						"EXCEPTION WHILE INSPECTING A PROPERTY " +
						"IN '" + group.getCanonicalName() + "'. " +
						"IS THERE A SECURITY MANAGER BLOCKING REFLECTION? " +
						"EXCEPTION TYPE: " + ex.getClass().getName());
			}

			printer.printPropertyGroupEnd(appDef, out, group);
		}

		printer.printSampleEnd(appDef, out);
	}

}
