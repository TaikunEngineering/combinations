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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("AssignmentToNull")
public class RelationFilter implements TupleSupplier {

	final ArrayList<Tuple> data;
	final Tuple[] relations;

	/**
	 * <p>This class takes a {@link TupleSupplier} and produces a subset of the supplier's output such that every unique
	 * sub-tuple as specified by the relation-tuples will be contained in that subset.</p>
	 *
	 * <p>Or more simply, you can think of this as taking a stream of permutations from a Combinator and filters it so
	 * that it's effective for pair-wise testing (where each pair relation, like (0, 3) denotes (A, D) in ABCDE, and where
	 * all sub-tuples from the combinator stream are represented... BUT more generic in that it will check any kind of
	 * relation, not just pairs).</p>
	 *
	 * <p>Relations are tuples of integers that specify the relations. You can also supply a {@code Stream<Tuple>} (like
	 * those produced by {@link RelationFilter#ALL_PAIRS(int)}</p>
	 *
	 * <h3>USAGE WARNINGS</h3>
	 *
	 * <p>The constructor completely generates and stores the supplier's output</p>
	 *
	 * <p>The total number of supplier outputs is limited to 2^31-1 (will crash if you attempt to exceed)</p>
	 *
	 * <p>No relation optimization is performed. Specifying ALL_PAIRS and ALL_TRIPLES will just cause things to take
	 * longer and use more memory.</p>
	 *
	 * <p>Lots of parameters (wide tuple) -> Lots of relations that each have their own witness sets<br />
	 * Lots of options for parameters -> (Very) large witness sets for affected relations</p>
	 *
	 * <p>As hinted at above, this class does not algorithmically construct a minimal, covering subset. It randomly
	 * iterates through the entire space. Expect the returned subset to be 1.5-5x larger than the theoretical minimum.</p>
	 *
	 * <h4>Simple example</h4>
	 *
	 * <pre>
	 * new RelationFilter(
	 *     new Combinator().permuteOne(true, false).permuteOne(1, 2, 3).permuteOne('a', 'b', 'c', 'd'),
	 *     ALL_PAIRS(3)
	 * ).get();
	 * </pre>
	 *
	 * <pre>
	 * [true, 2, a]
	 * [false, 1, a]
	 * [false, 1, b]
	 * [false, 1, c]
	 * [false, 1, d]
	 * [true, 2, c]
	 * [true, 3, b]
	 * [false, 2, d]
	 * [false, 3, a]
	 * [true, 2, b]
	 * [true, 3, c]
	 * [true, 1, c]
	 * [true, 3, d]
	 * </pre>
	 *
	 * <h5>Advanced example</h5>
	 *
	 * <pre>
	 * final Combinator advanced_comb = new Combinator()
	 *     .chooseOne(
	 *         t(true, false, false),
	 *         new Combinator()
	 *             .chooseOne(false)
	 *             .chooseOne(true, false)
	 *             .chooseOne(true, false)
	 *     )
	 *     .chooseOne(
	 *         t(true, false, false),
	 *         new Combinator()
	 *             .chooseOne(false)
	 *             .chooseOne(true, false)
	 *             .chooseOne(true, false)
	 *     )
	 *     .chooseOne(true, false);
	 *
	 * new RelationFilter(advanced_comb, t(0, 1, 2, 6), t(3, 4, 5, 6)).get();
	 * </pre>
	 *
	 * <pre>
	 * [false, true, true, true, false, false, true]
	 * [false, false, false, false, true, false, true]
	 * [false, false, true, true, false, false, true]
	 * [true, false, false, false, true, true, true]
	 * [false, false, false, false, false, true, true]
	 * [false, true, false, true, false, false, true]
	 * [false, false, true, false, true, false, false]
	 * [false, false, true, true, false, false, false]
	 * [false, false, false, false, true, false, false]
	 * [true, false, false, false, false, false, false]
	 * [false, true, true, false, false, true, false]
	 * [false, false, false, false, false, false, true]
	 * [true, false, false, false, true, true, false]
	 * [false, true, false, false, false, true, false]
	 * </pre>
	 *
	 * @param supplier Source of the tuples to be filtered
	 * @param relations Tuples of Integers or a Stream of such where each Tuple is one relation to be checked
	 */
	public RelationFilter(final TupleSupplier supplier, final Object... relations) {
		this.data = supplier.get().collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(this.data, new Random(52));

		this.relations = Arrays.stream(relations).flatMap(relation -> {
			if (relation instanceof Stream) {
				return (Stream<Tuple>) relation;
			} else {
				return Stream.of(relation);
			}
		}).toArray(Tuple[]::new);
	}

	@Override
	public Stream<Tuple> get() {
		final Acceptor acceptor = new Acceptor();
		return this.data.stream().filter(acceptor::accept);
	}

	private class Acceptor {

		HashSet[] sets;

		Acceptor() {
			this.sets = new HashSet[RelationFilter.this.relations.length];

			for (int i = 0; i < this.sets.length; i++) {
				this.sets[i] = new HashSet();
			}
		}

		boolean accept(final Tuple tuple) {

			boolean witnessed = true;

			for (int i = 0; i < this.sets.length; i++) {

				final Tuple relation = RelationFilter.this.relations[i];

				final Object[] subset = new Object[relation.o.length];

				for (int j = 0; j < relation.o.length; j++) {
					subset[j] = tuple.o[(Integer) relation.o[j]];
				}

				final Tuple subtuple = new Tuple(subset);

				if (this.sets[i].add(subtuple)) {
					witnessed = false;
				}
			}

			return !witnessed;
		}

	}

	/**
	 * @param n Dimensionality of search space
	 * @return A stream of all pairwise relations
	 */
	public static Stream<Tuple> ALL_PAIRS(final int n) {
		return ALL_K_SETS(n, 2);
	}

	/**
	 * @param n Dimensionality of search space
	 * @return A stream of all triplet-wise relations
	 */
	public static Stream<Tuple> ALL_TRIPLES(final int n) {
		return ALL_K_SETS(n, 3);
	}

	/**
	 * @param n Dimensionality of search space
	 * @return A stream of all quartet-wise relations
	 */
	public static Stream<Tuple> ALL_QUADS(final int n) {
		return ALL_K_SETS(n, 4);
	}

	/**
	 * @param n Dimensionality of search space
	 * @return A stream of all quintet-wise relations
	 */
	public static Stream<Tuple> ALL_QUINTS(final int n) {
		return ALL_K_SETS(n, 5);
	}

	/**
	 * @param n Dimensionality of search space
	 * @param k Dimensionality of subset space
	 * @return A stream of all k-wise relations
	 */
	public static Stream<Tuple> ALL_K_SETS(final int n, final int k) {
		return new Combinator().chooseR(k, IntStream.range(0, n).mapToObj(i -> i).toArray()).stream();
	}
}
