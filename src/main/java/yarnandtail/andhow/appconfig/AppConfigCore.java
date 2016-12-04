package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.load.FixedValueLoader;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AppConfigCore implements AppConfigValues {
	//User config
	private final ArrayList<PointValue> forcedValues = new ArrayList();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy namingStrategy;
	private final List<String> cmdLineArgs = new ArrayList();
	
	//Internal state
	private final AppConfigDefinition appConfigDef;
	private final AppConfigStructuredValues loadedValues;
	private final ArrayList<LoaderException> loaderExceptions = new ArrayList();
	private final ArrayList<RequirmentProblem> requirementsProblems = new ArrayList();
	
	public AppConfigCore(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues) throws AppFatalException {
		
		this.namingStrategy = (naming != null)?naming:new BasicNamingStrategy();
		
		if (loaders != null) {
			this.loaders.addAll(loaders);
		}
		
		if (startingValues != null) {
			forcedValues.addAll(startingValues);
			forcedValues.trimToSize();
		}
		
		if (cmdLineArgs != null && cmdLineArgs.length > 0) {
			this.cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
		}

		appConfigDef = AppConfigUtil.doRegisterConfigPoints(registeredGroups, loaders, namingStrategy);

		if (appConfigDef.getNamingExceptions().size() > 0) {
			throw new ConstructionException(appConfigDef.getNamingExceptions(), null, null);
		}

		loadedValues = loadValues().getUnmodifiableAppConfigStructuredValues();

		validateValues();

		if (requirementsProblems.size() > 0 || loadedValues.hasProblems()) {
			throw AppConfigUtil.buildFatalException(requirementsProblems, loadedValues);
		}
	}
	

	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return appConfigDef.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return appConfigDef.getPoints();
	}
	
	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return loadedValues.isPointPresent(point);
	}
	
	@Override
	public <T> T getValue(ConfigPoint<T> point) {
		return loadedValues.getValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return loadedValues.getEffectiveValue(point);
	}
	
	private AppConfigStructuredValues loadValues() throws FatalException {
		AppConfigStructuredValuesBuilder existingValues = new AppConfigStructuredValuesBuilder();

		if (forcedValues.size() > 0) {
			FixedValueLoader fvl = new FixedValueLoader(forcedValues);
			loaders.add(0, fvl);
		}

		//LoaderState state = new LoaderState(cmdLineArgs, existingValues, appConfigDef);
		for (Loader loader : loaders) {
			LoaderValues result = loader.load(appConfigDef, cmdLineArgs, existingValues, loaderExceptions);
			existingValues.addValues(result);
		}

		return existingValues;
	}
	

	
	private void validateValues() {
		
		boolean hasIssues = false;
		
		for (ConfigPoint<?> cp : appConfigDef.getPoints()) {
			if (cp.isRequired()) {
				if (getEffectiveValue(cp) == null) {
					requirementsProblems.add(new RequirmentProblem(cp));
				}
			}
		}
		
	}
		
}
