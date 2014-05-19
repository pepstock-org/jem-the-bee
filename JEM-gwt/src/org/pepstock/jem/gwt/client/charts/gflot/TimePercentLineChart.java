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

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.gwt.client.charts.gflot.listeners.PercentPlotHoverListener;


/**
 * A line chart that has timestamps on X axis and a percent in Y axis  
 * @author Marco "Fuzzo" Cuccato
 */
public class TimePercentLineChart extends TimeCountLineChart {

	/**
	 * Build the chart with the provided values
	 * @param times the times
	 * @param values the values
	 * @param color the line and fill color
	 * @param timesLabel the X axis label
	 * @param valuesLabel the Y axis label
	 */
	public void setTimeAndDatas(String[] times, double[] values, String color, String timesLabel, String valuesLabel) {
		// check if input data are correct
		if (times.length != values.length) {
			throw new IllegalArgumentException("Times and Values must have the same size!");
		}
		
		setHoverListener(new PercentPlotHoverListener());

		// set the labels
		if (timesLabel != null && !timesLabel.trim().isEmpty()) setLabelX(timesLabel);
		if (valuesLabel != null && !valuesLabel.trim().isEmpty()) setLabelY(valuesLabel);
		// set X axis tick formatter
		setTickFormatterX(new TimeTickFormatter(times));
		// set X axis min and max
		setMinX(0l);
		setMaxX((long)times.length-1);
		setMinXTickSize(1l);
		setTickSizeX(1d);
		// set Y axis min and max
		setMinY(0l);
		setMaxY(100l);
		setTickSizeY(10d);
		setTickDecimalsY(0d);
		
		List<SeriesData<Double, Double>> chartData = new ArrayList<SeriesData<Double,Double>>(1);
		SeriesData<Double, Double> series = new SeriesData<Double, Double>();
		// set the bars color and fill
		series.setColor(color);
		series.setFill(true);
		// build datapoints
		List<DataPoint<Double, Double>> dataPoints = new ArrayList<DataPoint<Double,Double>>();
		// for each value, build the datapoint
		for (int i=0; i<values.length; i++) {
			// X is the time, Y the count
			DataPoint<Double, Double> dataPoint = new DataPoint<Double, Double>((double)i, values[i]);
			dataPoints.add(dataPoint);
		}
		series.setDataPoints(dataPoints);
		chartData.add(series);
		super.setData(chartData);
	}

}
