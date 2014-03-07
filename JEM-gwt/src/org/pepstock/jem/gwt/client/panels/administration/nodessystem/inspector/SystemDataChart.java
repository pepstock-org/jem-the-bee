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
package org.pepstock.jem.gwt.client.panels.administration.nodessystem.inspector;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.pepstock.jem.gwt.client.charts.AbstractTimeSeriesChart;
import org.pepstock.jem.gwt.client.charts.ChartColor;
import org.pepstock.jem.gwt.client.charts.DataPropertyAccess;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.sencha.gxt.chart.client.chart.series.Primitives;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class SystemDataChart extends AbstractTimeSeriesChart<SystemData> {
	
	private static final SystemDataPropertyAccess DATA_ACCESS = GWT.create(SystemDataPropertyAccess.class);

	@SuppressWarnings("javadoc")
    public static final int MACHINE_CPU_PERCENT = 0, PROCESS_CPU_PERCENT = 1, PROCESS_MEMORY_UTIL = 2;

	private int type = MACHINE_CPU_PERCENT;
	
	/**
	 * @param type
	 */
	public SystemDataChart(int type) {
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
	    return Primitives.square(0, 0, 6);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getMaximum(java.util.Collection)
	 */
	@Override
	public int getMaximum(Collection<SystemData> data) {
		if (getType() == PROCESS_MEMORY_UTIL) {
			SystemData maxdata = Collections.max(data, new Comparator<SystemData>() {
				@Override
				public int compare(SystemData arg0, SystemData arg1) {
					return (int) (arg0.getProcessMemoryUtil() - arg1.getProcessMemoryUtil());
				}
			});
			int max = (int) maxdata.getProcessMemoryUtil();

			max = ((int) (max / 10) + 1) * 10;
			return max;
		} else {
			return 100;
		}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<SystemData, ? extends Number> getAxisValues(DataPropertyAccess<SystemData> propertyAccess) {
		switch (getType()) {
		case MACHINE_CPU_PERCENT:
			return DATA_ACCESS.machineCpuPercent();
		case PROCESS_CPU_PERCENT:
			return DATA_ACCESS.processCpuPercent();
		case PROCESS_MEMORY_UTIL:
			return DATA_ACCESS.processMemoryUtil();
		default:
			return DATA_ACCESS.machineCpuPercent();
		}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getAxisTitle()
	 */
    @Override
    public String getAxisTitle() {
	    return "Time";
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryValues(org.pepstock.jem.gwt.client.charts.DataPropertyAccess)
	 */
    @Override
    public ValueProvider<SystemData, String> getCategoryValues(DataPropertyAccess<SystemData> propertyAccess) {
	    return DATA_ACCESS.key();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getProvidedLabel(java.lang.Object, com.sencha.gxt.core.client.ValueProvider)
	 */
	@Override
	public String getProvidedLabel(SystemData item, ValueProvider<? super SystemData, ? extends Number> valueProvider) {
		String message = null;

		switch (getType()) {
		case MACHINE_CPU_PERCENT:
			message = "Cpu usage: " + NumberFormat.getFormat("##0.00").format(item.getMachineCpuPercent());
			break;
		case PROCESS_CPU_PERCENT:
			message = "Cpu usage of JEM node: " + NumberFormat.getFormat("##0.00").format(item.getProcessCpuPercent());
			break;
		case PROCESS_MEMORY_UTIL:
			message = "Memory usage of JEM node: " + NumberFormat.getFormat("###,##0 MB").format((double) item.getProcessMemoryUtil());
			break;

		default:
			message = "Cpu usage: " + NumberFormat.getFormat("##0.00").format(item.getMachineCpuPercent());
			break;
		}
		return message;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractChart#getCategoryTitle()
	 */
	@Override
	public String getCategoryTitle() {
		String title = null;
		switch (getType()) {
		case MACHINE_CPU_PERCENT:
			title = "Cpu %";
			break;
		case PROCESS_CPU_PERCENT:
			title = "Cpu %";
			break;
		case PROCESS_MEMORY_UTIL:
			title = "Megabytes";
			break;
		default:
			title = "Cpu %";
			break;
		}
		return title;
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
	    return new ChartColor(194, 0, 36);
    }


}
