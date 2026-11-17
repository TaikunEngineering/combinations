/*
 * Copyright (C) 2016 Philip Smith as Taikun Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package engineering.taikun.combinations;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static engineering.taikun.combinations.Tuple.t;

/**
 * <p>A class for generating combinations and permutations of input collections.</p>
 *
 * <p>Multiple length literals can be specified by wrapping them in a {@link Tuple}. The contents of the tuple will be
 * flattened into the returned tuples.</p>
 *
 * <p>Any instance of {@link TupleSupplier} will be expanded into the relative candidate space. That is, it is possible
 * to nest Combinators. If you implement your own TupleSupplier, be aware that it should return a new Stream each call.
 * </p>
 *
 * <p>Note: choose/permute chains do not create new Combinators, the base instance is mutated and returned instead.</p>
 *
 * <h4>Simple example</h4>
 *
 * <pre>
 * new Combinator().chooseTwo('a', 'b', 'c').permuteTwo(1, 2, 3).stream()
 * </pre>
 *
 * <pre>
 * [a, b, 1, 2], [a, b, 1, 3], [a, b, 2, 1], [a, b, 2, 3], [a, b, 3, 1], [a, b, 3, 2],
 * [a, c, 1, 2], [a, c, 1, 3], [a, c, 2, 1], [a, c, 2, 3], [a, c, 3, 1], [a, c, 3, 2],
 * [b, c, 1, 2], [b, c, 1, 3], [b, c, 2, 1], [b, c, 2, 3], [b, c, 3, 1], [b, c, 3, 2]
 * </pre>
 *
 * <h4>Advanced example</h4>
 *
 * <pre>
 * new Combinator().permuteThree(
 *     t('!', '*'),
 *     new Combinator().chooseTwo('x', 'y', 'z')
 * ).stream()
 * </pre>
 *
 * <pre>
 * [!, *, x, y, x, z], [!, *, x, y, y, z], [!, *, x, z, x, y],
 * [!, *, x, z, y, z], [!, *, y, z, x, y], [!, *, y, z, x, z],
 *
 * [x, y, !, *, x, z], [x, y, !, *, y, z], [x, y, x, z, !, *],
 * [x, y, x, z, y, z], [x, y, y, z, !, *], [x, y, y, z, x, z],
 *
 * [x, z, !, *, x, y], [x, z, !, *, y, z], [x, z, x, y, !, *],
 * [x, z, x, y, y, z], [x, z, y, z, !, *], [x, z, y, z, x, y],
 *
 * [y, z, !, *, x, y], [y, z, !, *, x, z], [y, z, x, y, !, *],
 * [y, z, x, y, x, z], [y, z, x, z, !, *], [y, z, x, z, x, y]
 * </pre>
 *
 */
public class Combinator implements TupleSupplier {

	private static final Object[] EMPTY_OBJ_AR = new Object[0];
	private static final Tuple PLACEHOLDER = t(EMPTY_OBJ_AR);
	private static final IntSupplier ZERO_SUPPLIER = () -> 0;
	private static final Supplier<Set<Integer>> EMPTY_SET_SUPPLIER = Set::of;

	private final ArrayList<Factor> factors = new ArrayList<>();

	private Combinator add_factor(final boolean combine, final int count, final List<?> objects) {
		if (count <= 0) {
			throw new IllegalArgumentException("count must be a positive integer: " + count);
		}

		this.factors.add(new Factor(combine, count, ZERO_SUPPLIER, EMPTY_SET_SUPPLIER, objects));
		return this;
	}

	/**
	 * <p>Choose one object from the input.</p>
	 *
	 * <p>Multiplies the number of total results by N, where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseOne(final Object... o) {
		return add_factor(true, 1, Arrays.asList(o));
	}

	/**
	 * <p>Choose two objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-2)!2!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseTwo(final Object... o) {
		return add_factor(true, 2, Arrays.asList(o));
	}

	/**
	 * <p>Choose three objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-3)!3!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseThree(final Object... o) {
		return add_factor(true, 3, Arrays.asList(o));
	}

	/**
	 * <p>Choose four objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-4)!4!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseFour(final Object... o) {
		return add_factor(true, 4, Arrays.asList(o));
	}

	/**
	 * <p>Choose five objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-5)!5!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseFive(final Object... o) {
		return add_factor(true, 5, Arrays.asList(o));
	}

	/**
	 * <p>Choose {@code r} objects from the supplied pool. Tuples that contain the same elements as a tuple returned
	 * before, but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-R)!R!), where N is the count of the input and R is the count
	 * chosen.</p>
	 *
	 * @param r The number of objects chosen
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseR(final int r, final Object... o) {
		return add_factor(true, r, Arrays.asList(o));
	}

	/**
	 * <p>Choose {@code r} objects from the supplied pool. Tuples that contain the same elements as a tuple returned
	 * before, but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-R)!R!), where N is the count of the input and R is the count
	 * chosen.</p>
	 *
	 * @param r The number of objects chosen
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator chooseR(final int r, final Collection<?> o) {
		return add_factor(true, r, new ArrayList<>(o));
	}

	/**
	 * <p>Select one object from the input.</p>
	 *
	 * <p>Multiplies the number of total results by N, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modified Combinator
	 */
	public Combinator permuteOne(final Object... o) {
		return chooseOne(o);
	}

	/**
	 * <p>Select two objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-2)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modified Combinator
	 */
	public Combinator permuteTwo(final Object... o) {
		return add_factor(false, 2, Arrays.asList(o));
	}

