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
import com.googlecode.gflot.client.options.TickFormatter;

/**
 * Provide a widget that show a Bar Chart. 
 * @author Marco "Fuzzo" Cuccato
 */
public class BarChart extends AbstractChart<Double, Double> {

	private double lineWidth = 1;
	private double barWidth = 0.75;
	private boolean horizontal = false;
	
	private Long minX, maxX, minXTickSize;
	private Long minY, maxY, minYTickSize;
	
	private String labelX, labelY;
	
	private Double tickDecimalsX, tickDecimalsY;
	
	private TickFormatter tickFormatterX, tickFormatterY; 
	
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
		getOptions().setGlobalSeriesOptions(GlobalSeriesOptions.create().setBarsSeriesOptions(
			BarSeriesOptions.create().setShow(true).setLineWidth(lineWidth).setBarWidth(barWidth).setAlignment(BarAlignment.CENTER)
			.setHorizontal(horizontal)
		));
		getOptions().setLegendOptions(LegendOptions.create().setShow(isShowLegend()));
		
		// axis X options
		AxisOptions optionsX = AxisOptions.create();
		if (minXTickSize != null) optionsX.setMinTickSize(minXTickSize);
		if (minX != null) optionsX.setMinimum(minX);
		if (maxX != null) optionsX.setMaximum(maxX);
		if (labelX != null) optionsX.setLabel(labelX);
		if (tickFormatterX != null) optionsX.setTickFormatter(tickFormatterX);
		if (tickDecimalsX != null) optionsX.setTickDecimals(tickDecimalsX);
		getOptions().addXAxisOptions(optionsX);
		
		// axis Y options
		AxisOptions optionsY = AxisOptions.create();
		if (minYTickSize != null) optionsY.setMinTickSize(minYTickSize);
		if (minY != null) optionsY.setMinimum(minY);
		if (maxY != null) optionsY.setMaximum(maxY);
		if (labelY != null) optionsY.setLabel(labelY);
		if (tickFormatterY != null) optionsY.setTickFormatter(tickFormatterY);
		if (tickDecimalsY != null) optionsY.setTickDecimals(tickDecimalsY);
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

	/**
	 * @return the minimum X value
	 */
	public Long getMinX() {
		return minX;
	}

	/**
	 * @param minX the minimum X value
	 */
	public void setMinX(Long minX) {
		this.minX = minX;
	}

	/**
	 * @return the maximum X value
	 */
	public Long getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX the maximum X value
	 */
	public void setMaxX(Long maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the minimum X axis scale step
	 */
	public Long getMinXTickSize() {
		return minXTickSize;
	}

	/**
	 * @param minXTickSize the minimum X axis scale step
	 */
	public void setMinXTickSize(Long minXTickSize) {
		this.minXTickSize = minXTickSize;
	}

	/**
	 * @return the minimum Y value
	 */
	public Long getMinY() {
		return minY;
	}

	/**
	 * @param minY the minimum Y value
	 */
	public void setMinY(Long minY) {
		this.minY = minY;
	}

	/**
	 * @return the maximum Y value
	 */
	public Long getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY  the maximum Y value
	 */
	public void setMaxY(Long maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return the minimum Y axis scale step
	 */
	public Long getMinYTickSize() {
		return minYTickSize;
	}

	/**
	 * @param minYTickSize the minimum Y axis scale step
	 */
	public void setMinYTickSize(Long minYTickSize) {
		this.minYTickSize = minYTickSize;
	}

	/**
	 * @return the X axis label
	 */
	public String getLabelX() {
		return labelX;
	}

	/**
	 * @param labelX the X axis label
	 */
	public void setLabelX(String labelX) {
		this.labelX = labelX;
	}

	/**
	 * @return the Y axis label
	 */
	public String getLabelY() {
		return labelY;
	}

	/**
	 * @param labelY the Y axis value
	 */
	public void setLabelY(String labelY) {
		this.labelY = labelY;
	}

	/**
	 * @return the X axis {@link TickFormatter}
	 */
	public TickFormatter getTickFormatterX() {
		return tickFormatterX;
	}

	/**
	 * Set the X axis {@link TickFormatter}. This class will be used as renderer of X axis ticks
	 * @param tickFormatterX
	 */
	public void setTickFormatterX(TickFormatter tickFormatterX) {
		this.tickFormatterX = tickFormatterX;
	}

	/**
	 * @return the Y axis {@link TickFormatter}
	 */
	public TickFormatter getTickFormatterY() {
		return tickFormatterY;
	}

	/**
	 * Set the Y axis {@link TickFormatter}. This class will be used as renderer of Y axis ticks
	 * @param tickFormatterY
	 */
	public void setTickFormatterY(TickFormatter tickFormatterY) {
		this.tickFormatterY = tickFormatterY;
	}

	/**
	 * @return the number of decimals rendered in X axis ticks
	 */
	public double getTickDecimalsX() {
		return tickDecimalsX;
	}

	/**
	 * @param tickDecimalsX the number of decimals rendered in X axis ticks. If a {@link TickFormatter} is provided, this is ignored.
	 */
	public void setTickDecimalsX(double tickDecimalsX) {
		this.tickDecimalsX = tickDecimalsX;
	}

	/**
	 * @return the number of decimals rendered in Y axis ticks
	 */
	public double getTickDecimalsY() {
		return tickDecimalsY;
	}

	/**
	 * @param tickDecimalsY the number of decimals rendered in Y axis ticks. If a {@link TickFormatter} is provided, this is ignored.
	 */
	public void setTickDecimalsY(double tickDecimalsY) {
		this.tickDecimalsY = tickDecimalsY;
	}

}
