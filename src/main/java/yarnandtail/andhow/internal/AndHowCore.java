package yarnandtail.andhow.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.load.FixedValueLoader;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.ReportGenerator;

/**
 * Actual central instance of the AndHow state after a successful startup.
 * The advertised AndHow class is really a proxy for this class, and allows
 * interaction with the AndHow framework prior to startup, reloading during unit
 * testing, and (potentially) a future implementation where reloading of production
 * data would be allowed.
 * 
 * @author eeverman
 */
public class AndHowCore implements ValueMap {
	//User config
	private final ArrayList<PointValue> forcedValues = new ArrayList();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy namingStrategy;
	private final List<String> cmdLineArgs = new ArrayList();
	
	//Internal state
	private final RuntimeDefinition runtimeDef;
	private final ValueMapWithContext loadedValues;
	private final List<ConstructionProblem> constructProblems = new ArrayList();
	private final ArrayList<LoaderException> loaderExceptions = new ArrayList();
	private final ArrayList<RequirementProblem> requirementsProblems = new ArrayList();
	
	public AndHowCore(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues) throws AppFatalException {
		
		this.namingStrategy = (naming != null)?naming:new BasicNamingStrategy();
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				if (! this.loaders.contains(loader)) {
					this.loaders.add(loader);
				} else {
					constructProblems.add(new ConstructionProblem.DuplicateLoader(loader));
				}
			}
		}
		
		if (startingValues != null) {
			forcedValues.addAll(startingValues);
			forcedValues.trimToSize();
		}
		
		if (cmdLineArgs != null && cmdLineArgs.length > 0) {
			this.cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
		}

		runtimeDef = AndHowUtil.doRegisterConfigPoints(registeredGroups, loaders, namingStrategy);
		constructProblems.addAll(runtimeDef.getConstructionProblems());
		
		//
		//If there are ConstructionProblems, we can't continue on to attempt to
		//load values.
		if (constructProblems.size() > 0) {
			throw new AppFatalException(constructProblems);
		}
		

		//Continuing on to load values
		try {
			loadedValues = loadValues().getValueMapWithContextImmutable();
		} catch (FatalException e) {
			AppFatalException afe = AndHowUtil.buildFatalException(requirementsProblems, null);
			
			printFailedStartupDetails(afe);
			
			throw AndHowUtil.buildFatalException(requirementsProblems, null);
		}

		checkForRequiredValues();

		if (requirementsProblems.size() > 0 || loadedValues.hasProblems()) {
			AppFatalException afe = AndHowUtil.buildFatalException(requirementsProblems, loadedValues);
			
			printFailedStartupDetails(afe);
			
			throw AndHowUtil.buildFatalException(requirementsProblems, loadedValues);
		}
	}
	
	private void printFailedStartupDetails(AppFatalException afe) {
		ReportGenerator.printProblems(System.err, afe, runtimeDef);
		ReportGenerator.printConfigSamples(System.err, runtimeDef, loaders);
	}

	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return runtimeDef.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return runtimeDef.getPoints();
	}
	
	@Override
	public boolean isExplicitlySet(ConfigPoint<?> point) {
		return loadedValues.isExplicitlySet(point);
	}
	
	@Override
	public <T> T getExplicitValue(ConfigPoint<T> point) {
		return loadedValues.getExplicitValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return loadedValues.getEffectiveValue(point);
	}
	
	private ValueMapWithContext loadValues() throws FatalException {
		ValueMapWithContextMutable existingValues = new ValueMapWithContextMutable();

		if (forcedValues.size() > 0) {
			FixedValueLoader fvl = new FixedValueLoader(forcedValues);
			loaders.add(0, fvl);
		}

		//LoaderState state = new LoaderState(cmdLineArgs, existingValues, runtimeDef);
		for (Loader loader : loaders) {
			LoaderValues result = loader.load(runtimeDef, cmdLineArgs, existingValues, loaderExceptions);
			existingValues.addValues(result);
		}

		return existingValues;
	}
	

	private void checkForRequiredValues() {
		
		for (ConfigPoint<?> cp : runtimeDef.getPoints()) {
			if (cp.isRequired()) {
				if (getEffectiveValue(cp) == null) {
					requirementsProblems.add(new RequirementProblem(cp));
				}
			}
		}
		
	}
		
}
