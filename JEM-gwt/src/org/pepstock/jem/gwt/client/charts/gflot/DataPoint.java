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
package org.pepstock.jem.gwt.client.charts.gflot;


/**
 * Contains info for a generic chart data point
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <X> the X axis type
 * @param <Y> the Y axis type
 */
public final class DataPoint<X, Y> {

	private X x;
	private Y y;
	
	/**
	 * Build an empty datapoint
	 */
	public DataPoint() {
		this(null, null);
	}

	/**
	 * Build a datapoint with spedified values
	 * @param x the X axis value
	 * @param y the Y axis value
	 */
	public DataPoint(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the X axis value
	 */
	public X getX() {
		return x;
	}

	/**
	 * @param x the X axis value
	 */
	public void setX(X x) {
		this.x = x;
	}

	/**
	 * @return the Y axis value
	 */
	public Y getY() {
		return y;
	}

	/**
	 * @param y the Y axis value
	 */
	public void setY(Y y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "DataPoint [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DataPoint<?, ?> other = (DataPoint<?, ?>) obj;
		if (x == null) {
			if (other.x != null) {
				return false;
			}
		} else if (!x.equals(other.x)) {
			return false;
		}
		if (y == null) {
			if (other.y != null) {
				return false;
			}
		} else if (!y.equals(other.y)) {
			return false;
		}
		return true;
	}
}
