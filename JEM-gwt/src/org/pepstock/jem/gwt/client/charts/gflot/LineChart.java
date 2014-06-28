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

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.event.PlotClickListener;
import com.googlecode.gflot.client.event.PlotHoverListener;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.GridOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.LegendOptions.LegendPosition;
import com.googlecode.gflot.client.options.LegendOptions.LegendSorting;
import com.googlecode.gflot.client.options.LineSeriesOptions;
import com.googlecode.gflot.client.options.PointsSeriesOptions;
import com.googlecode.gflot.client.options.PointsSeriesOptions.PointSymbol;

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
	
	private boolean showPoints = false;
	private PointSymbol pointSymbol;
	private Double pointRadious;
	private String pointHighlightColor;
	
	private PlotHoverListener hoverListener;
	private PlotClickListener clickListener;
	
	@Override
	protected void applyOptions() {
		// legend
		LegendOptions legendOptions = LegendOptions.create();
		legendOptions.setShow(isShowLegend());
		legendOptions.setBackgroundOpacity(legendBackgroundOpacity).setPosition(legendPosition);
		if (legendColumns != null) {
			legendOptions.setNumOfColumns(legendColumns);
		}
		if (legendSorting != null) {
			legendOptions.setSorted(legendSorting);
		}
		getOptions().setLegendOptions(legendOptions);

		// grid
		GridOptions gridOptions = GridOptions.create();
		gridOptions.setMargin(gridMargin);
		// make the plot hoverable/clickable (needed for point labels)
		gridOptions.setHoverable(true).setClickable(true);
		if (gridColor != null) {
			gridOptions.setColor(gridColor);
		}
		getOptions().setGridOptions(gridOptions);

		// point
		if (isShowPoints()) {
			GlobalSeriesOptions globalSeriesOptions = GlobalSeriesOptions.create();
			globalSeriesOptions.setLineSeriesOptions(LineSeriesOptions.create().setShow(true));
			
			if (hasPointHighlightColor()) {
				globalSeriesOptions.setHighlightColor(pointHighlightColor);
			}
			
			PointsSeriesOptions pointOptions = PointsSeriesOptions.create();
			pointOptions.setShow(true);
			if (hasPointSymbol()) {
				pointOptions.setSymbol(pointSymbol);
			}
			if (hasPointRadious()) {
				pointOptions.setRadius(pointRadious);
			}
			globalSeriesOptions.setPointsOptions(pointOptions);
			
			getOptions().setGlobalSeriesOptions(globalSeriesOptions);
		}
		
		// axis
		applyAxisOptions();
	}

	@Override
	public void setData(List<SeriesData<Double, Double>> data) {
		// reset model
		getModel().removeAllSeries();
		// save the data
		super.setData(data);
		// create model
		for (SeriesData<Double, Double> s : data) {
			// build the series
			Series series = Series.of(s.getLabel());
			if (s.hasColor()) {
				series.setColor(s.getColor());
			}
			series.setLineSeriesOptions(LineSeriesOptions.create().setFill(s.getFill()).setLineWidth(1d));
			// add series to model
			SeriesHandler sh = getModel().addSeries(series);
			// add all series datapoint
			for (DataPoint<Double, Double> dp : s.getDataPoints()) {
				sh.add(com.googlecode.gflot.client.DataPoint.of(dp.getX(), dp.getY()));
			}
		}
		redraw();
	}

	@Override
	public Widget asWidget() {
		Widget w = super.asWidget();
		
		// listeners (here because before plot is null)
		if (hasHoverListener()) {
			getPlot().addHoverListener(getHoverListener(), false);
		}
		if (hasClickListener()) {
			getPlot().addClickListener(getClickListener(), false);
		}
		
		return w;
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

	/**
	 * @return <code>true</code> if the points will be show, <code>false</code> otherwhise
	 */
	public boolean isShowPoints() {
		return showPoints;
	}

	/**
	 * @param showPoints set <code>true</code> if you want the points will be show, <code>false</code> otherwhise
	 */
	public void setShowPoints(boolean showPoints) {
		this.showPoints = showPoints;
	}

	/**
	 * @return the point symbol
	 */
	public PointSymbol getPointSymbol() {
		return pointSymbol;
	}

	/**
	 * @param pointSymbol set the point symbol
	 */
	public void setPointSymbol(PointSymbol pointSymbol) {
		this.pointSymbol = pointSymbol;
	}

	/**
	 * 
	 * @return <code>true</code> if the value <i>pointSymbol</i> is specified, <code>false</code> if not
	 */
	public boolean hasPointSymbol() {
		return pointSymbol != null;
	}
	
	/**
	 * @return the symbol radious
	 */
	public Double getPointRadious() {
		return pointRadious;
	}

	/**
	 * @param pointRadious set the point radious
	 */
	public void setPointRadious(Double pointRadious) {
		this.pointRadious = pointRadious;
	}

	/**
	 * @return <code>true</code> if the value <i>pointRadious</i> is specified, <code>false</code> if not
	 */
	public boolean hasPointRadious() {
		return pointRadious != null;
	}
	
	/**
	 * @return the color of an highlighted point
	 */
	public String getPointHighlightColor() {
		return pointHighlightColor;
	}

	/**
	 * @param pointHighlightColor set the color of an highlighted point
	 */
	public void setPointHighlightColor(String pointHighlightColor) {
		this.pointHighlightColor = pointHighlightColor;
	}
	
	/**
	 * @return <code>true</code> if the value <i>pointHighlightColor</i> is specified, <code>false</code> if not
	 */
	public boolean hasPointHighlightColor() {
		return pointHighlightColor != null;
	}

	/**
	 * @return the {@link PlotHoverListener}, or <code>null</code>
	 */
	public PlotHoverListener getHoverListener() {
		return hoverListener;
	}

	/**
	 * @param hoverListener set the {@link PlotHoverListener}
	 */
	public void setHoverListener(PlotHoverListener hoverListener) {
		this.hoverListener = hoverListener;
	}

	/**
	 * @return <code>true</code> if a {@link PlotHoverListener} is defined, <code>false</code> otherwhise
	 */
	public boolean hasHoverListener() {
		return hoverListener != null;
	}
	
	/**
	 * @return the {@link PlotClickListener}, or <code>null</code>
	 */
	public PlotClickListener getClickListener() {
		return clickListener;
	}

	/**
	 * 
	 * @param clickListener set the {@link PlotClickListener}
	 */
	public void setClickListener(PlotClickListener clickListener) {
		this.clickListener = clickListener;
	}

	/**
	 * @return <code>true</code> if a {@link PlotClickListener} is defined, <code>false</code> otherwhise
	 */
	public boolean hasClickListener() {
		return clickListener != null;
	}
}
