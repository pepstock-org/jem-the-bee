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
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Common time series chart.
 * @author Andrea "Stock" Stocchero
 * @param <T> 
 * 
 */
public abstract class AbstractTimeSeriesChart<T> extends AbstractChart<T> {

	/**
	 * Creates chart using property access
	 * @param propertyAccess property access instance
	 */
	public AbstractTimeSeriesChart(DataPropertyAccess<T> propertyAccess) {
		super(propertyAccess);
	}
	
	/**
	 * Returns the sprite to put on time series
	 * @return the sprite to put on time series
	 */
	public abstract Sprite getSprite();
	
	/**
	 * Overrides the Axis creation
	 */
	@Override
	NumericAxis<T> createNumericAxis() {
		NumericAxis<T> axis = super.createNumericAxis();
		axis.setPosition(Position.LEFT);
		PathSprite odd = new PathSprite();
		odd.setOpacity(1);
		odd.setFill(new Color("#ddd"));
		odd.setStroke(new Color("#bbb"));
		odd.setStrokeWidth(0.5);
		axis.setGridOddConfig(odd);
		return axis;
	}
	
	/**
	 * Overrides the Category creation
	 */
	@Override
	CategoryAxis<T, String> createCategoryAxis() {
		CategoryAxis<T, String> catAxis = super.createCategoryAxis();
		catAxis.setPosition(Position.BOTTOM);
		return catAxis;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#createChart()
	 */
    @Override
    public void createChart() {
		final LineSeries<T> series = new LineSeries<T>();
		series.setYAxisPosition(Position.LEFT);
		series.setYField(getAxisValues(getPropertyAccess()));
		
		ChartColor color = getColor();
		
		RGB rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
		
		series.setStroke(rgb);
		series.setShowMarkers(true);
		series.setSmooth(true);
		series.setFill(rgb);

		Sprite marker = getSprite();
		marker.setFill(rgb);
		series.setMarkerConfig(marker);

		series.setHighlighting(true);

	    final SeriesToolTipConfig<T> config = new SeriesToolTipConfig<T>();
	    config.setLabelProvider(new SeriesLabelProvider<T>() {
			
			@Override
			public String getLabel(T item, ValueProvider<? super T, ? extends Number> valueProvider) {
				return getProvidedLabel(item, valueProvider);
			}
		});
	    series.setToolTipConfig(config);
	
		getChart().addSeries(series);
	}
}