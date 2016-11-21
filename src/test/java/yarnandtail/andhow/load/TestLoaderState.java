package yarnandtail.andhow.load;

import yarnandtail.andhow.LoaderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigDefinition;
import yarnandtail.andhow.ConfigPoint;

/**
 * Test implementation designed for easy use during testing.
 * @author eeverman
 */
public class TestLoaderState extends LoaderState {

	public TestLoaderState() {
		super(new ArrayList<String>(), new ArrayList<Map<ConfigPoint<?>, Object>>(), new AppConfigDefinition());
	}
			
	@Override
	public List<String> getCmdLineArgs() {
		return cmdLineArgs;
	}

	@Override
	public List<Map<ConfigPoint<?>, Object>> getExistingValues() {
		return existingValues;
	}

	@Override
	public AppConfigDefinition getAppConfigDef() {
		return appConfigDef;
	}

	@Override
	public List<LoaderException> getLoaderExceptions() {
		return loaderExceptions;
	}
	
	public void setCmdLineArgs(List<String> cmdLineArgs) {
		this.cmdLineArgs = cmdLineArgs;
	}

	public void setExistingValues(List<Map<ConfigPoint<?>, Object>> existingValues) {
		this.existingValues = existingValues;
	}

	public void setAppConfigDef(AppConfigDefinition appConfigDef) {
		this.appConfigDef = appConfigDef;
	}

	public void setLoaderExceptions(List<LoaderException> loaderExceptions) {
		this.loaderExceptions = loaderExceptions;
	}
	
	public void addLoaderException(LoaderException loaderException) {
		if (loaderExceptions == null) {
			loaderExceptions = new ArrayList();
		}
		loaderExceptions.add(loaderException);
	}

}
