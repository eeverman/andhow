package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.Options;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.export.PropertyExport;
import org.yarnandtail.andhow.internal.export.ManualExportService;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.util.AndHowLog;
import org.yarnandtail.andhow.util.AndHowUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Actual central instance of AndHow's state after a successful initialization.
 * <p>
 * The AndHow class is really a proxy for this class, which allows
 * abstraction of the implementation and re-initialization during unit testing
 * by swapping out the AndHowCore implementation while maintaining the AndHow
 * singleton so references to it are stable.
 */
public class AndHowCore implements PropertyConfigurationInternal, ValidatedValues {
	private static final AndHowLog LOG = AndHowLog.getLogger(AndHowCore.class);

	//User config
	private final List<Loader> loaders = new ArrayList();

	//Internal state
	private final PropertyConfigurationInternal staticConfig;
	private final ValidatedValuesWithContext loadedValues;
	private final ProblemList<Problem> problems = new ProblemList();

	public AndHowCore(NamingStrategy naming, List<Loader> loaders,
			List<GroupProxy> registeredGroups)
			throws AppFatalException {

		NamingStrategy namingStrategy = (naming != null) ? naming : new CaseInsensitiveNaming();

		if (loaders != null) {
			for (Loader loader : loaders) {
				if (!this.loaders.contains(loader)) {
					this.loaders.add(loader);
				} else {
					problems.add(new ConstructionProblem.DuplicateLoader(loader));
				}
			}
		}

		List<GroupProxy> effRegGroups = findGroups(registeredGroups);

		if (effRegGroups.isEmpty()) {
			LOG.warn("AndHow found no Properties to configure.  " +
					"If this is unexpected, verify AndHowCompileProcessor was on the classpath at compile time " +
					"(Maven artifact andhow-annotation-processor) and javac annotation processing was not disabled " +
					"(javac -proc flags).");
		}

		try {
			//Global options are general config of AndHow itself
			GroupProxy options = AndHowUtil.buildGroupProxy(Options.class, false);
			effRegGroups.add(options);
		} catch (Exception ex) {
			problems.add(new ConstructionProblem.SecurityException(ex, Options.class));
		}


		PropertyConfigurationMutable startupDef = AndHowUtil.buildDefinition(effRegGroups, loaders, namingStrategy, problems);
		staticConfig = startupDef.toImmutable();

		//
		//If there are ConstructionProblems, we can't continue on to attempt to
		//load values.
		if (problems.size() > 0) {
			AppFatalException afe = new AppFatalException(
					"There is a problem with the basic setup of the " + AndHow.ANDHOW_INLINE_NAME + " framework. " +
							"Since it is the framework itself that is misconfigured, no attempt was made to load values. " +
							"See System.err, out or the log files for more details.",
					problems);
			printFailedStartupDetails(afe);
			throw afe;
		}

		//No Construction problems, so continue on...

		loadedValues = loadValues(staticConfig, problems).getValueMapWithContextImmutable();
		doPropertyValidations(staticConfig, loadedValues, problems);
		checkForValuesWhichMustBeNonNull(staticConfig, loadedValues, problems);

		if (problems.size() > 0) {
			AppFatalException afe = AndHowUtil.buildFatalException(problems);
			printFailedStartupDetails(afe);
			throw afe;
		}

		//Export Values if applicable
		List<ExportGroup> exportGroups = staticConfig.getExportGroups();

		for (ExportGroup eg : exportGroups) {
			eg.getExporter().export(eg.getGroup(), staticConfig, this);
		}

		//Print samples (if requested) to System.out
		if (getValue(Options.CREATE_SAMPLES)) {
			ReportGenerator.printConfigSamples(staticConfig, loaders, false);
		}
	}


