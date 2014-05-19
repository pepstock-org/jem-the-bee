
/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.gwt.client;

/**
 * Utility class
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class Toolbox {

	private Toolbox() {}
	
	/**
	 * Returns the greatest value present in {@code array}.
	 * 
	 * @param array a <i>nonempty</i> array of {@code long} values
	 * @return the value present in {@code array} that is greater than or equal to every other value in the array
	 * @throws IllegalArgumentException if {@code array} is empty
	 */
	public static long maxLong(long... array) {
		if (array.length < 1) {
			throw new IllegalArgumentException("Array must be not empty!");
		}
		long max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}
	
	/**
	 * Returns the lowest value present in {@code array}.
	 * 
	 * @param array a <i>nonempty</i> array of {@code long} values
	 * @return the value present in {@code array} that is lower than or equal to every other value in the array
	 * @throws IllegalArgumentException if {@code array} is empty
	 */
	public static long minLong(long... array) {
		if (array.length < 1) {
			throw new IllegalArgumentException("Array must be not empty!");
		}
		long min = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
			}
		}
		return min;
	}
	
	/**
	 * Returns the greatest value present in {@code array}.
	 * 
	 * @param array a <i>nonempty</i> array of {@code double} values
	 * @return the value present in {@code array} that is greater than or equal to every other value in the array
	 * @throws IllegalArgumentException if {@code array} is empty
	 */
	public static double maxDouble(double... array) {
		if (array.length < 1) {
			throw new IllegalArgumentException("Array must be not empty!");
		}
		double max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}
	
	/**
	 * Returns the lowest value present in {@code array}.
	 * 
	 * @param array a <i>nonempty</i> array of {@code double} values
	 * @return the value present in {@code array} that is lower than or equal to every other value in the array
	 * @throws IllegalArgumentException if {@code array} is empty
	 */
	public static double minDouble(double... array) {
		if (array.length < 1) {
			throw new IllegalArgumentException("Array must be not empty!");
		}
		double min = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
			}
		}
		return min;
	}

}
