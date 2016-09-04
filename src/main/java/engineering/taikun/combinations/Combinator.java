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
import java.util.function.Supplier;
import java.util.stream.Stream;

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

	final ArrayList<Factor> factors = new ArrayList<>();

	/**
	 * <p>Choose one object from the input.</p>
	 *
	 * <p>Multiplies the number of total results by N, where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseOne(final Object... o) {
		this.factors.add(new Factor(true, 1, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Choose two objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-2)!2!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseTwo(final Object... o) {
		this.factors.add(new Factor(true, 2, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Choose three objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-3)!3!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseThree(final Object... o) {
		this.factors.add(new Factor(true, 3, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Choose four objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-4)!4!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseFour(final Object... o) {
		this.factors.add(new Factor(true, 4, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Choose five objects from the supplied pool. Tuples that contain the same elements as a tuple returned before,
	 * but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-5)!5!), where N is the count of the input.</p>
	 *
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseFive(final Object... o) {
		this.factors.add(new Factor(true, 5, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Choose <tt>r</tt> objects from the supplied pool. Tuples that contain the same elements as a tuple returned
	 * before, but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-R)!R!), where N is the count of the input and R is the count
	 * chosen.</p>
	 *
	 * @param r The number of objects chosen
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseR(final int r, final Object... o) {
		this.factors.add(new Factor(true, r, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Choose <tt>r</tt> objects from the supplied pool. Tuples that contain the same elements as a tuple returned
	 * before, but in a different order, are ignored.</p>
	 *
	 * <p>Multiplies the number of total results by N!/((N-R)!R!), where N is the count of the input and R is the count
	 * chosen.</p>
	 *
	 * @param r The number of objects chosen
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator chooseR(final int r, final List<Object> o) {
		this.factors.add(new Factor(true, r, () -> 0, () -> Collections.EMPTY_SET, o));
		return this;
	}

	/**
	 * <p>Select one object from the input.</p>
	 *
	 * <p>Multiplies the number of total results by N, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modifed Combinator
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
	 * @return The modifed Combinator
	 */
	public Combinator permuteTwo(final Object... o) {
		this.factors.add(new Factor(false, 2, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Select three objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-3)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modifed Combinator
	 */
	public Combinator permuteThree(final Object... o) {
		this.factors.add(new Factor(false, 3, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Select four objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-4)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modifed Combinator
	 */
	public Combinator permuteFour(final Object... o) {
		this.factors.add(new Factor(false, 4, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Select five objects from the input. Tuples may contain the same elements as a tuple returned before, but the
	 * ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-5)!, where N is the count of the input.</p>
	 *
	 * @param o The pool to select from
	 * @return The modifed Combinator
	 */
	public Combinator permuteFive(final Object... o) {
		this.factors.add(new Factor(false, 5, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Select <tt>r</tt> objects from the input. Tuples may contain the same elements as a tuple returned before, but
	 * the ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-R)!, where N is the count of the input and R is the count
	 * selected.</p>
	 *
	 * @param r The number of objects selected
	 * @param o The pool to select from
	 * @return The modifed Combinator
	 */
	public Combinator permuteN(final int r, final Object... o) {
		this.factors.add(new Factor(false, r, () -> 0, () -> Collections.EMPTY_SET, Arrays.asList(o)));
		return this;
	}

	/**
	 * <p>Select <tt>r</tt> objects from the input. Tuples may contain the same elements as a tuple returned before, but
	 * the ordering will be different.</p>
	 *
	 * <p>Multiplies the number of total results by N!/(N-R)!, where N is the count of the input and R is the count
	 * selected.</p>
	 *
	 * @param r The number of objects selected
	 * @param o The pool to choose from
	 * @return The modifed Combinator
	 */
	public Combinator permuteN(final int r, final List<Object> o) {
		this.factors.add(new Factor(false, r, () -> 0, () -> Collections.EMPTY_SET, o));
		return this;
	}

	/**
	 * <p>Iteratively, lazily, return all tuples as dictated by the layering of combinations and permutations.</p>
	 *
	 * @return A lazily constructed stream of the resulting tuples
	 */
	public Stream<Tuple> stream() {

		Stream stream = Stream.of(t());

		for (final Factor factor : this.factors) {
			stream = pump(stream, factor);
		}

		return stream;
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
		if (factor.count == 1) {

			return stream.flatMap(tuple -> factorToStream(factor, true).map(o -> {

				final Object[] temp = Arrays.copyOf(tuple.o, tuple.o.length + o.o.length);
				System.arraycopy(o.o, 0, temp, tuple.o.length, o.o.length);
				return t(temp);

			}));

		} else if (factor.combine) {

			return stream.flatMap(tuple -> {

				final int[] count = { 0 };

				final Stream<Tuple> stream2 = factorToStream(factor, true).map(o -> {

					count[0]++;

					final Object[] temp = Arrays.copyOf(tuple.o, tuple.o.length + o.o.length);
					System.arraycopy(o.o, 0, temp, tuple.o.length, o.o.length);
					return t(temp);

				});

				return pump(stream2, new Factor(
						true, factor.count - 1, () -> factor.removed.get() + count[0], factor.black_indexes, factor.objects
				));

			});

		} else {

			return stream.flatMap(tuple -> {

				final int[] count = { -1 };

				final Stream<Tuple> stream2 = factorToStream(factor, false).flatMap(o -> {

					count[0]++;

					if (o == PLACEHOLDER) {
						return (Stream<Tuple>) (Object) Stream.empty();
					}

					final Object[] temp = Arrays.copyOf(tuple.o, tuple.o.length + o.o.length);
					System.arraycopy(o.o, 0, temp, tuple.o.length, o.o.length);
					return Stream.of(t(temp));

				});

				return pump(stream2, new Factor(false, factor.count - 1, factor.removed, () -> {
					final HashSet<Integer> black_copy = new HashSet<>(factor.black_indexes.get());
					black_copy.add(count[0]);
					return black_copy;
				}, factor.objects
				));

			});

		}
	}

	private static Stream<Tuple> factorToStream(final Factor factor, final boolean skip_empty) {
		final int[] count = { 0 };
		final Set<Integer> black_indexes = factor.black_indexes.get();

		count[0] = 0;

		return factor.objects.stream().flatMap(o -> {
			if (o instanceof TupleSupplier) {
				return ((TupleSupplier) o).get();
			} else if (o instanceof Tuple) {
				return Stream.of((Tuple) o);
			} else {
				return Stream.of(t(o));
			}
		}).skip(factor.removed.get()).flatMap(o -> {
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

	static class Factor {
		final boolean combine;
		final int count;
		final Supplier<Integer> removed;
		final Supplier<Set<Integer>> black_indexes;
		final List<Object> objects;

		Factor(
				final boolean combine, final int count, final Supplier<Integer> removed,
				final Supplier<Set<Integer>> black_indexes, final List<Object> objects
		) {
			this.combine = combine;
			this.count = count;
			this.removed = removed;
			this.black_indexes = black_indexes;
			this.objects = objects;
		}
	}
}
