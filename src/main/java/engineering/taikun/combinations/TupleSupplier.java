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

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Each supplied stream must be unique and return the same sequence
 */
@FunctionalInterface public interface TupleSupplier extends Supplier<Stream<Tuple>> {

	/**
	 * <p>Compute the entire result and return it as two-dimensional array (tuples converted to arrays as well).</p>
	 *
	 * @return An Object[][] containing all of the resulting tuples
	 */
	default Object[][] array() {
		return get().map(t -> t.o).toArray(Object[][]::new);
	}
}
