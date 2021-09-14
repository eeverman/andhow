package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.AndHow;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Implementations of {@link Collector} that can be used collect a {@code stream()} of
 * {@link PropertyExport}s into key-value pair Maps or {@link java.util.Properties}.
 * <p>
 * See {@link AndHow#export()} for export details and examples.
 */
public class ExportCollector {

	private ExportCollector() { /* No instances */ }

	//Copied from Collectors:  OK to run in parallel, order not important, finisher is just identity.
	static final Set<Collector.Characteristics> CH_CONCURRENT_ID
			= Collections.unmodifiableSet(EnumSet.of(
					Collector.Characteristics.CONCURRENT,
					Collector.Characteristics.UNORDERED,
					Collector.Characteristics.IDENTITY_FINISH)
	);

	/**
	 * Returns a {@link Collector} that collects {@link PropertyExport}s into a
	 * {@link Map}{@code <String, String>} collection of key-value pairs.
	 * <p>
	 * Usage example:
	 * <pre>{@code
	 * Map<String, String> export =
	 *  AndHow.instance().export(MyClass.class)
	 *    .collect(ExportCollector.stringMap());
	 * }</pre>
	 * <p>
	 * <em>Note:  This method calls {@link PropertyExport#getValueAsString()} for the
	 * 'value' part of the key-value pair.  That value can be remapped - See
	 * {@link PropertyExport#mapValueAsString(String)}.</em>
	 * <p>
	 * See {@link AndHow#export()} for export details and more examples.
	 * <p>
	 * @return A {@link Collector} that creates a {@link Map}{@code <String, String>}
	 *   from a {@link PropertyExport} stream.
	 */
	public static StringMap stringMap() {
		return new StringMap();
	}

	/**
	 * Returns a {@link Collector} that collects {@link PropertyExport}s into a
	 * {@link java.util.Properties} object with key-value entries that are Strings.
	 * <p>
	 * Usage example:
	 * <pre>{@code
	 * Map<String, String> export =
	 *  AndHow.instance().export(MyClass.class)
	 *    .collect(ExportCollector.stringProperties());
	 * }</pre>
	 * <p>
	 * <em>Note:  This method calls {@link PropertyExport#getValueAsString()} for the
	 * 'value' part of the key-value pair.  That value can be remapped - See
	 * {@link PropertyExport#mapValueAsString(String)}.</em>
	 * <p>
	 * See {@link AndHow#export()} for export details and more examples.
	 * <p>
	 * @param nullValueString The value to use for Properties that have a null
	 *   value, since {@link java.util.Properties} cannot contain null values.
	 * @return A {@link Collector} that creates a {@link java.util.Properties} with String
	 *   values from a {@link PropertyExport} stream.
	 */
	public static StringProperties stringProperties(String nullValueString) {
		return new StringProperties(nullValueString);
	}

	/**
	 * Returns a {@link Collector} that collects {@link PropertyExport}s into a
	 * {@link Map}{@code <String, Object>} collection of key-value pairs.
	 * <p>
	 * Usage example:
	 * <pre>{@code
	 * Map<String, String> export =
	 *  AndHow.instance().export(MyClass.class)
	 *    .collect(ExportCollector.objectMap());
	 * }</pre>
	 * <p>
	 * <em>Note:  This method calls {@link PropertyExport#getValue()} for the
	 * 'value' part of the key-value pair.  That value can be remapped - See
	 * {@link PropertyExport#mapValue(Object)}.</em>
	 * <p>
	 * See {@link AndHow#export()} for export details and more examples.
	 * <p>
	 * @return A {@link Collector} that creates a {@link Map}{@code <String, Object>}
	 *   from a {@link PropertyExport} stream.
	 */
	public static ObjectMap objectMap() {
		return new ObjectMap();
	}

	/**
	 * Returns a {@link Collector} that collects {@link PropertyExport}s into a
	 * {@link java.util.Properties} object with key-value entries that have Strings
	 * keys and Object values.
	 * <p>
	 * Usage example:
	 * <pre>{@code
	 * Map<String, String> export =
	 *  AndHow.instance().export(MyClass.class)
	 *    .collect(ExportCollector.objectProperties());
	 * }</pre>
	 * <p>
	 * <em>Note:  This method calls {@link PropertyExport#getValue()} for the
	 * 'value' part of the key-value pair.  That value can be remapped - See
	 * {@link PropertyExport#mapValue(Object)}.</em>
	 * <p>
	 * See {@link AndHow#export()} for export details and more examples.
	 * <p>
	 * @param nullValue The value to use for Properties that have a null value, since
	 *   {@link java.util.Properties} cannot contain null values.
	 * @return A {@link Collector} that creates a {@link java.util.Properties} with String
	 *   keys and Object values from a {@link PropertyExport} stream.
	 */
	public static ObjectProperties objectProperties(Object nullValue) {
		return new ObjectProperties(nullValue);
	}

	static class StringMap implements Collector<PropertyExport, Map<String, String>, Map<String, String>> {

		@Override public Supplier<Map<String, String>> supplier() {
			return HashMap::new;
		}

		@Override
		public BiConsumer<Map<String, String>, PropertyExport> accumulator() {
			return (map, exp) -> {
				List<String> eNames = exp.getExportNames();
				if (eNames != null && !eNames.isEmpty()) {
					eNames.stream().forEach(
							n -> map.put(n, exp.getValueAsString())
					);
				}
			};
		}

		@Override
		public BinaryOperator<Map<String, String>> combiner() {
			return (r1, r2) ->  { r1.putAll(r2); return r1; };
		}

		@Override
		public Function<Map<String, String>, Map<String, String>> finisher() {
			return Function.identity();
		}

		@Override public Set<Characteristics> characteristics() {
			return CH_CONCURRENT_ID;
		}
	}

	static class StringProperties implements Collector<PropertyExport, Properties, Properties> {

		final String nullValue;

		StringProperties(String nullValue) {
			this.nullValue = nullValue;
			if (nullValue == null) {
				throw new IllegalArgumentException("The nullValue for StringProperties cannot be null");
			}
		}

		@Override public Supplier<Properties> supplier() {
			return Properties::new;
		}

		@Override
		public BiConsumer<Properties, PropertyExport> accumulator() {
			return (map, exp) -> {
				List<String> eNames = exp.getExportNames();
				if (eNames != null && !eNames.isEmpty()) {
					eNames.stream().forEach(
							n -> map.put(n, exp.getValueAsString() != null ? exp.getValueAsString() : nullValue)
					);
				}
			};
		}

		@Override
		public BinaryOperator<Properties> combiner() {
			return (r1, r2) ->  { r1.putAll(r2); return r1; };
		}

		@Override
		public Function<Properties, Properties> finisher() {
			return Function.identity();
		}

		@Override public Set<Collector.Characteristics> characteristics() {
			return CH_CONCURRENT_ID;
		}
	}

	static class ObjectMap implements Collector<PropertyExport, Map<String, Object>, Map<String, Object>> {

		@Override public Supplier<Map<String, Object>> supplier() {
			return HashMap::new;
		}

		@Override
		public BiConsumer<Map<String, Object>, PropertyExport> accumulator() {
			return (map, exp) -> {
				List<String> eNames = exp.getExportNames();
				if (eNames != null && !eNames.isEmpty()) {
					eNames.stream().forEach(
							n -> map.put(n, exp.getValue())
					);
				}
			};
		}

		@Override
		public BinaryOperator<Map<String, Object>> combiner() {
			return (r1, r2) ->  { r1.putAll(r2); return r1; };
		}

		@Override
		public Function<Map<String, Object>, Map<String, Object>> finisher() {
			return Function.identity();
		}

		@Override public Set<Characteristics> characteristics() {
			return CH_CONCURRENT_ID;
		}
	}


	static class ObjectProperties extends StringProperties {

		final Object nullValue;

		ObjectProperties(Object nullValue) {
			super("");
			this.nullValue = nullValue;
			if (nullValue == null) {
				throw new IllegalArgumentException("The nullValue for ObjectProperties cannot be null");
			}
		}

		@Override
		public BiConsumer<Properties, PropertyExport> accumulator() {
			return (map, exp) -> {
				List<String> eNames = exp.getExportNames();
				if (eNames != null && !eNames.isEmpty()) {
					eNames.stream().forEach(
							n -> map.put(n, exp.getValue() != null ? exp.getValue() : nullValue)
					);
				}
			};
		}

	}


}
