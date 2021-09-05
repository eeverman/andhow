package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Property;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ExportCollector {

	//Copied from Collectors:  OK to run in parallel, order not important, finisher is just identity.
	static final Set<Collector.Characteristics> CH_CONCURRENT_ID
			= Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
			Collector.Characteristics.UNORDERED,
			Collector.Characteristics.IDENTITY_FINISH));

	public static StringMap stringMap() {
		return new StringMap();
	}

	public static StringProperties stringProperties() {
		return new StringProperties();
	}

	public static ObjectMap objectMap() {
		return new ObjectMap();
	}

	public static ObjectProperties objectProperties() {
		return new ObjectProperties();
	}

	private static class StringMap implements Collector<PropertyExport, Map<String, String>, Map<String, String>> {

		@Override public Supplier<Map<String, String>> supplier() {
			return HashMap::new;
		}

		@Override
		public BiConsumer<Map<String, String>, PropertyExport> accumulator() {
			return (map, exp) -> {
				exp.getExportNames().stream().forEach(
						n -> map.put(n, exp.getValueAsString()));
			};
		}

		@Override
		public BinaryOperator<Map<String, String>> combiner() {
			return (r1, r2) ->  { r1.putAll(r2); return r1; };
		}

		@Override
		public Function<Map<String, String>, Map<String, String>> finisher() {
			return i -> (Map<String, String>) i;
		}

		@Override public Set<Characteristics> characteristics() {
			return CH_CONCURRENT_ID;
		}
	}

	private static class StringProperties implements Collector<PropertyExport, Properties, Properties> {

		@Override public Supplier<Properties> supplier() {
			return Properties::new;
		}

		@Override
		public BiConsumer<Properties, PropertyExport> accumulator() {
			return (map, exp) -> {
				exp.getExportNames().stream().forEach(
						n -> map.put(n, exp.getValueAsString()));
			};
		}

		@Override
		public BinaryOperator<Properties> combiner() {
			return (r1, r2) ->  { r1.putAll(r2); return r1; };
		}

		@Override
		public Function<Properties, Properties> finisher() {
			return i -> (Properties) i;
		}

		@Override public Set<Collector.Characteristics> characteristics() {
			return CH_CONCURRENT_ID;
		}
	}

	private static class ObjectMap implements Collector<PropertyExport, Map<String, Object>, Map<String, Object>> {

		@Override public Supplier<Map<String, Object>> supplier() {
			return HashMap::new;
		}

		@Override
		public BiConsumer<Map<String, Object>, PropertyExport> accumulator() {
			return (map, exp) -> {
				exp.getExportNames().stream().forEach(
						n -> map.put(n, exp.getValue()));
			};
		}

		@Override
		public BinaryOperator<Map<String, Object>> combiner() {
			return (r1, r2) ->  { r1.putAll(r2); return r1; };
		}

		@Override
		public Function<Map<String, Object>, Map<String, Object>> finisher() {
			return i -> (Map<String, Object>) i;
		}

		@Override public Set<Characteristics> characteristics() {
			return CH_CONCURRENT_ID;
		}
	}


	private static class ObjectProperties extends StringProperties {

		@Override
		public BiConsumer<Properties, PropertyExport> accumulator() {
			return (map, exp) -> {
				exp.getExportNames().stream().forEach(
						n -> map.put(n, exp.getValue()));
			};
		}

	}


}
