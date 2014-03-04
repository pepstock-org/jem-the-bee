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
package org.pepstock.jem.gwt.client.panels.administration.current;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.pepstock.jem.gwt.client.charts.AbstractBarChart;
import org.pepstock.jem.gwt.client.charts.ChartColor;
import org.pepstock.jem.gwt.client.charts.DataPropertyAccess;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class EntriesChart extends AbstractBarChart<QueueData> {
	
	private static final EntriesPropertyAccess DATA_ACCESS = GWT.create(EntriesPropertyAccess.class);

	/**
	 * @param propertyAccess
	 */
	public EntriesChart() {
		super(DATA_ACCESS);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getMaximum(java.util.Collection)
	 */
    @Override
    public int getMaximum(Collection<QueueData> data) {
		QueueData maxdata = Collections.max(data, new Comparator<QueueData>() {
			@Override
            public int compare(QueueData arg0, QueueData arg1) {
				long diff = 0;
	            diff = arg0.getEntries() - arg1.getEntries();
	            return (int)diff;
            }
		});
		long max = maxdata.getEntries();
		
		max = ((long)(max/10) + 1) * 10;
		return (int)max;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getAxisValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<QueueData, ? extends Number> getAxisValues(DataPropertyAccess<QueueData> propertyAccess) {
	    return DATA_ACCESS.entries();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getAxisTitle()
	 */
    @Override
    public String getAxisTitle() {
	    return "Entries";
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getCategoryValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<QueueData, String> getCategoryValues(DataPropertyAccess<QueueData> propertyAccess) {
	    return DATA_ACCESS.key();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getCategoryTitle()
	 */
    @Override
    public String getCategoryTitle() {
	    return "Queues";
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getProvidedLabel(java.lang.Object, com.sencha.gxt.core.client.ValueProvider)
	 */
    @Override
    public String getProvidedLabel(QueueData item, ValueProvider<? super QueueData, ? extends Number> valueProvider) {
	    return "Entries: "+item.getEntries();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.BarChart#getColor()
	 */
    @Override
    public ChartColor getColor() {
	    return new ChartColor(148,174,10);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryLabel(java.lang.String)
	 */
    @Override
    public String getCategoryLabel(String item) {
	    return item;
    }


}
