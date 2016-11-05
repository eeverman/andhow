package yarnandtail.andhow;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author eeverman
 */
public class ConfigParamUtil {

	/**
	 * Parses a string to a boolean.
	 * It just uses Apache BooleanUtils, but this codifies that this is how its done.
	 * @param value
	 * @return 
	 */
	public static boolean toBoolean(String value) {
		return BooleanUtils.toBoolean(value);
	}

	
	/**
	 * Returns a file reference, expanding the path as needed.
	 * 
	 * The path expands as follows:<ul>
	 * <li>Entries that begin w/ a tilde (~) are expanded to the current user's home directory
	 * <li>Empty paths are expanded to be the executable directory, which would
	 * be the directory containing the jar file if running from a jar.
	 * A null path is treated as empty.
	 * </ul>
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static File getFile(String path, String fileName) {
		
		path = StringUtils.trimToEmpty("");
		
		if (path.startsWith("~")) {
			path = path.replaceFirst("~", System.getProperty("user.home"));
		} else if (path.equals("")) {
			path = StringUtils.trimToEmpty(findExecutableDirectory());
		}
		
		File dir = new File(path);
		File file = new File(dir, fileName);
		
		return file;
	}
	
	private static String findExecutableDirectory() {
		try {
			String path = ConfigParamUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			File jarFile = new File(path);
			File jarDir = jarFile.getParentFile();

			if (jarDir.exists()) {
				return jarDir.getCanonicalPath();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void printParams(List<ConfigPointLoaderValue> configParams, PrintStream ps, String header) {
		if (header != null) ps.println(header);
		ps.println("Parameter Key: ");
		ps.println(					
				"|" + " Valid? " +
				"|" + " Original param text " +
				"|" + " Name in org text " +
				"|" + " Interprited Value " +
				"|" + " Full name of parameter " +
				"|" + " Parameter type (see ParamType enum for definitions) " +
				"|"
		);
		configParams.stream().forEachOrdered(p -> 
				ps.println(
					"|" + p.isValid() +
					"|" + p.getExplicitString() +
					"|" + p.getExplicitKey() +
					"|" + p.getString() +
					"|" + p.getConfigPointUsage().getEffectiveName() +
					"|" + p.getConfigPointUsage().getPointType() +
					"|"
				)
		);
	}
	
	
	public static void printHelpForParams(List<List<ConfigPointDef>> configParamEnumLists, String[] configParamSetNames, PrintStream ps, String header) {
		if (header != null) ps.println(header);
		
		for (int i = 0; i < configParamEnumLists.size(); i++) {
			
			List<ConfigPointDef> subList = configParamEnumLists.get(i);
			
			String setName = null;
			if (configParamSetNames != null && i < configParamSetNames.length) {
				setName = configParamSetNames[i];
			}
			
			if (header != null) {
				ps.println("Available configuration parameters for: " + setName);
			} else if (configParamEnumLists.size() > 1) {
				ps.println("Available configuration parameters for set " + (i + 1));
			} else {
				ps.println("All available configuration parameters:");
			}
			
			for (ConfigPointDef pd : subList) {
				if (pd.getPointType().isReal()) {
					ps.print(pd.getExplicitBaseName());
					if (! pd.getPointType().isFlag()) {
						ps.print("=[value]");
					}
					
					if (! pd.getBaseAliases().isEmpty()) {
						ps.print("  aliases: " + StringUtils.join(pd.getBaseAliases(), ", "));
					}
					ps.println();
					ps.println("    " + pd.getShortDescription());
					ps.println("    " + pd.getHelpText());
					
					if (! pd.getPossibleValueEnums().isEmpty()) {
						ps.println("    Allowed Values: " + StringUtils.join(pd.getPossibleValueEnums(), ", "));
					}

				}
			}
		}
	}
	
	
	
	public static class NameValuePair {
		String name;
		String value;
		
		public NameValuePair(String name) {
			this.name = name;
		}

		public NameValuePair(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
}
