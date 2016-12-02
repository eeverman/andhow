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
	private final ArrayList<ValidationException> validationExceptions = new ArrayList();
	
	public AppConfigCore(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues) {
		
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
			throw new ConfigurationException(appConfigDef.getNamingExceptions(), null, null);
		}

		loadedValues = loadValues().getUnmodifiableAppConfigStructuredValues();

		validateValues();

		if (validationExceptions.size() > 0) {
			throw new ConfigurationException(null, null, validationExceptions);
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
				if (getValue(cp) == null && cp.getDefaultValue() == null) {
					validationExceptions.add(new RequiredPointException(cp, appConfigDef.getCanonicalName(cp)));
				}
			}
		}
		
		for (LoaderValues lvs : loadedValues.getAllLoaderValues()) {
			for (PointValue pv : lvs.getValues()) {
				for (ValueIssue issue : pv.getIssues()) {
					hasIssues = true;
					
					System.err.println(issue.getMessageInPointContext());
				}
			}
		}
		
		if (hasIssues) {
			throw new RuntimeException("VAlues are not valid!!");
		}

		
	}
		
}
