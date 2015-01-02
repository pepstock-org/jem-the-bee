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

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.HasSizes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.PlotOptions;

/**
 * Contains common chart things.
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <X> the X axis value type
 * @param <Y> the Y axis value type
 */
public abstract class AbstractChart<X, Y> implements IsWidget, HasSizes {

	/**
	 * Chart panel default width, in pixel
	 */
	public static final int DEFAULT_WIDTH = 300;
	
	/**
	 * Chart panel default height, in pixel 
	 */
	public static final int DEFAULT_HEIGHT = 300;
	
	private SimplePlot plot = null;
	private final PlotModel model = new PlotModel();
	private final PlotOptions options = PlotOptions.create();

	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	
	private boolean showLegend = true;
	
	private List<SeriesData<X, Y>> data = new LinkedList<SeriesData<X, Y>>();

	/**
	 * Build an AbstractChart widget
	 */
	public AbstractChart() {
	}
	
	/**
	 * Build the chart widget with given sizes
	 * @param width the chart widget width, in pixel
	 * @param height the chart widget height, in pixel
	 */
	public AbstractChart(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Apply the chart options to underlying implementation
	 */
	protected abstract void applyOptions();

	@Override
	public Widget asWidget() {
		applyOptions();
		plot = new SimplePlot(model, options);
		applySizes();
		return plot;
	}

	/**
	 * Apply the sizes to chart panel, use setWidth/setHeight before 
	 */
	protected void applySizes() {
		if (plot != null) {
			plot.setWidth(getWidth());
			plot.setHeight(getHeight());
		}
	}

	/**
	 * @return the chart data
	 */
	public List<SeriesData<X, Y>> getData() {
		return data;
	}

	/**
	 * Set the chart data. This method is intended to be called as super.setData() in an implementation. 
	 * @param data the chart data
	 */
	public void setData(List<SeriesData<X, Y>> data) {
		this.data = data;
	}

	/**
	 * Redraw the plot
	 */
	public void redraw() {
		if (plot != null) {
			plot.redraw();
		}
	}
	
	/**
	 * @return <code>true</code> if the legend will be displayed, <code>false</code> otherwise
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
	 * @return the chart plot
	 */
	public SimplePlot getPlot() {
		return plot;
	}

	/**
	 * @return the chart model, which contains chart data
	 */
	public PlotModel getModel() {
		return model;
	}

	/**
	 * @return the chart options, which contains parametrs used to draw the chart
	 */
	public PlotOptions getOptions() {
		return options;
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

}
