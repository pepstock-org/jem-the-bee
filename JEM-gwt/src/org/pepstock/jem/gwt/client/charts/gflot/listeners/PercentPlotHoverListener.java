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
package org.pepstock.jem.gwt.client.charts.gflot.listeners;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.googlecode.gflot.client.event.PlotClickListener;
import com.googlecode.gflot.client.event.PlotHoverListener;
import com.googlecode.gflot.client.event.PlotItem;
import com.googlecode.gflot.client.event.PlotPosition;
import com.googlecode.gflot.client.jsni.Plot;

/**
 * {@link PlotClickListener} that show the percentage values 
 * @author Marco "Fuzzo" Cuccato
 */
public class PercentPlotHoverListener implements PlotHoverListener {

	private static final NumberFormat FORMATTER = NumberFormat.getFormat("#0.##");
	
	final PopupPanel popup = new PopupPanel();
	final Label label = new Label();

	/**
	 * 
	 */
	public PercentPlotHoverListener() {
		popup.add(label);
	}
	
	@Override
	public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
		if (item != null) {
			String text = FORMATTER.format(item.getDataPoint().getY());
			label.setText(text);
			popup.setPopupPosition(item.getPageX() + 10, item.getPageY() - 25);
			popup.show();
		} else {
			popup.hide();
		}
	}
	
}
