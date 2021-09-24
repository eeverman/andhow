package com.bigcorp;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.export.ExportCollector;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

import java.util.Properties;

/**
 * A hypothetical class that configures the ORM framework Hibernate using AndHow Properties.
 * <p>
 * Of course, this isn't a complete application, but it shows how this might be done.
 */
@ManualExportAllowed(useCanonicalName = Exporter.EXPORT_CANONICAL_NAME.NEVER)
public class HibernateInit {

	//All the config Properties for this class
	// - Its a best practice to group AndHow Properties into an interface.
	private interface Config {
		StrProp DRIVER = StrProp.builder().aliasInAndOut("hibernate.connection.driver_class")
				.mustBeNonNull().build();
		StrProp CONN_URL = StrProp.builder().aliasInAndOut("hibernate.connection.url")
				.mustBeNonNull().startsWith("jdbc:").build();
		StrProp USER = StrProp.builder().aliasInAndOut("hibernate.connection.username")
				.mustBeNonNull().build();
		StrProp PWD = StrProp.builder().aliasInAndOut("hibernate.connection.password")
				.mustBeNonNull().build();
		StrProp ISOLATION = StrProp.builder().aliasInAndOut("hibernate.connection.isolation")
				.oneOf("REPEATABLE_READ", "TRANSACTION_REPEATABLE_READ")
				.defaultValue("REPEATABLE_READ").build();
		IntProp INIT_POOL_SIZE = IntProp.builder().aliasInAndOut("hibernate.connection.initial_pool_size")
				.defaultValue(1).lessThan(20).build();
		IntProp POOL_SIZE = IntProp.builder().aliasInAndOut("hibernate.connection.pool_size")
				.defaultValue(20).lessThan(200).build();
	}

	@ManualExportNotAllowed
	private interface OTHER_CONFIG {
		StrProp PWD = StrProp.builder().desc("Some other secret pwd that not to be exported").build();
	}

	/**
	 * In a real app, this wouldn't be a public method since it exposes db connection info,
	 * but it makes testing easy for this example.
	 *
	 * @return
	 * @throws IllegalAccessException
	 */
	public Properties getHibernateProperties() throws IllegalAccessException {
		return AndHow.instance().export(HibernateInit.class)
				.collect(ExportCollector.stringProperties(""));
	}

	public HibernateConfiguration pretendToinitializeHibernate() throws IllegalAccessException {
		Properties props = getHibernateProperties();

		HibernateConfiguration hibConfig = new HibernateConfiguration();
		hibConfig.setProperties(props);

		return hibConfig;
	}


	/**
	 * Fake class that pretends to be a Hibernate Configuration object.
	 * See the real thing here:
	 * https://docs.jboss.org/hibernate/orm/3.3/reference/en-US/html/session-configuration.html
	 */
	public static class HibernateConfiguration {
		Properties props;

		void setProperties(Properties props) {
			this.props = props;
		}

		Properties getProperties() {
			return props;
		}
	}


	/**
	 * A main method to run this class as a small app - In a real app, a main method would not be here
	 * - its just for testing.
	 * <p>
	 * Run it from an IDE , or via command line.
	 * <p>
	 * To run from command line, first use Maven to create a runnable jar.
	 * Here are the commands, executed from the root of the AndHow project, to build and run the jar:<br>
	 * <pre>{@code
	 * > mvn clean package -DskipTests -Dmaven.javadoc.skip=true
	 * > java -jar andhow-testing/andhow-simulated-app-tests/example-app-3/target/app.jar
	 * }</pre>
	 * The output of this command will match the config in {@code andhow.properties}:
	 * <pre>
	 */
	public static void main(String[] args) throws Exception {

		AndHow.findConfig().setCmdLineArgs(args);

		HibernateInit hibernateInit = new HibernateInit();


		HibernateConfiguration hc = hibernateInit.pretendToinitializeHibernate();

		hc.getProperties().entrySet().stream().forEach(
				e -> System.out.println(String.format("name: %1s value: %2s", e.getKey(), e.getValue()))
		);
	}

}
