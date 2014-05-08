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
import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.HasSizes;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.PieDataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Background;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Formatter;
import com.googlecode.gflot.client.options.PlotOptions;

/**
 * Provide a widget that show a Pie Chart. 
 * @author Marco "Fuzzo" Cuccato
 */
public class PieChart implements IsWidget, HasSizes {

	protected static int DEFAULT_WIDTH = 300;
	protected static int DEFAULT_HEIGHT = 300;
	
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	
	private final PlotModel model = new PlotModel();
	private final PlotOptions options = PlotOptions.create();
	private SimplePlot plot = null;
	
	private double pieRadious = 1;
	private double pieInnerRadious = 0.2;
	
	private boolean showLegend = true;
	
	private boolean showLabel = true;
	private double labelRadious = 0.5;
	private double labelThreshold = 0.05;
	private Formatter labelFormatter = new DefaultLabelFormatter();
	private double labelBackgroundOpacity = 0.8;
	
	private List<DataPoint<String, Double>> data = new LinkedList<DataPoint<String, Double>>();
	
	/**
	 * Build an empty PieChart widget
	 */
	public PieChart() {
	}

	/**
	 * Build an PieChart widget 
	 * @param data the chart data
	 */
	public PieChart(List<DataPoint<String, Double>> data) {
		setData(data);
	}

	/**
	 * Build a PieChart widget
	 * @param width the widget width
	 * @param height the widget height
	 */
	public PieChart(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Build a PieChart widget
	 * @param data the chart data
	 * @param width the widget width
	 * @param height the widget height
	 */
	public PieChart(List<DataPoint<String, Double>> data, int width, int height) {
		setData(data);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Widget asWidget() {
		applyOptions();
		plot = new SimplePlot(model, options);
		applySizes();
		return plot;
	}

	protected void applyOptions() {
		// activate the pie
		options.setGlobalSeriesOptions(GlobalSeriesOptions.create().setPieSeriesOptions(
			PieSeriesOptions.create().setShow(true).setRadius(pieRadious).setInnerRadius(pieInnerRadious).setLabel(
				Label.create().setShow(showLabel).setRadius(labelRadious).setBackground(
						Background.create().setOpacity(labelBackgroundOpacity)).setThreshold(labelThreshold).setFormatter(labelFormatter)
					)));
		options.setLegendOptions(LegendOptions.create().setShow(showLegend));
		//options.setGridOptions(GridOptions.create().setHoverable(true));
	}
	
	protected void applySizes() {
		if (plot != null) {
			plot.setWidth(getWidth());
			plot.setHeight(getHeight());
		}
	}
	
	/**
	 * @return the chart data
	 */
	public List<DataPoint<String, Double>> getData() {
		return data;
	}

	/**
	 * Set the chart data
	 * @param data list of {@link DataPoint}
	 */
	public void setData(List<DataPoint<String, Double>> data) {
		// save the data
		this.data = data;
		// reset model
		model.removeAllSeries();
		// create model
		for (DataPoint<String, Double> dataPoint : data) {
			SeriesHandler seriesHandler; 
			if (dataPoint.hasColor()) {
				seriesHandler = model.addSeries(Series.of(dataPoint.getX(), dataPoint.getColor()));
			} else {
				seriesHandler = model.addSeries(Series.of(dataPoint.getX()));
			}
			seriesHandler.add(PieDataPoint.of(dataPoint.getY()));
		}
	}
	
	/**
	 * Clear all chart data
	 */
	public void clearData() {
		setData(new ArrayList<DataPoint<String,Double>>());
	}
	
	/**
	 * @return the pie radius. 0-1 for percentage of fullsize, or a specified pixel length
	 */
	public double getPieRadious() {
		return pieRadious;
	}

	/**
	 * Set the pie radious 
	 * @param pieRadious the pie radius. 0-1 for percentage of fullsize, or a specified pixel length
	 */
	public void setPieRadious(double pieRadious) {
		this.pieRadious = pieRadious;
	}

	/**
	 * @return the inner radius to create a donut effect. 0-1 for percentage of fullsize or a specified pixel length
	 */
	public double getPieInnerRadious() {
		return pieInnerRadious;
	}

	/**
	 * Set the inner pie radious
	 * @param pieInnerRadious the inner radius to create a donut effect. 0-1 for percentage of fullsize or a specified pixel length
	 */
	public void setPieInnerRadious(double pieInnerRadious) {
		this.pieInnerRadious = pieInnerRadious;
	}

	/**
	 * @return <code>true</code> if the legent be displayed, <code>false</code> otherwise
	 */
	public boolean isShowLegend() {
		return showLegend;
	}

	/**
	 * Set if the legeng will be displayed
	 * @param showLegend <code>true</code> if you want the legend will be displayed, <code>false</code> otherwise
	 */
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	/**
	 * @return <code>true</code> if you want a label for pie series will be displayed, <code>false</code> otherwise
	 */
	public boolean isShowLabel() {
		return showLabel;
	}

	/**
	 * Set if the pie series label will be displayed
	 * @param showLabel <code>true</code> if you want a label for pie series will be displayed, <code>false</code> otherwise
	 */
	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	/**
	 * @return the label radius. 0-1 for percentage of fullsize, or a specified pixel length
	 */
	public double getLabelRadious() {
		return labelRadious;
	}

	/**
	 * Set the label radious
	 * @param labelRadious the label radius. 0-1 for percentage of fullsize, or a specified pixel length
	 */
	public void setLabelRadious(double labelRadious) {
		this.labelRadious = labelRadious;
	}

	/**
	 * @return the threshold. 0-1 for the percentage value at which to hide labels (if they're too small)
	 */
	public double getLabelThreshold() {
		return labelThreshold;
	}

	/**
	 * Set the label threshold
	 * @param labelThreshold the threshold. 0-1 for the percentage value at which to hide labels (if they're too small)
	 */
	public void setLabelThreshold(double labelThreshold) {
		this.labelThreshold = labelThreshold;
	}

	/**
	 * @return the label {@link Formatter} class which format the series label
	 */
	public Formatter getLabelFormatter() {
		return labelFormatter;
	}

	/**
	 * Set the label {@link Formatter} class
	 * @param labelFormatter the label {@link Formatter} class which format the series label
	 */
	public void setLabelFormatter(Formatter labelFormatter) {
		this.labelFormatter = labelFormatter;
	}

	/**
	 * @return the label background opacity
	 */
	public double getLabelBackgroundOpacity() {
		return labelBackgroundOpacity;
	}

	/**
	 * Set the label background opacity
	 * @param labelBackgroundOpacity the label background opacity
	 */
	public void setLabelBackgroundOpacity(double labelBackgroundOpacity) {
		this.labelBackgroundOpacity = labelBackgroundOpacity;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
		applySizes();
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
		applySizes();
	}

	/**
	 * Default pie label formatter, that is 'value (percent%)'  
	 * @author Marco "Fuzzo" Cuccato
	 */
	private static class DefaultLabelFormatter implements Formatter {

		private static final NumberFormat FORMAT = NumberFormat.getFormat("#0.#");
		
		@Override
		public String format(String label, Series series) {
			return FORMAT.format(series.getData().getY(0)) + " (" + FORMAT.format(series.getPercent()) + "%)";
		}
		
	}

}
