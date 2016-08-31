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

import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static engineering.taikun.combinations.Tuple.t;

@SuppressWarnings("ALL")
public class CombinatorTest {

	@Test
	public static void main() {

		final Stream<Tuple> simple = new Combinator().chooseTwo('a', 'b', 'c').permuteTwo(1, 2, 3).stream();

		assert_(simple.collect(Collectors.toList()).toString().equals(
				'[' +
						"[a, b, 1, 2], [a, b, 1, 3], [a, b, 2, 1], [a, b, 2, 3], [a, b, 3, 1], [a, b, 3, 2], " +
						"[a, c, 1, 2], [a, c, 1, 3], [a, c, 2, 1], [a, c, 2, 3], [a, c, 3, 1], [a, c, 3, 2], " +
						"[b, c, 1, 2], [b, c, 1, 3], [b, c, 2, 1], [b, c, 2, 3], [b, c, 3, 1], [b, c, 3, 2]" +
				']'
		));

		final Stream<Tuple> advanced = new Combinator().permuteThree(
				t('!', '*'),
				new Combinator().chooseTwo('x', 'y', 'z')
		).stream();

		assert_(advanced.collect(Collectors.toList()).toString().equals(
				'[' +
						"[!, *, x, y, x, z], [!, *, x, y, y, z], [!, *, x, z, x, y], " +
						"[!, *, x, z, y, z], [!, *, y, z, x, y], [!, *, y, z, x, z], " +

						"[x, y, !, *, x, z], [x, y, !, *, y, z], [x, y, x, z, !, *], " +
						"[x, y, x, z, y, z], [x, y, y, z, !, *], [x, y, y, z, x, z], " +

						"[x, z, !, *, x, y], [x, z, !, *, y, z], [x, z, x, y, !, *], " +
						"[x, z, x, y, y, z], [x, z, y, z, !, *], [x, z, y, z, x, y], " +

						"[y, z, !, *, x, y], [y, z, !, *, x, z], [y, z, x, y, !, *], " +
						"[y, z, x, y, x, z], [y, z, x, z, !, *], [y, z, x, z, x, y]" +
				']'
		));

		System.out.println("passed");
	}

	public static void assert_(final boolean bool) {
		if (!bool) throw new RuntimeException();
	}
}
