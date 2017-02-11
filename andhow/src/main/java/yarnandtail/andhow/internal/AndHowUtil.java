package yarnandtail.andhow.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;

/**
 * Utilities used by AndHow during initial construction.
 * @author eeverman
 */
public class AndHowUtil {
	

	/**
	 * Build a fully populated ConstructionDefinition from the passed Groups, 
	 * using the NamingStrategy to generate names for each.
	 * 
	 * @param groups The PropertyGroups from which to find Properties.  May be null.
	 * @param loaders The Loaders, which may their own configurable PropertyGroups.
	 * @param naming  A naming strategy to use when reading the properties during loading
	 * @param problems If construction problems are found, add to this list.
	 * @return A fully configured instance
	 */
	public static ConstructionDefinitionMutable buildDefinition(
			List<Class<? extends PropertyGroup>> groups, List<Loader> loaders, 
			NamingStrategy naming, ProblemList<Problem> problems) {

		ConstructionDefinitionMutable appDef = new ConstructionDefinitionMutable(naming);
		
		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
			for (Class<? extends PropertyGroup> group : groups) {

				problems.addAll(registerGroup(appDef, group));
				
				try {
					List<Exporter> exps = PropertyGroup.getExporters(group);
					
					for (Exporter e : exps) {
						ExportGroup eg = new ExportGroup(e, group);
						appDef.addExportGroup(eg);
					}
					
				} catch (InstantiationException ex) {
					ConstructionProblem.ExportException ee = 
							new ConstructionProblem.ExportException(ex, group,
							"Unable to created a new instance of one of the Exporters for this group.  "
									+ "Do they all have zero argument constructors?");
					problems.add(ee);
				} catch (IllegalAccessException ex) {
					ConstructionProblem.SecurityException se = 
						new ConstructionProblem.SecurityException(ex, group);
					problems.add(se);
				}
				
			}
		}
		
		//Loaders must be after properties b/c the loaders may look for registered
		//Properties.
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				
				//Add any implicit properties used to configure this loader
				if (loader.getLoaderConfig() != null) {
					problems.addAll(registerGroup(appDef, loader.getLoaderConfig()));
				}
				
				//Check that user specified config properties for this loader are registered
				for (Property p : loader.getUserLoaderConfig()) {
					if (appDef.getCanonicalName(p) == null) {
						problems.add(new ConstructionProblem.LoaderPropertyNotRegistered(loader, p));
					}
				}
			}
		}
		
		return appDef;
	}
		
	protected static ProblemList<ConstructionProblem> registerGroup(ConstructionDefinitionMutable appDef,
			Class<? extends PropertyGroup> group) {
		
		ProblemList<ConstructionProblem> problems = new ProblemList();

		try {
			List<PropertyGroup.NameAndProperty> nameAndProperties = PropertyGroup.getProperties(group);
			
			for (PropertyGroup.NameAndProperty nameAndProp : nameAndProperties) {
				problems.add(appDef.addProperty(group, nameAndProp.property));
			}
			
		} catch (Exception ex) {
			ConstructionProblem.SecurityException se = 
					new ConstructionProblem.SecurityException(ex, group);
			problems.add(se);
		}
		
		return problems;
	}
		
	public static void printExceptions(List<? extends Exception> exceptions, PrintStream out) {
		for (Exception ne : exceptions) {
			out.println(ne.getMessage());
		}
	}
	
	public static AppFatalException buildFatalException(ProblemList<Problem> problems) {
		
		
		return new AppFatalException(
				"Unable to complete application configuration due to problems. " +
				"See the System.err out or the log files for complete details.",
				problems);
		
	}
}
