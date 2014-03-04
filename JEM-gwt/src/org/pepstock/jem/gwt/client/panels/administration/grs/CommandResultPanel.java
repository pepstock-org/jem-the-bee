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
package org.pepstock.jem.gwt.client.panels.administration.grs;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.editor.viewers.TextViewer;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class CommandResultPanel extends VerticalPanel implements ResizeCapable {
	
	private static final String NODES_COMMAND_ID = "grsCommandId";
	
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.administration().ensureInjected();
	}

	private VerticalPanel resultHolder = new VerticalPanel();
	
	private TextViewer viewer = new TextViewer(NODES_COMMAND_ID);
	
	private Label header = new Label("Contentions list:");

	/**
	 * @param parent
	 */
	public CommandResultPanel() {
		header.addStyleName(Styles.INSTANCE.common().bold());
		resultHolder.addStyleName(Styles.INSTANCE.administration().nodeList());
		resultHolder.add(viewer);
		resultHolder.setSpacing(0);
		
		//scroller
		add(header);
		add(resultHolder);
	}

	/**
	 * @param result
	 */
	public void setResult(String result) {
		
		viewer.setContent(result);
		viewer.startEditor();
	}

    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	int headerHeight = Sizes.NODE_LIST_HEADER_PX / 2;
    	
    	// borders (1px and 1px to remove) of Scrollapanel set by CSS 
    	int desiredHeight = availableHeight - 
    			headerHeight - 
    			Sizes.MAIN_TAB_PANEL_BORDER - 
    			Sizes.MAIN_TAB_PANEL_BORDER;
    	
    	header.setSize(Sizes.toString(availableWidth), Sizes.toString(headerHeight));
    	viewer.onResize(availableWidth, desiredHeight);

	}
}