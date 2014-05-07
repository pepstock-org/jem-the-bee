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

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.LegendOptions.LegendPosition;
import com.googlecode.gflot.client.options.PlotOptions;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * Abstarct component for chart
 * 
 * @author Marco "Cuc" Cuccato
 * @param <T> 
 * 
 */
public abstract class AbstractChart<T> implements IsWidget {

	private final SimpleContainer panel = new SimpleContainer();

	private SimplePlot plot = null;
	
	private PlotOptions options = null;
	
	private PlotModel model = null;

	private int width = 0;
	
	private int height = 0;
	
	private boolean loaded = false;

	private LinkedList<T> samples = null;
	
	/**
	 * 
	 */
	public AbstractChart() {
		options = PlotOptions.create();
	}
	
	/**
	 * 
	 * @param data
	 */
	public abstract void setData(List<T> data);
	
	/**
	 * Redraw the chart
	 */
	public void redraw(){
		panel.setPixelSize(width, height);
		plot.redraw();
	}

	/**
	 * Gets the maximum value for scaling
	 * @return maximum value
	 */
	public abstract int getMaximum();
	
	/**
	 * Returns the X axis title
	 * @return the X axis title
	 */
	public String getXAxisTitle() {
		return options.getXAxisOptions().getLabel();
	}
	
	/**
	 * Returns the Y axis title
	 * @return the Y axis title
	 */
	public String getYAxisTitle() {
		return options.getYAxisOptions().getLabel();
	}
	
	/**
	 * Creates the chart
	 */
	public void createChart() {
		plot = new SimplePlot(model, options);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
	 */
    @Override
    public Widget asWidget() {
		panel.setPixelSize(width, height);
		plot.setSize(Sizes.toString(width), Sizes.toString(height));
		panel.remove(plot);
		panel.add(plot);
		return panel;
	}

	/**
	 * Creates the legend of chart
	 */
	protected void createLegend() {
		options.setLegendOptions(LegendOptions.create().setBackgroundOpacity(0).setPosition(LegendPosition.NORTH_EAST));
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * @return the chart
	 */
	public SimplePlot getChart() {
		return plot;
	}
	
	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @param loaded the loaded to set
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

}