	/**
	 * Determine the 'Groups' (classes or interfaces containing AndHow Properties) that should be in
	 * scope of AndHow.
	 * <p>
	 * In special situations (testing and exotic use cases), a non-null list of configuredGroups may
	 * be passed in to bypass automatic discovery.  If the passed groups is null, use auto-discovery.
	 * If non-null, use the passed list, even if empty.
	 *
	 * @param configuredGroups A list of groups to use instead of the normal auto-discovery.
	 *                         If null, auto-discovery is used.  If non-null (even empty) configuredGroups is used.
	 * @return A list of groups that are in-scope for AndHow.  Not null.
	 */
	private static List<GroupProxy> findGroups(List<GroupProxy> configuredGroups) {
		if (configuredGroups == null) {
			PropertyRegistrarLoader registrar = new PropertyRegistrarLoader();
			return registrar.getGroups();
		} else {
			return configuredGroups;
		}
	}

	/**
	 * Adds a NonNullPropertyProblem for each null valued Property that is required to be non-null.
	 * <p>
	 * All parameters are required to be non-null and will throw a null-pointer otherwise.
	 * <p>
	 * @param config Configuration and metadata for all known Properties
	 * @param values Values loaded for the Properties
	 * @param problems A list of Problems to append to
	 */
	public static void checkForValuesWhichMustBeNonNull(PropertyConfigurationInternal config,
			ValidatedValues values, ProblemList<Problem> problems) {

		for (Property<?> prop : config.getProperties()) {
			if (prop.isNonNullRequired()) {
				if (values.getValue(prop) == null) {

					problems.add(new RequirementProblem.NonNullPropertyProblem(
							config.getGroupForProperty(prop).getProxiedGroup(), prop));
				}
			}
		}

	}

	/**
	 * Prints failed startup details to System.err
	 *
	 * @param afe
	 */
	private void printFailedStartupDetails(AppFatalException afe) {

		File sampleDir = ReportGenerator.printConfigSamples(staticConfig, loaders, true);
		String sampleDirStr = (sampleDir != null) ? sampleDir.getAbsolutePath() : "";
		afe.setSampleDirectory(sampleDirStr);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		ReportGenerator.printProblems(ps, afe, staticConfig);

		try {
			String message = os.toString("UTF8");
			//Add separator prefix to prevent log prefixes from indenting 1st line
			System.err.println(System.lineSeparator() + message);
		} catch (UnsupportedEncodingException ex) {
			ReportGenerator.printProblems(System.err, afe, staticConfig);  //shouldn't happen
		}

	}

