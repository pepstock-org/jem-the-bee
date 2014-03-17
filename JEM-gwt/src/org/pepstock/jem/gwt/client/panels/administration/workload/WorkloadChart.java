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
package org.pepstock.jem.gwt.client.panels.administration.workload;

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
public class WorkloadChart extends AbstractTimeSeriesChart<Workload> {
	
	private static final WorkloadPropertyAccess DATA_ACCESS = GWT.create(WorkloadPropertyAccess.class);
	
	@SuppressWarnings("javadoc")
    public static final int JOBS_SUBMITTED = 0, JCLS_CHECKED = 1;
	
	private int type = JOBS_SUBMITTED;

	/**
	 * @param type
	 */
	public WorkloadChart(int type) {
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
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getMaximum(java.util.Collection)
	 */
    @Override
    public int getMaximum(Collection<Workload> data) {
		Workload maxdata = Collections.max(data, new Comparator<Workload>() {
			@Override
            public int compare(Workload arg0, Workload arg1) {
				int diff = 0;
	            if (getType() == JOBS_SUBMITTED){
	            	diff = arg0.getJobsSubmitted() - arg1.getJobsSubmitted();
	            } else {
	            	diff = arg0.getJclsChecked() - arg1.getJclsChecked();
	            }
	            return diff;
            }
		});
		int max = (getType() == JOBS_SUBMITTED) ? maxdata.getJobsSubmitted() : maxdata.getJclsChecked();
		
		max = ((int)(max/10) + 1) * 10;
		return max;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<Workload, ? extends Number> getAxisValues(DataPropertyAccess<Workload> propertyAccess) {
    	return getType() == JOBS_SUBMITTED ? DATA_ACCESS.jobsSubmitted() : DATA_ACCESS.jclsChecked();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisTitle()
	 */
    @Override
    public String getAxisTitle() {
	    return "Number";
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<Workload, String> getCategoryValues(DataPropertyAccess<Workload> propertyAccess) {
    	return DATA_ACCESS.key();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getProvidedLabel(java.lang.Object, com.sencha.gxt.core.client.ValueProvider)
	 */
    @Override
    public String getProvidedLabel(Workload item, ValueProvider<? super Workload, ? extends Number> valueProvider) {
    	StringBuilder sb = new StringBuilder("Count: ");
    	if (getType() == JOBS_SUBMITTED) {
    		sb.append(item.getJobsSubmitted());
    	} else {
    		sb.append(item.getJclsChecked());
    	}
	    return sb.toString();
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
	    return new ChartColor(32, 68, 186);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractTimeSeriesChart#getSprite()
	 */
    @Override
    public Sprite getSprite() {
    	return Primitives.diamond(0, 0, 6);
    }
}
