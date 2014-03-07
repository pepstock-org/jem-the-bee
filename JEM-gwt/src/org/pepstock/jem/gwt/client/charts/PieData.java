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
package org.pepstock.jem.gwt.client.charts;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class PieData extends KeyData {

	private String time = null;
	
	private long value = 0L;
	
	private double percent = 0D;

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}



	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		this.value = value;
	}

	

	/**
	 * @return the percent
	 */
	public double getPercent() {
		return percent;
	}


	/**
	 * @param percent the percent to set
	 */
	public void setPercent(double percent) {
		this.percent = percent;
	}
}