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
import com.googlecode.gflot.client.options.GridOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.LegendOptions.LegendPosition;
import com.googlecode.gflot.client.options.LegendOptions.LegendSorting;
import com.googlecode.gflot.client.options.LineSeriesOptions;

/**
 * Provide a widget that show a Line Chart.
 * @author Marco "Fuzzo" Cuccato
 */
public class LineChart extends AbstractGridBasedChart {

	private Double legendBackgroundOpacity = 0d;
	private LegendPosition legendPosition = LegendPosition.NORTH_EAST;
	private Integer legendColumns;
	private LegendSorting legendSorting;
	
	private Double gridMargin = 5d;
	private String gridColor;
	
	@Override
	protected void applyOptions() {
		// legend
		LegendOptions legendOptions = LegendOptions.create();
		legendOptions.setShow(isShowLegend());
		legendOptions.setBackgroundOpacity(legendBackgroundOpacity).setPosition(legendPosition);
		if (legendColumns != null) legendOptions.setNumOfColumns(legendColumns);
		if (legendSorting != null) legendOptions.setSorted(legendSorting);
		getOptions().setLegendOptions(legendOptions);
		
		// grid
		GridOptions gridOptions = GridOptions.create();
		gridOptions.setMargin(gridMargin);
		if (gridColor != null) gridOptions.setColor(gridColor);
		getOptions().setGridOptions(gridOptions);
		
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
		for (SeriesData<Double, Double> s : data) {
			// build the series
			Series series = Series.of(s.getLabel());
			if (s.hasColor()) series.setColor(s.getColor());
			series.setLineSeriesOptions(LineSeriesOptions.create().setFill(s.getFill()));
			// add series to model
			SeriesHandler sh = getModel().addSeries(series);
			// add all series datapoint
			for (DataPoint<Double, Double> dp : s.getDataPoints()) {
				sh.add(com.googlecode.gflot.client.DataPoint.of(dp.getX(), dp.getY()));
			}
		}
	}

	/**
	 * @return the legend background opacity. Opacity range from 0.0 to 1.0.
	 */
	public Double getLegendBackgroundOpacity() {
		return legendBackgroundOpacity;
	}

	/**
	 * @param legendBackgroundOpacity set the legend background opacity. Opacity range from 0.0 to 1.0.
	 */
	public void setLegendBackgroundOpacity(Double legendBackgroundOpacity) {
		this.legendBackgroundOpacity = legendBackgroundOpacity;
	}

	/**
	 * @return the overall placement of the legend within the plot 
	 */
	public LegendPosition getLegendPosition() {
		return legendPosition;
	}

	/**
	 * @param legendPosition set the overall placement of the legend within the plot 
	 */
	public void setLegendPosition(LegendPosition legendPosition) {
		this.legendPosition = legendPosition;
	}

	/**
	 * @return the number of columns to divide the legend table into
	 */
	public Integer getLegendColumns() {
		return legendColumns;
	}

	/**
	 * @param legendColumns set the number of columns to divide the legend table into
	 */
	public void setLegendColumns(Integer legendColumns) {
		this.legendColumns = legendColumns;
	}

	/**
	 * @return the sorted option. Legend entries appear in the same order as their series by default
	 */
	public LegendSorting getLegendSorting() {
		return legendSorting;
	}

	/**
	 * @param legendSorting set the sorted option. Legend entries appear in the same order as their series by default
	 */
	public void setLegendSorting(LegendSorting legendSorting) {
		this.legendSorting = legendSorting;
	}

	/**
	 * @return the space in pixels between the canvas edge and the grid
	 */
	public Double getGridMargin() {
		return gridMargin;
	}

	/**
	 * @param gridMargin set the space in pixels between the canvas edge and the grid
	 */
	public void setGridMargin(Double gridMargin) {
		this.gridMargin = gridMargin;
	}

	/**
	 * @return the color of the grid itself
	 */
	public String getGridColor() {
		return gridColor;
	}

	/**
	 * @param gridColor set the color of the grid itself
	 */
	public void setGridColor(String gridColor) {
		this.gridColor = gridColor;
	}

}
