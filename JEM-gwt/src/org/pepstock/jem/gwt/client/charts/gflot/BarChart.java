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

import java.util.List;

import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.options.AxisOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions.BarAlignment;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.LegendOptions;

/**
 * Provide a widget that show a Bar Chart. 
 * @author Marco "Fuzzo" Cuccato
 */
public class BarChart extends AbstractGridBasedChart {

	private double lineWidth = 1;
	private double barWidth = 0.75;
	private boolean horizontal = false;
	
	/**
	 * Build an empty BarChart widget
	 */
	public BarChart() {
	}

	/**
	 * Build a BarChart widget
	 * @param width the widget width
	 * @param height the widget height
	 */
	public BarChart(int width, int height) {
		super(width, height);
	}

	protected void applyOptions() {
		// global
		getOptions().setGlobalSeriesOptions(GlobalSeriesOptions.create().setBarsSeriesOptions(
			BarSeriesOptions.create().setShow(true).setLineWidth(lineWidth).setBarWidth(barWidth).setAlignment(BarAlignment.CENTER)
			.setHorizontal(horizontal)
		));
		
		// legend
		getOptions().setLegendOptions(LegendOptions.create().setShow(isShowLegend()));
		
		// axis X options
		AxisOptions optionsX = AxisOptions.create();
		if (hasMinXTickSize()) optionsX.setMinTickSize(getMinXTickSize());
		if (hasMinX()) optionsX.setMinimum(getMinX());
		if (hasMaxX()) optionsX.setMaximum(getMaxX());
		if (hasLabelX()) optionsX.setLabel(getLabelX());
		if (hasTickFormatterX()) optionsX.setTickFormatter(getTickFormatterX());
		if (hasTickDecimalsX()) optionsX.setTickDecimals(getTickDecimalsX());
		if (hasTickSizeX()) optionsX.setTickSize(getTickSizeX());
		getOptions().addXAxisOptions(optionsX);
		
		// axis Y options
		AxisOptions optionsY = AxisOptions.create();
		if (hasMinYTickSize()) optionsY.setMinTickSize(getMinYTickSize());
		if (hasMinY()) optionsY.setMinimum(getMinY());
		if (hasMaxY()) optionsY.setMaximum(getMaxY());
		if (hasLabelY()) optionsY.setLabel(getLabelY());
		if (hasTickFormatterY()) optionsY.setTickFormatter(getTickFormatterY());
		if (hasTickDecimalsY()) optionsY.setTickDecimals(getTickDecimalsY());
		if (hasTickSizeY()) optionsY.setTickSize(getTickSizeY());
		getOptions().addYAxisOptions(optionsY);
	}

	@Override
	public void setData(List<SeriesData<Double, Double>> data) {
		// save the data
		super.setData(data);
		// reset model
		getModel().removeAllSeries();
		// create model
		int order = 0;
		for (SeriesData<Double, Double> s : data) {
			// for each series, create and add data; the order is the same as the list one
			SeriesHandler sh = getModel().addSeries(Series.of(s.getLabel()).setColor(s.getColor())
				.setBarsSeriesOptions(BarSeriesOptions.create().setOrder(order++))
			);
			// add all series datapoint
			for (DataPoint<Double, Double> dp : s.getDataPoints()) {
				sh.add(com.googlecode.gflot.client.DataPoint.of(dp.getX(), dp.getY()));
			}
		}
	}

	/**
	 * @return the line width
	 */
	public double getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param lineWidth the line width, 0 if you want hide
	 */
	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the bar width. Should be <= lineWidth
	 */
	public double getBarWidth() {
		return barWidth;
	}

	/**
	 * @param barWidth the bar width. Should be <= lineWidth
	 */
	public void setBarWidth(double barWidth) {
		this.barWidth = barWidth;
	}

	/**
	 * @return <code>true</code> if the bars are drawn horizontally, i.e. from the y axis instead of the x axis
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @param horizontal set <code>true</code> if you want the bars are drawn horizontally, i.e. from the y axis instead of the x axis
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

}