	public Stream<PropertyExport> export(Class<?>... exportClasses) throws IllegalAccessException {
		if (exportClasses != null) {
			ManualExportService svs = new ManualExportService();

			return svs.doManualExport(Arrays.asList(exportClasses), staticConfig.getPropertyGroups());
		} else {
			throw new IllegalArgumentException("Cannot export a null list of classes");
		}
	}

	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return loadedValues.isExplicitlySet(prop);
	}

	/**
	 * The value found and loaded for this value by a Loader.
	 * <p>
	 * If no non-null value was found by a loader for this property, null is returned.
	 * If the Property is not recognized, an IllegalArgumentException is thrown.
	 * In normal usage, AndHow would not expect an unrecognized Property, so this would indicate
	 * an unrecoverable state.
	 *
	 * @param <T>  The return type of the Property.
	 * @param prop The property to get the value for
	 * @return The value, if explicitly set, or null if not explicity set.
	 * @throws IllegalArgumentException if the Property is not recognized by AndHow.
	 */
	@Override
	public <T> T getExplicitValue(Property<T> prop) throws IllegalArgumentException {

		T val = loadedValues.getExplicitValue(prop);

		if (val == null && staticConfig.getCanonicalName(prop) == null) {
			throw new IllegalArgumentException("Unrecognized Property of type " +
					"'" + prop.getValueType().getDestinationType() + "'. Likely caused by one of:" + System.lineSeparator() +
					" - AndHow error'ed on startup, but the error was caught and did not cause app " +
					"startup to abort.  Check the logs and remove try-catch that intercepts RuntimeExceptions." +
					System.lineSeparator() +
					" - Code was compiled without AndHow's annotation processor." + System.lineSeparator() +
					" - The Property was created in some exotic way that was not detected by the AndHow " +
					"annotation processor.  Review the creation of this Property and make sure it follows " +
					"AndHow guidelines.");
		}

		return val;
	}

	/**
	 * The effective value of the Property.
	 * <p>
	 * The effective value is the explicitly configured value, or if that is null, the default value.
	 * If the Property is not recognized, an IllegalArgumentException is thrown.
	 * In normal usage, AndHow would not expect an unrecognized Property, so this would indicate
	 * an unrecoverable state.
	 * <p>
	 * @param <T> The return type of the Property.
	 * @param prop The property to get the value for.
	 * @return The explicit value or, if no explicit, the default value.  Otherwise null.
	 * @throws IllegalArgumentException if the Property is not recognized by AndHow.
	 */
	@Override
	public <T> T getValue(Property<T> prop) throws IllegalArgumentException {
		T val = getExplicitValue(prop);

		if (val != null) {
			return val;
		} else {
			return prop.getDefaultValue();
		}
	}

	//TODO:  Shouldn't this be stateless and pass in the loader list?
	private ValidatedValuesWithContext loadValues(PropertyConfigurationInternal config, ProblemList<Problem> problems) {
		ValidatedValuesWithContextMutable existingValues = new ValidatedValuesWithContextMutable();

		for (Loader loader : loaders) {
			LoaderValues result = loader.load(config, existingValues);
			existingValues.addValues(result);
			problems.addAll(result.getProblems());

			loader.releaseResources();
		}

		return existingValues;
	}

	/**
	 * Validates all Property values.
	 *
	 * @param config       Needed bc validation is done while construction is
	 *                     not complete, thus the as-is definition is needed prior to it being complete.
	 * @param loadedValues The values to be validated.
	 * @param problems     Add any new problems to this list
	 */
	private void doPropertyValidations(PropertyConfigurationInternal config,
			ValidatedValuesWithContext loadedValues, ProblemList<Problem> problems) {

		for (LoaderValues lvs : loadedValues.getAllLoaderValues()) {
			for (ValidatedValue pv : lvs.getValues()) {
				doPropertyValidation(config, lvs.getLoader(), problems, pv);
			}
		}
	}

	/**
	 * Does validation on a single Property value as loaded by a single loader.
	 *
	 * @param <T>          The shared type of the Property and Value.
	 * @param config       Needed bc validation is done while construction is
	 *                     not complete, thus the as-is definition is needed prior to it being complete.
	 * @param loader       The loader used to load the value, for context when creating a Problem.
	 * @param problems     Add any new problems to this list
	 * @param propValue<T> The Property and its value, both of type 'T'.
	 */
	private <T> void doPropertyValidation(PropertyConfigurationInternal config,
			Loader loader, ProblemList<Problem> problems, ValidatedValue<T> propValue) {

		Property<T> prop = propValue.getProperty();

		for (Validator<T> v : prop.getValidators()) {
			if (!v.isValid(propValue.getValue())) {

				ValueProblem.InvalidValueProblem problem =
						new ValueProblem.InvalidValueProblem(loader,
								config.getGroupForProperty(prop).getProxiedGroup(),
								prop, propValue.getValue(), v);

				propValue.addProblem(problem);
				problems.add(problem);
			}
		}
	}


	//
	//ConstructionDefinition Interface

	@Override
	public List<EffectiveName> getAliases(Property<?> property) {
		return staticConfig.getAliases(property);
	}

	@Override
	public String getCanonicalName(Property<?> prop) {
		return staticConfig.getCanonicalName(prop);
	}

	@Override
	public GroupProxy getGroupForProperty(Property<?> prop) {
		return staticConfig.getGroupForProperty(prop);
	}

	@Override
	public List<Property<?>> getPropertiesForGroup(GroupProxy group) {
		return staticConfig.getPropertiesForGroup(group);
	}

	@Override
	public Property<?> getProperty(String name) {
		return staticConfig.getProperty(name);
	}

	@Override
	public List<GroupProxy> getPropertyGroups() {
		return staticConfig.getPropertyGroups();
	}

	@Override
	public boolean containsUserGroups() {
		return staticConfig.containsUserGroups();
	}

	@Override
	public List<Property<?>> getProperties() {
		return staticConfig.getProperties();
	}

	@Override
	public List<ExportGroup> getExportGroups() {
		return staticConfig.getExportGroups();
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return staticConfig.getNamingStrategy();
	}

}
