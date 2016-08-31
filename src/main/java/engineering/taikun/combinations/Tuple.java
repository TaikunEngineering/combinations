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

import java.util.Arrays;

/**
 * Basically an Object[]
 */
public class Tuple {
	public final Object[] o;

	Tuple(final Object... o) {
		this.o = o;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.o);
	}

	@Override
	public boolean equals(final Object o1) {
		if (this == o1)
			return true;
		if (!(o1 instanceof Tuple))
			return false;

		final Tuple tuple = (Tuple) o1;

		return Arrays.equals(this.o, tuple.o);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.o);
	}

	public static Tuple t(final Object... o) {
		return new Tuple(o);
	}

}
