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

import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Common bar chart.
 * @author Andrea "Stock" Stocchero
 * @param <T> 
 * 
 */
public abstract class AbstractBarChart<T> extends AbstractChart<T> {

	/**
	 * Creates chart using property access
	 * @param propertyAccess property access instance
	 */
	public AbstractBarChart(DataPropertyAccess<T> propertyAccess) {
		super(propertyAccess);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#createChart()
	 */
    @Override
    public final void createChart() {
	    final BarSeries<T> bar = new BarSeries<T>();
	    bar.setYAxisPosition(Position.BOTTOM);
	    bar.addYField(getAxisValues(getPropertyAccess()));
	    
	    ChartColor color = getColor();
	    bar.addColor(new RGB(color.getRed(), color.getGreen(), color.getBlue()));
	    bar.setHighlighting(true);
	    getChart().addSeries(bar);
	    
	    final SeriesToolTipConfig<T> config = new SeriesToolTipConfig<T>();
	    config.setLabelProvider(new SeriesLabelProvider<T>() {
			
			@Override
			public String getLabel(T item, ValueProvider<? super T, ? extends Number> valueProvider) {
				return getProvidedLabel(item, valueProvider);
			}
		});
	    bar.setToolTipConfig(config);
	}
}