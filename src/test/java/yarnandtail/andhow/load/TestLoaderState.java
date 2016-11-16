package yarnandtail.andhow.load;

import yarnandtail.andhow.load.LoaderException;
import yarnandtail.andhow.load.LoaderState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.ConfigPoint;

/**
 * Test implementation designed for easy use during testing.
 * @author eeverman
 */
public class TestLoaderState implements LoaderState {

	String[] cmdLineArgs = new String[0];
	List<Map<ConfigPoint<?>, Object>> existingValues = new ArrayList();
	Map<String, ConfigPoint> registeredConfigPoints = new HashMap();
	List<LoaderException> loaderExceptions = new ArrayList();

	public TestLoaderState() {
		clear();
	}
			
	@Override
	public String[] getCmdLineArgs() {
		return cmdLineArgs;
	}

	@Override
	public List<Map<ConfigPoint<?>, Object>> getExistingValues() {
		return existingValues;
	}

	@Override
	public Map<String, ConfigPoint> getRegisteredConfigPoints() {
		return registeredConfigPoints;
	}

	@Override
	public List<LoaderException> getLoaderExceptions() {
		return loaderExceptions;
	}
	
	public void setCmdLineArgs(String[] cmdLineArgs) {
		this.cmdLineArgs = cmdLineArgs;
	}

	public void setExistingValues(List<Map<ConfigPoint<?>, Object>> existingValues) {
		this.existingValues = existingValues;
	}

	public void setRegisteredConfigPoints(Map<String, ConfigPoint> registeredConfigPoints) {
		this.registeredConfigPoints = registeredConfigPoints;
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
	
	public void clear() {
		String[] cmdLineArgs = new String[0];
		List<Map<ConfigPoint, String>> existingValues = new ArrayList();
		Map<String, ConfigPoint> registeredConfigPoints = new HashMap();
		List<LoaderException> loaderExceptions = new ArrayList();
	}
}
