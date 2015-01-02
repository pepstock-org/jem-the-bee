/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.charts.gflot.listeners;

import com.googlecode.gflot.client.event.PlotClickListener;
import com.googlecode.gflot.client.event.PlotItem;
import com.googlecode.gflot.client.event.PlotPosition;
import com.googlecode.gflot.client.jsni.Plot;

/**
 * Default {@link PlotClickListener} that keep highlighted a point if clicked and unhighlight it if a click is done outside 
 * @author Marco "Fuzzo" Cuccato
 */
public class DefaultPlotClickListener implements PlotClickListener {

	@Override
	public void onPlotClick(Plot plot, PlotPosition position, PlotItem item) {
        plot.unhighlight();
        if(null != item){
            plot.highlight(item.getSeries(), item.getDataPoint());
        }
	}
	
}