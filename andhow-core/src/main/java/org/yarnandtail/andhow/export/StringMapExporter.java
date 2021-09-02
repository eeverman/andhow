package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Property;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StringMapExporter
		implements Collector<PropertyExport, Map<String, String>, Map<String, String>> {

	//Copied from Collectors:  OK to run in parallel, order not important, finisher is just identity.
	static final Set<Collector.Characteristics> CH_CONCURRENT_ID
		= Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
		Collector.Characteristics.UNORDERED,
		Collector.Characteristics.IDENTITY_FINISH));

	@Override
	public Supplier<Map<String, String>> supplier() {
		return HashMap::new;
	}

	@Override
	public BiConsumer<Map<String, String>, PropertyExport> accumulator() {
		return (map, exp) -> {
			exp.getPreferedNames().stream().forEach(
				n -> map.put(n, exp.getValueAsString()));
		};
	}

	protected <T> String getValueAsString(Property<T> prop) {
		return prop.getValueType().toString(prop.getValue());
	}

	@Override
	public BinaryOperator<Map<String, String>> combiner() {
		return (r1, r2) ->  { r1.putAll(r2); return r1; };
	}

	@Override
	public Function<Map<String, String>, Map<String, String>> finisher() {
		return i -> (Map<String, String>) i;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return CH_CONCURRENT_ID;
	}
}
