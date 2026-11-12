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

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.stream.Stream;

import static engineering.taikun.combinations.RelationFilter.ALL_PAIRS;
import static engineering.taikun.combinations.Tuple.t;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ALL")
public class RelationFilterTest {

	@Test
	public void testRelationFilter() {

		final Stream<Tuple> simple = new RelationFilter(
				new Combinator().permuteOne(true, false).permuteOne(1, 2, 3).permuteOne('a', 'b', 'c', 'd'),
				ALL_PAIRS(3)
		).get();

		final HashSet simple01 = new HashSet();
		final HashSet simple02 = new HashSet();
		final HashSet simple12 = new HashSet();

		simple.forEach(t -> {
			simple01.add(t(t.o[0], t.o[1]));
			simple02.add(t(t.o[0], t.o[2]));
			simple12.add(t(t.o[1], t.o[2]));
		});

		assertEquals(6, simple01.size());
		assertEquals(8, simple02.size());
		assertEquals(12, simple12.size());

		final Combinator advanced_comb = new Combinator()
				.chooseOne(
						t(true, false, false),
						new Combinator()
								.chooseOne(false)
								.chooseOne(true, false)
								.chooseOne(true, false)
				)
				.chooseOne(
						t(true, false, false),
						new Combinator()
								.chooseOne(false)
								.chooseOne(true, false)
								.chooseOne(true, false)
				)
				.chooseOne(true, false);

		final Stream<Tuple> advanced = new RelationFilter(advanced_comb, t(0, 1, 2, 6), t(3, 4, 5, 6)).get();

		final HashSet advanced0126 = new HashSet();
		final HashSet advanced3456 = new HashSet();

		advanced.forEach(t -> {
			advanced0126.add(t(t.o[0], t.o[1], t.o[2], t.o[6]));
			advanced3456.add(t(t.o[3], t.o[4], t.o[5], t.o[6]));
		});

		assertEquals(10, advanced0126.size());
		assertEquals(10, advanced3456.size());
	}
}
