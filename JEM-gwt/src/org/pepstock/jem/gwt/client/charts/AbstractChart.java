/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.charts;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * Abstarct component for chart (bar and time series)
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T> 
 * 
 */
public abstract class AbstractChart<T> implements IsWidget {

	private final Chart<T> chart = new Chart<T>();

	private final ListStore<T> store;
	
	private NumericAxis<T> axis = null;
	
	private int width = 0;
	
	private int height = 0;
	
	private final DataPropertyAccess<T> propertyAccess;
	
	private boolean loaded = false;
	
	private final SimpleContainer panel = new SimpleContainer();

	/**
	 * Constructs the object using the property access 
	 * @param propertyAccess property access instance
	 */
	public AbstractChart(DataPropertyAccess<T> propertyAccess) {
		this.propertyAccess = propertyAccess;
		
		store = new ListStore<T>(propertyAccess.nameKey());
		
		chart.setShadowChart(true);
		chart.setAnimated(true);
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
	public Chart<T> getChart() {
		return chart;
	}
	
	/**
	 * @return the propertyAccess
	 */
	public DataPropertyAccess<T> getPropertyAccess() {
		return propertyAccess;
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
	
	/**
	 * 
	 * @param data
	 */
	public void setData(List<T> data) {
		boolean redraw = store.size() == 0;
		if (redraw) {
			store.addAll(data);
		} else { 
			store.replaceAll(data);
		}
		chart.setStore(store);
		if (redraw) {
			axis = createNumericAxis();
			chart.addAxis(axis);
			chart.addAxis(createCategoryAxis());
			createChart();
			createLegend();
		}
		axis.setMaximum(getMaximum(store.getAll()));
		chart.redrawChart();
		setLoaded(true);
	}
	
	/**
	 * Redraw the chart
	 */
	public void redraw(){
		panel.setPixelSize(width, height);
		chart.redrawChart();
	}

	/**
	 * Gets the maximum value for scaling
	 * @param data all data
	 * @return maximum value
	 */
	public abstract int getMaximum(Collection<T> data);
	
	/**
	 * Returns the value key for axis
	 * @param propertyAccess property access
	 * @return the value key for axis
	 */
	public abstract ValueProvider<T, ? extends Number> getAxisValues(DataPropertyAccess<T> propertyAccess);
	
	/**
	 * Returns the axis title
	 * @return the axis title
	 */
	public abstract String getAxisTitle();
	
	/**
	 * Returns values for category
	 * @param propertyAccess property access
	 * @return values for category
	 */
	public abstract ValueProvider<T, String> getCategoryValues(DataPropertyAccess<T> propertyAccess);
	
	/**
	 * Returns value to show on tooltip
	 * @param item item of list data 
	 * @param valueProvider value provider
	 * @return string to show
	 */
	public abstract String getProvidedLabel(T item, ValueProvider<? super T, ? extends Number> valueProvider);
	
	/**
	 * Returns the category title
	 * @return the category title
	 */
	public abstract String getCategoryTitle();
	
	/**
	 * Returns teh label to show on line of axis
	 * @param item
	 * @return
	 */
	public abstract String getCategoryLabel(String item);
	
	/**
	 * Creates the chart
	 */
	public abstract void createChart();
	
	/**
	 * Returns the color of chart
	 * @return the color of chart
	 */
	public abstract ChartColor getColor();
	

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
	 */
    @Override
    public Widget asWidget() {
		panel.setPixelSize(width, height);

		VerticalLayoutContainer layout = new VerticalLayoutContainer();
		panel.add(layout);

		chart.setLayoutData(new VerticalLayoutData(1, 1));
		layout.add(chart);
		return panel;
	}

	/**
	 * 
	 */
	NumericAxis<T> createNumericAxis() {
		NumericAxis<T> localAxis = new NumericAxis<T>();
		localAxis.setPosition(Position.BOTTOM);
		localAxis.addField(getAxisValues(propertyAccess));

		TextSprite titleSprite = new TextSprite(getAxisTitle());
		titleSprite.setFontSize(12);
		localAxis.setTitleConfig(titleSprite);

		localAxis.setMinorTickSteps(1);
		localAxis.setDisplayGrid(true);
		localAxis.setMinimum(0);
		return localAxis;
	}

	/**
	 * 
	 */
	CategoryAxis<T, String> createCategoryAxis() {
		CategoryAxis<T, String> catAxis = new CategoryAxis<T, String>();
		catAxis.setPosition(Position.LEFT);
		catAxis.setField(getCategoryValues(propertyAccess));

		TextSprite title = new TextSprite(getCategoryTitle());
		title.setFontSize(12);
		catAxis.setTitleConfig(title);
		
		catAxis.setLabelProvider(new LabelProvider<String>() {
			@Override
			public String getLabel(String item) {
				return getCategoryLabel(item);
			}
		});
		return catAxis;
	}

	/**
	 * Creates the legend of chart
	 */
	void createLegend() {
		final Legend<T> legend = new Legend<T>();
		legend.setPosition(Position.RIGHT);
		chart.setLegend(legend);
	}

}