	/**
	 * <p>Select three objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-3)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modified Combinator
	 */
	public Combinator permuteThree(final Object... o) {
		return add_factor(false, 3, Arrays.asList(o));
	}

	/**
	 * <p>Select four objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-4)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modified Combinator
	 */
	public Combinator permuteFour(final Object... o) {
		return add_factor(false, 4, Arrays.asList(o));
	}

	/**
	 * <p>Select five objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-5)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modified Combinator
	 */
	public Combinator permuteFive(final Object... o) {
		return add_factor(false, 5, Arrays.asList(o));
	}

	/**
	 * <p>Select {@code r} objects from the input. Tuples may contain the same elements as a tuple returned before, but
	 * the ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-R)!, where N is the count of the input and R is the count
	 * selected.</p>
	 *
	 * @param r The number of objects selected
	 * @param o The pool to select from
	 * @return The modified Combinator
	 */
	public Combinator permuteN(final int r, final Object... o) {
		return add_factor(false, r, Arrays.asList(o));
	}

	/**
	 * <p>Select {@code r} objects from the input. Tuples may contain the same elements as a tuple returned before, but
	 * the ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-R)!, where N is the count of the input and R is the count
	 * selected.</p>
	 *
	 * @param r The number of objects selected
	 * @param o The pool to choose from
	 * @return The modified Combinator
	 */
	public Combinator permuteN(final int r, final Collection<?> o) {
		return add_factor(false, r, new ArrayList<>(o));
	}

	/**
	 * <p>Iteratively, lazily, return all tuples as dictated by the layering of combinations and permutations.</p>
	 *
	 * <p>Note: tuple generation is inherently sequential; the returned stream does not split for parallel execution.</p>
	 *
	 * @return A lazily constructed stream of the resulting tuples
	 */
	public Stream<Tuple> stream() {

		Stream<Tuple> stream = Stream.of(t());

		for (final Factor factor : this.factors) {
			stream = pump(stream, factor);
		}

		final Spliterator<Tuple> base = stream.spliterator();

		return StreamSupport.stream(new Spliterator<Tuple>() {
			@Override
			public boolean tryAdvance(final Consumer<? super Tuple> action) {
				return base.tryAdvance(action);
			}

			@Override
			public void forEachRemaining(final Consumer<? super Tuple> action) {
				base.forEachRemaining(action);
			}

			@Override
			public Spliterator<Tuple> trySplit() {
				return null;
			}

			@Override
			public long estimateSize() {
				return base.estimateSize();
			}

			@Override
			public int characteristics() {
				return base.characteristics() & ~Spliterator.SUBSIZED;
			}
		}, false).onClose(stream::close);
	}

	/**
	 * <p>Iteratively, lazily, return all tuples as dictated by the layering of combinations and permutations.</p>
	 *
	 * @return A lazily constructed stream of the resulting tuples
	 */
	@Override
	public Stream<Tuple> get() {
		return stream();
	}

	private static Stream<Tuple> pump(final Stream<Tuple> stream, final Factor factor) {
		if (factor.count() == 1) {

			return stream.flatMap(tuple -> factorToStream(factor, true).map(o -> {

				final Object[] temp = Arrays.copyOf(tuple.o(), tuple.o().length + o.o().length);
				System.arraycopy(o.o(), 0, temp, tuple.o().length, o.o().length);
				return t(temp);

			}));

		} else if (factor.combine()) {

			return stream.flatMap(tuple -> {

				final int[] count = { 0 };

				final Stream<Tuple> stream2 = factorToStream(factor, true).map(o -> {

					count[0]++;

					final Object[] temp = Arrays.copyOf(tuple.o(), tuple.o().length + o.o().length);
					System.arraycopy(o.o(), 0, temp, tuple.o().length, o.o().length);
					return t(temp);

				});

				return pump(stream2, new Factor(
						true, factor.count() - 1, () -> factor.removed().getAsInt() + count[0], factor.black_indexes(), factor.objects()
				));

			});

		} else {

			return stream.flatMap(tuple -> {

				final int[] count = { -1 };

				final Stream<Tuple> stream2 = factorToStream(factor, false).flatMap(o -> {

					count[0]++;

					if (o == PLACEHOLDER) {
						return Stream.empty();
					}

					final Object[] temp = Arrays.copyOf(tuple.o(), tuple.o().length + o.o().length);
					System.arraycopy(o.o(), 0, temp, tuple.o().length, o.o().length);
					return Stream.of(t(temp));

				});

				return pump(stream2, new Factor(false, factor.count() - 1, factor.removed(), () -> {
					final Set<Integer> black_copy = new HashSet<>(factor.black_indexes().get());
					black_copy.add(count[0]);
					return black_copy;
				}, factor.objects()
				));

			});

		}
	}

	private static Stream<Tuple> factorToStream(final Factor factor, final boolean skip_empty) {
		final int[] count = { 0 };
		final Set<Integer> black_indexes = factor.black_indexes().get();

		return factor.objects().stream().flatMap(o -> {
			if (o instanceof final TupleSupplier supplier) {
				return supplier.get();
			} else if (o instanceof final Tuple tuple) {
				return Stream.of(tuple);
			} else {
				return Stream.of(t(o));
			}
		}).skip(factor.removed().getAsInt()).flatMap(o -> {
			if (black_indexes.contains(count[0]++)) {
				if (skip_empty) {
					return Stream.empty();
				} else {
					return Stream.of(PLACEHOLDER);
				}
			}

			return Stream.of(o);
		});
	}

	record Factor(
			boolean combine,
			int count,
			IntSupplier removed,
			Supplier<Set<Integer>> black_indexes,
			List<?> objects
	) {
	}
}
