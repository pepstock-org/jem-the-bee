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
package org.pepstock.jem.gwt.client.panels.administration.queues.inspector;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.pepstock.jem.gwt.client.charts.AbstractTimeSeriesChart;
import org.pepstock.jem.gwt.client.charts.ChartColor;
import org.pepstock.jem.gwt.client.charts.DataPropertyAccess;
import org.pepstock.jem.gwt.client.panels.administration.queues.DetailedQueueData;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.chart.client.chart.series.Primitives;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class QueueDetailsChart extends AbstractTimeSeriesChart<DetailedQueueData> {
	
	private static final QueueDataPropertyAccess DATA_ACCESS = GWT.create(QueueDataPropertyAccess.class);
	
	@SuppressWarnings("javadoc")
    public static final int ENTRIES = 0, HITS = 1, LOCKED = 2, WAITS = 3, GETS = 4, PUTS = 5, REMOVES = 6;

	private int type = ENTRIES;
	
	/**
	 * @param type
	 */
	public QueueDetailsChart(int type) {
		super(DATA_ACCESS);
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.charts.AbstractChart#getMaximum(java.util
	 * .Collection)
	 */
	@Override
	public int getMaximum(Collection<DetailedQueueData> data) {
		DetailedQueueData maxdata = Collections.max(data, new DetailedQueueDataComparator());
		long max = 0;
		switch (getType()) {
		case ENTRIES:
			max = maxdata.getEntries();
			break;
		case HITS:
			max = maxdata.getHits();
			break;
		case LOCKED:
			max = maxdata.getLocked();
			break;
		case WAITS:
			max = maxdata.getLockWaits();
			break;
		case GETS:
			max = maxdata.getGets();
			break;
		case PUTS:
			max = maxdata.getPuts();
			break;
		case REMOVES:
			max = maxdata.getRemoves();
			break;
		default:
			max = maxdata.getEntries();
			break;
		}

		max = ((long) (max / 10) + 1) * 10;
		return (int) max;
	}

	private class DetailedQueueDataComparator implements Comparator<DetailedQueueData> {
		@Override
		public int compare(DetailedQueueData arg0, DetailedQueueData arg1) {
			long diff = 0;
			switch (getType()) {
			case ENTRIES:
				diff = arg0.getEntries() - arg1.getEntries();
				break;
			case HITS:
				diff = arg0.getHits() - arg1.getHits();
				break;
			case LOCKED:
				diff = arg0.getLocked() - arg1.getLocked();
				break;
			case WAITS:
				diff = arg0.getLockWaits() - arg1.getLockWaits();
				break;
			case GETS:
				diff = arg0.getGets() - arg1.getGets();
				break;
			case PUTS:
				diff = arg0.getPuts() - arg1.getPuts();
				break;
			case REMOVES:
				diff = arg0.getRemoves() - arg1.getRemoves();
				break;
			default:
				diff = arg0.getEntries() - arg1.getEntries();
				break;
			}
			return (int) diff;
		}	
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<DetailedQueueData, ? extends Number> getAxisValues(DataPropertyAccess<DetailedQueueData> propertyAccess) {
		switch (getType()) {
		case ENTRIES:
			return DATA_ACCESS.entries();
		case HITS:
			return DATA_ACCESS.hits();
		case LOCKED:
			return DATA_ACCESS.locked();
		case WAITS:
			return DATA_ACCESS.lockWaits();
		case GETS:
			return DATA_ACCESS.gets();
		case PUTS:
			return DATA_ACCESS.puts();
		case REMOVES:
			return DATA_ACCESS.removes();
		default:
			return DATA_ACCESS.entries();
		}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisTitle()
	 */
    @Override
    public String getAxisTitle() {
		if (getType() == ENTRIES || getType() == LOCKED){
			return "Entries";
		} else {
			return "Count";
		}    	
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<DetailedQueueData, String> getCategoryValues(DataPropertyAccess<DetailedQueueData> propertyAccess) {
	    return DATA_ACCESS.key();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getProvidedLabel(java.lang.Object, com.sencha.gxt.core.client.ValueProvider)
	 */
	@Override
	public String getProvidedLabel(DetailedQueueData item, ValueProvider<? super DetailedQueueData, ? extends Number> valueProvider) {
		String message = null;

		switch (getType()) {
		case ENTRIES:
			message = "Entries: " + item.getEntries();
			break;
		case HITS:
			message = "Count of hits: " + item.getHits();
			break;
		case LOCKED:
			message = "Count of locks: " + item.getLocked();
			break;
		case WAITS:
			message = "Count of waiters: " + item.getLockWaits();
			break;
		case GETS:
			message = "Number of gets: " + item.getGets();
			break;
		case PUTS:
			message = "Number of puts: " + item.getPuts();
			break;
		case REMOVES:
			message = "Number of removes: " + item.getRemoves();
			break;
		default:
			message = "Entries: " + item.getEntries();
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
    	 return new ChartColor(148, 174, 10);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractTimeSeriesChart#getSprite()
	 */
    @Override
    public Sprite getSprite() {
    	return Primitives.triangle(0, 0, 6);
    }
}
