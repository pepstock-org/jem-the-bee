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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig.nodescommand;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.panels.administration.NodesCommandsPanel;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.CommandExecutor;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.ResultPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class CmdHeader extends FlexTable  {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 * 
	 */
	private ResultPanel listener = null;

	/**
	 * @param label 
	 * 
	 */
	public CmdHeader() {
		setSize(Sizes.HUNDRED_PERCENT, Sizes.toString(Sizes.NODE_LIST_HEADER_PX));
		
		/* 		  0							1
		 * 		-------------------------------------
		 * 	0	| <title>					back    |
		 * 		-------------------------------------
		 */
		
		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		cf.setWidth(0, 0, Sizes.HUNDRED_PERCENT);
		cf.setWordWrap(0, 0, false);
		cf.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		cf.addStyleName(0, 0, Styles.INSTANCE.common().bold());
		
		
		int column = 1;
		for (final CommandExecutor executor : NodesCommandsPanel.COMMANDS){
			// 0-1 > back
			cf.setWidth(0, column, "15%");
			// logoff button (and handler)
			final Button button = new Button(executor.getLabel());
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (listener != null){
						listener.execute(executor.getIndex());
						setHTML(0, 0, executor.getTitle());
					}
				}
			});
			new Tooltip(button, executor.getTitle());
			cf.setVerticalAlignment(0, column, HasVerticalAlignment.ALIGN_MIDDLE);
			cf.setHorizontalAlignment(0, column, HasHorizontalAlignment.ALIGN_RIGHT);
			cf.setWordWrap(0, column, false);
			setWidget(0, column, button);
			if (column == 1){
				setHTML(0, 0, executor.getTitle());
			}
			column++;
		}
	}

	/**
	 * @return the listener
	 */
	public ResultPanel getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(ResultPanel listener) {
		this.listener = listener;
	}


}