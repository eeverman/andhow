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
import org.yarnandtail.andhow.api.BasePropertyGroup;

/**
 * Actual central instance of the AndHow state after a successful startup.
 * The advertised AndHow class is really a proxy for this class, and allows
 * interaction with the AndHow framework prior to startup, reloading during unit
 * testing, and (potentially) a future implementation where reloading of production
 * data would be allowed.
 * 
 * @author eeverman
 */
public class AndHowCore implements ConstructionDefinition, ValueMap {
	//User config
	private final List<Loader> loaders = new ArrayList();
	
	//Internal state
	private final ConstructionDefinition runtimeDef;
	private final ValueMapWithContext loadedValues;
	private final ProblemList<Problem> problems = new ProblemList();
	
	public AndHowCore(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends BasePropertyGroup>> registeredGroups) 
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
		ArrayList<Class<? extends BasePropertyGroup>> effRegGroups = new ArrayList();
		if (registeredGroups != null) {
			effRegGroups.addAll(registeredGroups);
		}
		effRegGroups.add(Options.class);

		ConstructionDefinitionMutable startupDef = AndHowUtil.buildDefinition(effRegGroups, loaders, namingStrategy, problems);
		runtimeDef = startupDef.toImmutable();
		
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
		
		//Continuing on to load values
		loadedValues = loadValues(runtimeDef, problems).getValueMapWithContextImmutable();

		checkForValuesWhichMustBeNonNull(runtimeDef, problems);

		if (problems.size() > 0) {
			AppFatalException afe = AndHowUtil.buildFatalException(problems);
			printFailedStartupDetails(afe);
			throw afe;
		}
		
		//Export Values if applicable
		List<ExportGroup> exportGroups = runtimeDef.getExportGroups();
		for (ExportGroup eg : exportGroups) {
			Exporter exporter = eg.getExporter();
			Class<? extends BasePropertyGroup> group = eg.getGroup();
			
			if (group != null) {
				exporter.export(group, runtimeDef, this);
			} else {
				for (Class<? extends BasePropertyGroup> grp : runtimeDef.getPropertyGroups()) {
					exporter.export(grp, runtimeDef, this);
				}
			}
		}
		
		//Print samples (if requested) to System.out
		if (Options.CREATE_SAMPLES.getValue(this)) {
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
		
			ReportGenerator.printConfigSamples(runtimeDef, ps, loaders, false);

			try {
				String message = os.toString("UTF8");
				System.out.println(message);
			} catch (UnsupportedEncodingException ex) {
				//This shouldn't happen, but don't want to have the message burried
				ReportGenerator.printConfigSamples(runtimeDef, System.out, loaders, true);
			}
		}
	}
	
	/**
	 * Prints failed startup details to System.err
	 * 
	 * @param afe 
	 */
	private void printFailedStartupDetails(AppFatalException afe) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		
		ReportGenerator.printProblems(ps, afe, runtimeDef);
		ReportGenerator.printConfigSamples(runtimeDef, ps, loaders, true);
		
		try {
			String message = os.toString("UTF8");
			System.err.println(message);
		} catch (UnsupportedEncodingException ex) {
			//This shouldn't happen, but don't want to have the message burried
			ReportGenerator.printProblems(System.err, afe, runtimeDef);
			ReportGenerator.printConfigSamples(runtimeDef, System.err, loaders, true);
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
	public <T> T getEffectiveValue(Property<T> prop) {
		return loadedValues.getEffectiveValue(prop);
	}
	
	//TODO:  Shouldn't this be stateless and pass in the loader list?
	private ValueMapWithContext loadValues(ConstructionDefinition definition, ProblemList<Problem> problems) {
		ValueMapWithContextMutable existingValues = new ValueMapWithContextMutable();

		for (Loader loader : loaders) {
			LoaderValues result = loader.load(definition, existingValues);
			existingValues.addValues(result);
			problems.addAll(result.getProblems());
		}

		return existingValues;
	}
	

	private void checkForValuesWhichMustBeNonNull(ConstructionDefinition definition, ProblemList<Problem> problems) {
		
		for (Property<?> prop : definition.getProperties()) {
			if (prop.isNonNullRequired()) {
				if (getEffectiveValue(prop) == null) {
					
					problems.add(
						new RequirementProblem.NonNullPropertyProblem(
								definition.getGroupForProperty(prop), prop));
				}
			}
		}
		
	}

	
	//
	//ConstructionDefinition Interface
	
	@Override
	public List<EffectiveName> getAliases(Property<?> property) {
		return runtimeDef.getAliases(property);
	}

	@Override
	public String getCanonicalName(Property<?> prop) {
		return runtimeDef.getCanonicalName(prop);
	}

	@Override
	public Class<? extends BasePropertyGroup> getGroupForProperty(Property<?> prop) {
		return runtimeDef.getGroupForProperty(prop);
	}

	@Override
	public List<Property<?>> getPropertiesForGroup(Class<? extends BasePropertyGroup> group) {
		return runtimeDef.getPropertiesForGroup(group);
	}

	@Override
	public Property<?> getProperty(String name) {
		return runtimeDef.getProperty(name);
	}
	
	@Override
	public List<Class<? extends BasePropertyGroup>> getPropertyGroups() {
		return runtimeDef.getPropertyGroups();
	}

	@Override
	public List<Property<?>> getProperties() {
		return runtimeDef.getProperties();
	}

	@Override
	public List<ExportGroup> getExportGroups() {
		return runtimeDef.getExportGroups();
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return runtimeDef.getNamingStrategy();
	}
	
	@Override
	public Map<String, String> getSystemEnvironment() {
		return runtimeDef.getSystemEnvironment();
	}
	
		
}
