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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import org.pepstock.jem.gwt.client.charts.AbstractPieChart;
import org.pepstock.jem.gwt.client.charts.PieData;

import com.google.gwt.i18n.client.NumberFormat;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class GfsChart extends AbstractPieChart {

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.charts.AbstractPieChart#getProvidedLabel(org.pepstock.jem.gwt.client.charts.PieData, com.sencha.gxt.core.client.ValueProvider)
	 */
	@Override
	public String getProvidedLabel(PieData item, ValueProvider<? super PieData, ? extends Number> valueProvider) {
		return item.getKey()+": "+NumberFormat.getFormat("##0.00 %").format((double)item.getPercent())+", "+NumberFormat.getFormat("###,###,##0 MB").format((double)item.getValue()/1024);
	}

}
