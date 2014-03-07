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
package org.pepstock.jem.gwt.client.panels.administration.nodesqueues.inspector;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.pepstock.jem.gwt.client.charts.AbstractTimeSeriesChart;
import org.pepstock.jem.gwt.client.charts.ChartColor;
import org.pepstock.jem.gwt.client.charts.DataPropertyAccess;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.chart.client.chart.series.Primitives;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class NodeDataChart extends AbstractTimeSeriesChart<NodeData> {
	
	private static final NodeDataPropertyAccess DATA_ACCESS = GWT.create(NodeDataPropertyAccess.class);
	
	@SuppressWarnings("javadoc")
    public static final int INPUT = 0, RUNNING = 1, OUTPUT = 2, ROUTING = 3;
	
	private int type = INPUT;

	/**
	 * @param type
	 */
	public NodeDataChart(int type) {
		super(DATA_ACCESS);
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractTimeSeriesChart#getSprite()
	 */
    @Override
    public Sprite getSprite() {
	    return Primitives.circle(0, 0, 6);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getMaximum(java.util.Collection)
	 */
    @Override
    public int getMaximum(Collection<NodeData> data) {
		NodeData maxdata = Collections.max(data, new NodeDataComparator());
		long max = 0;
		switch (getType()) {
		case INPUT:
			max = maxdata.getInput();
			break;
		case RUNNING:
			max = maxdata.getRunning();
			break;
		case OUTPUT:
			max = maxdata.getOutput();
			break;
		case ROUTING:
			max = maxdata.getRouting();
			break;
		default:
			max = maxdata.getInput();
			break;
		}

		max = ((long) (max / 10) + 1) * 10;
		return (int) max;
    }

    private class NodeDataComparator implements Comparator<NodeData> {
		@Override
		public int compare(NodeData arg0, NodeData arg1) {
			long diff = 0;
			switch (getType()) {
			case INPUT:
				diff = arg0.getInput() - arg1.getInput();
				break;
			case RUNNING:
				diff = arg0.getRunning() - arg1.getRunning();
				break;
			case OUTPUT:
				diff = arg0.getOutput() - arg1.getOutput();
				break;
			case ROUTING:
				diff = arg0.getRouting() - arg1.getRouting();
				break;
			default:
				diff = arg0.getInput() - arg1.getInput();
				break;
			}
			return (int) diff;
		}
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
	@Override
	public ValueProvider<NodeData, ? extends Number> getAxisValues(DataPropertyAccess<NodeData> propertyAccess) {
		switch (getType()) {
		case INPUT:
			return DATA_ACCESS.input();
		case RUNNING:
			return DATA_ACCESS.running();
		case OUTPUT:
			return DATA_ACCESS.output();
		case ROUTING:
			return DATA_ACCESS.routing();
		default:
			return DATA_ACCESS.input();
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisTitle()
	 */
    @Override
    public String getAxisTitle() {
	    return "Entries";
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<NodeData, String> getCategoryValues(DataPropertyAccess<NodeData> propertyAccess) {
	    return DATA_ACCESS.key();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getProvidedLabel(java.lang.Object, com.sencha.gxt.core.client.ValueProvider)
	 */
    @Override
    public String getProvidedLabel(NodeData item, ValueProvider<? super NodeData, ? extends Number> valueProvider) {
		String message = null;

		switch (getType()) {
		case INPUT:
			message = "Entries: " + item.getInput();
			break;
		case RUNNING:
			message = "Entries: " + item.getRunning();
			break;
		case OUTPUT:
			message = "Entries: " + item.getOutput();
			break;
		case ROUTING:
			message = "Entries: " + item.getRouting();
			break;
		default:
			message = "Entries: " + item.getInput();
			break;
		}

		return message;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryTitle()
	 */
    @Override
    public String getCategoryTitle() {
	    return "Time";
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryLabel(java.lang.String)
	 */
    @Override
    public String getCategoryLabel(String item) {
	    return item.substring(0, 5);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getColor()
	 */
    @Override
    public ChartColor getColor() {
	    return new ChartColor(240, 165, 10);
    }
	
}
