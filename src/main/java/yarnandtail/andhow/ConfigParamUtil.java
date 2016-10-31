package yarnandtail.andhow;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author eeverman
 */
public class ConfigParamUtil {

	public static List<Param> parseCommandArgs(
			List<List<ParamDefinition>> configParamEnumLists, String[] args, String nameValueSeparator) {
		
		List<Param> configParams = new ArrayList();
		
		for (String arg : args) {
			NameValuePair pair = parseCommandArg(arg, nameValueSeparator);
			ParamDefinition cpEnum = findConfigParam(configParamEnumLists, pair.name);
			
			ParamMutable cpm = new ParamMutable(cpEnum, arg, pair.name, pair.value, null);
			cpm.setValid(validatedConfigParam(cpm));
			configParams.add(cpm.toImmutable());
		}
		
		return configParams;
		
	}
	
	public static ParamDefinition findConfigParam(List<List<ParamDefinition>> configParamEnumLists, String name) {
		
		name = StringUtils.trimToNull(name);
		
		for (List<ParamDefinition> enumList : configParamEnumLists) {
			for (ParamDefinition param : enumList) {
				if (param.isMatch(name)) {
					return param;
				}
			}
		}
		
		return null;
	}
	
	public static boolean validatedConfigParam(Param configParam) {
		
		
		if (configParam.getParamDefinition() != null) {
			ParamDefinition cpe = configParam.getParamDefinition();
			
			if (cpe.getParamType().isRequired()) {
				if (configParam.getValue() == null) return false;
			}
			
		}
		
		return true;
	}
	
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
	 * Parses command arguments into name value pairs if they contain the specified separator.
	 * 
	 * @param arg
	 * @return 
	 */
	public static NameValuePair parseCommandArg(String arg, String separator) {
		
		arg = StringUtils.trimToEmpty(arg);
		
		if (arg.contains(separator)) {
			
			String[] parts = arg.split(separator);
			
			if (parts.length == 2) {
				return new NameValuePair(StringUtils.trimToNull(parts[0]), StringUtils.trimToNull(parts[1]));
			} else {
				throw new RuntimeException("The command argument '" + arg + 
						"' has more than one '" + separator + "' name/value separators.  "
						+ "There should be just one in the form name" + separator + "value.");
			}
			
		} else {
			return new NameValuePair(arg);
		}
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
	
	public static void printParams(List<Param> configParams, PrintStream ps, String header) {
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
					"|" + p.getOriginalText() +
					"|" + p.getName() +
					"|" + p.getEffectiveValue() +
					"|" + p.getParamDefinition().getExplicitName() +
					"|" + p.getParamType() +
					"|"
				)
		);
	}
	
	
	public static void printHelpForParams(List<List<ParamDefinition>> configParamEnumLists, String[] configParamSetNames, PrintStream ps, String header) {
		if (header != null) ps.println(header);
		
		for (int i = 0; i < configParamEnumLists.size(); i++) {
			
			List<ParamDefinition> subList = configParamEnumLists.get(i);
			
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
			
			for (ParamDefinition pd : subList) {
				if (pd.getParamType().isReal()) {
					ps.print(pd.getExplicitName());
					if (! pd.getParamType().isFlag()) {
						ps.print("=[value]");
					}
					
					if (! pd.getAlias().isEmpty()) {
						ps.print("  aliases: " + StringUtils.join(pd.getAlias(), ", "));
					}
					ps.println();
					ps.println("    " + pd.getShortDescription());
					ps.println("    " + pd.getHelpText());
					
					if (! pd.getPossibleValues().isEmpty()) {
						ps.println("    Allowed Values: " + StringUtils.join(pd.getPossibleValues(), ", "));
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
