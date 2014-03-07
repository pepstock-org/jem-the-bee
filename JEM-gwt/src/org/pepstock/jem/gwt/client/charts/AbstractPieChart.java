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
package org.pepstock.jem.gwt.client.charts;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.series.PieSeries;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextBaseline;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * Pie chart, used for GFSan dmemory
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class AbstractPieChart implements IsWidget {
	
	private static final PieDataPropertyAccess PROPERTY_ACCESS = GWT.create(PieDataPropertyAccess.class);

	private final Chart<PieData> chart = new Chart<PieData>();

	private final ListStore<PieData> store = new ListStore<PieData>(PROPERTY_ACCESS.nameKey());
	
	private static final RGB BLUE = new RGB(164, 175, 210);
	
	private static final RGB RED = RGB.RED;
	
	private int width = 0;
	
	private int height = 0;
	
	private final SimpleContainer panel = new SimpleContainer();


	/**
	 * Constructs object
	 */
	public AbstractPieChart() {
		chart.setShadowChart(true);
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
	 * 
	 * @param data
	 */
	public void setData(List<PieData> data) {
		boolean redraw = store.size() == 0;
		if (redraw) {
			store.addAll(data);
		} else { 
			store.replaceAll(data);
		}
		chart.setStore(store);
		if (redraw) {
			createPie();
			createLegend();
		}
		chart.redrawChart();
	}

	

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
	 * Returns value to show on tooltip
	 * @param item item of list data 
	 * @param valueProvider value provider
	 * @return string to show
	 */
	public abstract String getProvidedLabel(PieData item, ValueProvider<? super PieData, ? extends Number> valueProvider);

	/**
	 * Creates chart
	 */
	void createPie() {
	    final PieSeries<PieData> series = new PieSeries<PieData>();
	    series.setAngleField(PROPERTY_ACCESS.value());
	    series.setDonut(35);
	    series.addColor(BLUE);
	    series.addColor(RED);
	    TextSprite textConfig = new TextSprite();
	    textConfig.setFont("Arial");
	    textConfig.setTextBaseline(TextBaseline.MIDDLE);
	    textConfig.setFontSize(18);
	    textConfig.setTextAnchor(TextAnchor.MIDDLE);
	    textConfig.setZIndex(15);
	    SeriesLabelConfig<PieData> labelConfig = new SeriesLabelConfig<PieData>();
	    labelConfig.setSpriteConfig(textConfig);
	    labelConfig.setLabelPosition(LabelPosition.START);
	    labelConfig.setValueProvider(PROPERTY_ACCESS.key(), new StringLabelProvider<String>());
	    series.setLabelConfig(labelConfig);
	    series.setHighlighting(true);
	    series.setLegendValueProvider(PROPERTY_ACCESS.key(),new StringLabelProvider<String>()); 

	    chart.addSeries(series);
	    
	    final SeriesToolTipConfig<PieData> config = new SeriesToolTipConfig<PieData>();
	    config.setLabelProvider(new SeriesLabelProvider<PieData>() {
			
			@Override
			public String getLabel(PieData item, ValueProvider<? super PieData, ? extends Number> valueProvider) {
				return getProvidedLabel(item, valueProvider);
			}
		});
	    series.setToolTipConfig(config);
	}
	
	/**
	 * Creates legend
	 */
	void createLegend() {
		final Legend<PieData> legend = new Legend<PieData>();
		legend.setPosition(Position.RIGHT);
		chart.setLegend(legend);
	}
}