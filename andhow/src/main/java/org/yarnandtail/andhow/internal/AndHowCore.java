package org.yarnandtail.andhow.internal;

import java.io.*;
import org.yarnandtail.andhow.util.AndHowUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.Options;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;

/**
 * Actual central instance of the AndHow state after a successful startup.
 * The advertised AndHow class is really a proxy for this class, and allows
 * interaction with the AndHow framework prior to startup, reloading during unit
 * testing, and (potentially) a future implementation where reloading of production
 * data would be allowed.
 * 
 * @author eeverman
 */
public class AndHowCore implements StaticPropertyConfiguration, ValidatedValues {
	//User config
	private final List<Loader> loaders = new ArrayList();
	
	//Internal state
	private final StaticPropertyConfiguration staticConfig;
	private final ValidatedValuesWithContext loadedValues;
	private final ProblemList<Problem> problems = new ProblemList();
	
	public AndHowCore(NamingStrategy naming, List<Loader> loaders, 
			List<GroupProxy> registeredGroups) 
			throws AppFatalException {
		
		NamingStrategy namingStrategy = (naming != null)?naming:new CaseInsensitiveNaming();
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				if (! this.loaders.contains(loader)) {
					this.loaders.add(loader);
				} else {
					problems.add(new ConstructionProblem.DuplicateLoader(loader));
				}
			}
		}		
		
		//The global options are always added to the list of registered groups
		ArrayList<GroupProxy> effRegGroups = new ArrayList();
		if (registeredGroups != null) {
			effRegGroups.addAll(registeredGroups);
		}
		
		try {
			GroupProxy options = AndHowUtil.buildGroupProxy(Options.class);
			effRegGroups.add(options);
		} catch (Exception ex) {
			problems.add(new ConstructionProblem.SecurityException(ex, Options.class));
		}


		StaticPropertyConfigurationMutable startupDef = AndHowUtil.buildDefinition(effRegGroups, loaders, namingStrategy, problems);
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
		checkForValuesWhichMustBeNonNull(staticConfig, problems);

		if (problems.size() > 0) {
			AppFatalException afe = AndHowUtil.buildFatalException(problems);
			printFailedStartupDetails(afe);
			throw afe;
		}
		
		//Export Values if applicable
		List<ExportGroup> exportGroups = staticConfig.getExportGroups();
		for (ExportGroup eg : exportGroups) {
			Exporter exporter = eg.getExporter();
			GroupProxy group = eg.getGroup();
			
			if (group != null) {
				exporter.export(group, staticConfig, this);
			} else {
				for (GroupProxy grp : staticConfig.getPropertyGroups()) {
					exporter.export(grp, staticConfig, this);
				}
			}
		}
		
		//Print samples (if requested) to System.out
		if (getValue(Options.CREATE_SAMPLES)) {
			ReportGenerator.printConfigSamples(staticConfig, loaders, false);
		}
	}
	
	/**
	 * Prints failed startup details to System.err
	 * 
	 * @param afe 
	 */
	private void printFailedStartupDetails(AppFatalException afe) {
		
		File sampleDir = ReportGenerator.printConfigSamples(staticConfig, loaders, true);
		String sampleDirStr = (sampleDir != null)?sampleDir.getAbsolutePath():"";
		afe.setSampleDirectory(sampleDirStr);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		ReportGenerator.printProblems(ps, afe, staticConfig);
		
		try {
			String message = os.toString("UTF8");
			//Add separator prefix to prevent log prefixes from indenting 1st line
			System.err.println(System.lineSeparator() + message);
		} catch (UnsupportedEncodingException ex) {
			ReportGenerator.printProblems(System.err, afe, staticConfig);	//shouldn't happen	
		}
		
	}
	
	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return loadedValues.isExplicitlySet(prop);
	}
	
	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		return loadedValues.getExplicitValue(prop);
	}
	
	@Override
	public <T> T getValue(Property<T> prop) {
		return loadedValues.getValue(prop);
	}
	
	//TODO:  Shouldn't this be stateless and pass in the loader list?
	private ValidatedValuesWithContext loadValues(StaticPropertyConfiguration config, ProblemList<Problem> problems) {
		ValidatedValuesWithContextMutable existingValues = new ValidatedValuesWithContextMutable();

		for (Loader loader : loaders) {
			LoaderValues result = loader.load(config, existingValues);
			existingValues.addValues(result);
			problems.addAll(result.getProblems());
		}

		return existingValues;
	}
	
	/**
	 * Validates all Property values.
	 * 
	 * @param config Needed bc validation is done while construction is 
	 *	not complete, thus the as-is definition is needed prior to it being complete.
	 * @param loadedValues The values to be validated.
	 * @param problems Add any new problems to this list
	 */
	private void doPropertyValidations(StaticPropertyConfiguration config, 
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
	 * @param <T> The shared type of the Property and Value.
	 * @param config Needed bc validation is done while construction is 
	 *	not complete, thus the as-is definition is needed prior to it being complete.
	 * @param loader The loader used to load the value, for context when creating a Problem.
	 * @param problems Add any new problems to this list
	 * @param propValue<T> The Property and its value, both of type 'T'.
	 */
	private <T> void doPropertyValidation(StaticPropertyConfiguration config,
			Loader loader, ProblemList<Problem> problems, ValidatedValue<T> propValue) {
		
		Property<T> prop = propValue.getProperty();
		
		for (Validator<T> v : prop.getValidators()) {
			if (! v.isValid(propValue.getValue())) {
				
				ValueProblem.InvalidValueProblem problem = 
						new ValueProblem.InvalidValueProblem(loader, 
								config.getGroupForProperty(prop).getProxiedGroup(),
								prop, propValue.getValue(), v);
				
				propValue.addProblem(problem);
				problems.add(problem);
			}
		}
	}
	

	private void checkForValuesWhichMustBeNonNull(StaticPropertyConfiguration config, ProblemList<Problem> problems) {
		
		for (Property<?> prop : config.getProperties()) {
			if (prop.isNonNullRequired()) {
				if (getValue(prop) == null) {
					
					problems.add(new RequirementProblem.NonNullPropertyProblem(
								config.getGroupForProperty(prop).getProxiedGroup(), prop));
				}
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
	
	@Override
	public Map<String, String> getSystemEnvironment() {
		return staticConfig.getSystemEnvironment();
	}
	
		
}
