/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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

import java.util.ArrayList;
import java.util.List;

import com.googlecode.gflot.client.Axis;
import com.googlecode.gflot.client.options.TickFormatter;


/**
 * A commodity class that provide an horizontal Bar Chart widget used to display entities counts
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class CounterHBarChart extends BarChart {

	/**
	 * Build an empty chart
	 */
	public CounterHBarChart() {
		setHorizontal(true);
		setMinXTickSize(1L);
		setMinYTickSize(1L);
		setTickDecimalsX(0);
		setTickDecimalsY(0);
		setMinX(0L);
		setMinY(-1L);
	}

	/**
	 * Build the chart with provided values
	 * @param names the entity names
	 * @param values the entity values
	 * @param color the bar color
	 * @param valuesLabel the values axis label (can be null)
	 * @param namesLabel the names axis label (can be null)
	 */
	public void setCountData(String[] names, long[] values, String color, String valuesLabel, String namesLabel) {
		// check if input data are correct
		if (names.length != values.length) {
			throw new IllegalArgumentException("Names and Values must have the same size!");
		}
		// set the labels
		if (valuesLabel != null && !valuesLabel.trim().isEmpty()) {
			setLabelX(valuesLabel);
		}
		if (namesLabel != null && !namesLabel.trim().isEmpty()) {
			setLabelY(namesLabel);
		}
		// set Y axis tick formatter
		setTickFormatterY(new NamesTickFormatter(names));
		// set Y axis max value
		setMaxY((long)names.length);
		// build dataponts
		List<SeriesData<Double, Double>> chartData = new ArrayList<SeriesData<Double,Double>>(1);
		SeriesData<Double, Double> series = new SeriesData<Double, Double>();
		// set the bars color
		series.setColor(color);
		List<DataPoint<Double, Double>> dataPoints = new ArrayList<DataPoint<Double,Double>>();
		// for each value, build the datapoint
		for (int i=0; i<values.length; i++) {
			// X is the entries count, Y is the name index
			DataPoint<Double, Double> dataPoint = new DataPoint<Double, Double>((double)values[i], (double)i);
			dataPoints.add(dataPoint);
		}
		series.setDataPoints(dataPoints);
		chartData.add(series);
		super.setData(chartData);
	}

	/**
	 * Default tickformatter for Names axis
	 * @author Marco "Fuzzo" Cuccato
	 *
	 */
	private static class NamesTickFormatter implements TickFormatter {

		public static final String DOTH = ".";
		private String[] names;
		
		/**
		 * Build the formatter
		 * @param names the names
		 */
		public NamesTickFormatter(String[] names) {
			// normalize the names
			this.names = new String[names.length];
			for (int i=0; i<names.length; i++) {
				int lastDothIndex = names[i].lastIndexOf(DOTH); 
				String newName = names[i];
				if (lastDothIndex > -1) {
					newName = names[i].substring(lastDothIndex+1);
				}
				this.names[i] = newName;
			}
		}
		
		@Override
		public String formatTickValue(double tickValue, Axis axis) {
			String tickLabel;
			int tickIndex = (int)tickValue;
			if (tickIndex > -1 && tickIndex<names.length) {
				tickLabel = names[tickIndex];
			} else {
				tickLabel = "";
			}
			return tickLabel;
		}
		
	}
	
}
