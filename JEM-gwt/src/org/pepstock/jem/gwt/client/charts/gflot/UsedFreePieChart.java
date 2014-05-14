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

import org.pepstock.jem.gwt.client.ColorsHex;

import com.google.gwt.i18n.client.NumberFormat;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Formatter;

/**
 * A commodity class that provide a Pie Chart widget for used and free space 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class UsedFreePieChart extends PieChart {

	protected static final NumberFormat PERCENT_FORMAT = NumberFormat.getFormat("#.#"); 
	
	/**
	 * Build an empty pie
	 */
	public UsedFreePieChart() {
		setLabelFormatter(new Formatter() {
			@Override
			public String format(String label, Series series) {
				return PERCENT_FORMAT.format(series.getPercent()) + "%";
			}
		});
	}
	
	/**
	 * Build a pie with provided values
	 * @param usedBytes the used space
	 * @param freeBytes the free space
	 */
	public UsedFreePieChart(long usedBytes, long freeBytes) {
		this();
		setUsedFreeData(usedBytes, freeBytes);
	}

	/**
	 * Set the values of used and free space
	 * @param usedBytes the used space value
	 * @param freeBytes the free space value
	 */
	public void setUsedFreeData(long usedBytes, long freeBytes) {
		clearData();
		List<SeriesData<String, Double>> data = new ArrayList<SeriesData<String,Double>>();
		
		// used series
		List<DataPoint<String, Double>> usedDataPoints = new ArrayList<DataPoint<String,Double>>(1);
		SeriesData<String, Double> usedSeries = new SeriesData<String, Double>();
		usedSeries.setColor(ColorsHex.LIGHT_RED);
		DataPoint<String, Double> used = new DataPoint<String, Double>("Used", usedBytes/1024d);
		usedDataPoints.add(used);
		usedSeries.setDataPoints(usedDataPoints);
		
		// free series
		List<DataPoint<String, Double>> freeDataPoints = new ArrayList<DataPoint<String,Double>>(1);
		SeriesData<String, Double> freeSeries = new SeriesData<String, Double>();
		usedSeries.setColor(ColorsHex.LIGHT_BLUE);
		DataPoint<String, Double> free = new DataPoint<String, Double>("Free", usedBytes/1024d);
		freeDataPoints.add(free);
		freeSeries.setDataPoints(freeDataPoints);
		
		// add both series to data and set it
		data.add(usedSeries);
		data.add(freeSeries);
		setData(data);
	}

	@Override
	public void setData(List<SeriesData<String, Double>> data) {
		throw new UnsupportedOperationException("You have to call setUsedFreeData()!");
	}
	
}
