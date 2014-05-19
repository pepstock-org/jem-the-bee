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

import com.googlecode.gflot.client.Axis;
import com.googlecode.gflot.client.options.TickFormatter;

/**
 * Default tickformatter for Time axis
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class TimeTickFormatter implements TickFormatter {
	
	private String[] times;

	/**
	 * Build the formatter
	 * @param times the times
	 */
	public TimeTickFormatter(String[] times) {
		this.times = times;
	}

	@Override
	public String formatTickValue(double tickValue, Axis axis) {
		String tickLabel;
		int tickIndex = (int)tickValue;
		if (tickIndex > -1 && tickIndex<times.length) {
			tickLabel = times[tickIndex];
		} else {
			tickLabel = "";
		}
		return tickLabel;
	}
}
