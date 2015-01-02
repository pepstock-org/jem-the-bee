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
package org.pepstock.jem.gwt.client.panels.nodes.commons;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.nodes.commons.inspector.JobNameCell;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JobsListInspector extends PopupPanel {
	
	/**
	 * Width of inspector
	 */
	public static final int WIDTH = 250;
	
	/**
	 * Height of inspector
	 */
	// assumes that a row is 26px height, then max 4 rows
	public static final int HEIGHT = 26 * 4;
	
	private VerticalPanel mainContainer = new VerticalPanel();
	
	/**
	 * Inspector which shows the list of jobs of node
	 * 
	 * @param node node which contains jobs 
	 */
	public JobsListInspector(NodeInfoBean node) {
		// Popup panel constructor
		super(true);

		// cell renderer for job name
		JobNameCell textCell = new JobNameCell();

		// Create a CellList that uses the cell.
		CellList<String> cellList = new CellList<String>(textCell);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Set the total row count. This isn't strictly necessary, but it affects
		// paging calculations, so its good habit to keep the row count up to date.
		cellList.setRowCount(node.getJobNames().size(), true);

		// Push the data into the widget.
		cellList.setRowData(0, node.getJobNames());
		
		ScrollPanel panel = new ScrollPanel(cellList);
		panel.setSize(Sizes.toString(WIDTH), Sizes.toString(HEIGHT));
		
		mainContainer.add(panel);
		
		setWidget(mainContainer);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#onPreviewNativeEvent(com.google.gwt.user.client.NativePreviewEvent)
	 */
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()){
		case Event.ONKEYDOWN:
			onKeyDown(event);
			break;
		case Event.ONMOUSEOUT:
			onMouseOut(event);
			break;
		default:	
			break;
		}
	}

	private void onMouseOut(NativePreviewEvent event) {
		if (isShowing()){
			boolean insidePopup = Sizes.isEventInsideWidget(event.getNativeEvent(), this);
			// if inside of popup, ignore the event (generates by label of message and title)
			if (!insidePopup){
				hide();
			}
		}
	}
	
	private void onKeyDown(NativePreviewEvent event) {
		// clicking ESC, close the popup
		if ((event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) && (isShowing())){
			hide();
		}
	}
	
}
