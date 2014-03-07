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
package org.pepstock.jem.gwt.client.commons;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that holds several other panel, with only one showed at a time 
 * @author Andrea "Stock" Stocchero
 */
public class ViewStackPanel extends VerticalPanel {

	private List<Widget> widgets = new LinkedList<Widget>();

	private int selected = -1;

	/**
	 * Creates an empty stack panel.
	 */
	public ViewStackPanel() {
		setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	}

	/**
	 * Adds a new child with the given widget.
	 * 
	 * @param w
	 *            the widget to be added
	 */
	public void add(Widget w) {
		widgets.add(w);
		if (selected == -1) {
			showSelected(0);
		}
	}

	/**
	 * Gets the currently selected widget.
	 * 
	 * @return the currently selected widget
	 */
	public Widget getSelectedWidget(){
		if (selected >= 0) {
			return this.widgets.get(selected);
		}
		return null;
	}
	
	/**
	 * @return the widgtes
	 */
	public List<Widget> getWidgets() {
		return widgets;
	}

	@Override
	public int getWidgetCount() {
		return widgets.size();
	}

	@Override
	public int getWidgetIndex(Widget child) {
		return widgets.indexOf(child);
	}

	@Override
	public int getWidgetIndex(IsWidget child) {
		return widgets.indexOf(child);
	}

	/**
	 * Gets the currently selected child index.
	 * 
	 * @return selected child
	 */
	public int getSelectedIndex() {
		return selected;
	}

	/**
	 * Shows the widget at the specified child index.
	 * 
	 * @param index
	 *            the index of the child to be shown
	 */
	public void showStack(int index) {
		if ((index >= widgets.size()) || (index == selected)) {
			return;
		}

		if (selected >= 0) {
			changeSelected(index);
		} else {
			showSelected(index);
		}
	}

    private void changeSelected(int index) {
    	Widget select = widgets.get(selected);
    	super.remove(select);
    	showSelected(index);
    }
    
    private void showSelected(int index) {
    	Widget toSee = widgets.get(index);
    	super.add(toSee);
    	selected = index;
    }


}