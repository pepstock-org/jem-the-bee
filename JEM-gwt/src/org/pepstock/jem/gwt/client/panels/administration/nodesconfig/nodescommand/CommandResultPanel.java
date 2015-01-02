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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig.nodescommand;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.editor.viewers.TextViewer;
import org.pepstock.jem.gwt.client.panels.administration.NodesCommandsPanel;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.ResultPanel;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class CommandResultPanel extends VerticalPanel implements ResultPanel, ResizeCapable {
	
	private static final String NODES_COMMAND_ID = "nodesCommandId";
	
	static {
		Styles.INSTANCE.administration().ensureInjected();
	}

	private CmdHeader header = new CmdHeader();

	private VerticalPanel resultHolder = new VerticalPanel();
	
	private TextViewer viewer = new TextViewer(NODES_COMMAND_ID);
	
	private NodesCommandsPanel parent = null;

	/**
	 * @param parent
	 */
	public CommandResultPanel(final NodesCommandsPanel parent) {
		this.parent = parent;
		resultHolder.add(viewer);
		resultHolder.addStyleName(Styles.INSTANCE.administration().nodeList());
		resultHolder.setSpacing(0);
		
		add(header);
		add(resultHolder);
		
		header.setListener(this);
	}

	/**
	 * @param result
	 */
	public void setResult(String result) {
		viewer.setContent(result);
		viewer.startEditor();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.administration.nodesconfig.CommandExecutor#execute(int)
	 */
	@Override
    public void execute(int command) {
    	NodeInfoBean node = parent.getList().getSelectedNode();
    	if (node != null){
    		parent.execute(node, command);
    	}
    }


    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	int desiredHeight = availableHeight - Sizes.NODE_LIST_HEADER_PX;
    	viewer.onResize(availableWidth, desiredHeight);
	}
	
	
}