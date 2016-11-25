package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.load.LoaderState;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AppConfigCore {
	//User config
	private final Map<ConfigPoint<?>, Object> forcedValues = new HashMap();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy namingStrategy;
	private final List<String> cmdLineArgs = new ArrayList();
	
	//Internal state
	private final AppConfigDefinition appConfigDef;
	private final List<Map<ConfigPoint<?>, Object>> loadedValues = new ArrayList();
	private final List<ValidationException> validationExceptions = new ArrayList();
	
	public AppConfigCore(NamingStrategy naming, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, Map<ConfigPoint<?>, Object> startingValues) {
		this.namingStrategy = (naming != null)?naming:new BasicNamingStrategy();
		
		if (loaders != null) {
			this.loaders.addAll(loaders);
		}
		
		if (startingValues != null) {
			forcedValues.putAll(startingValues);
		}
		
		if (cmdLineArgs != null && cmdLineArgs.length > 0) {
			this.cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
		}

		appConfigDef = AppConfigUtil.doRegisterConfigPoints(registeredGroups, loaders, namingStrategy);

		if (appConfigDef.getNamingExceptions().size() > 0) {
			throw new ConfigurationException(appConfigDef.getNamingExceptions(), null, null);
		}

		loadValues();

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
	
	public boolean isPointPresent(ConfigPoint<?> point) {
		return getValue(point) != null;
	}
	
	public Object getValue(ConfigPoint<?> point) {
		for (Map<ConfigPoint<?>, Object> map : loadedValues) {
			if (map.containsKey(point)) {
				return map.get(point);
			}
		}
		
		return null;
	}
	
//	private AppConfigDefinition registerGroups(List<Class<? extends ConfigPointGroup>> registeredGroups) {
//		return AppConfigUtil.doRegisterConfigPoints(registeredGroups, namingStrategy);
//	}
	
	
	private void loadValues() throws FatalException {
		List<Map<ConfigPoint<?>, Object>> existingValues = new ArrayList();

		if (forcedValues.size() > 0) {
			existingValues.add(forcedValues);
		}

		LoaderState state = new LoaderState(cmdLineArgs, existingValues, appConfigDef);
		for (Loader loader : loaders) {
			Map<ConfigPoint<?>, Object> result = loader.load(state);
			if (result.size() > 0) existingValues.add(result);
		}

		loadedValues.clear();
		loadedValues.addAll(existingValues);
	}
	
	
	private void validateValues() {
		for (ConfigPoint<?> cp : appConfigDef.getPoints()) {
			if (cp.isRequired()) {
				if (getValue(cp) == null && cp.getDefaultValue() == null) {
					validationExceptions.add(new RequiredPointException(cp, appConfigDef.getCanonicalName(cp)));
				}
			}
		}
	}
		
}
