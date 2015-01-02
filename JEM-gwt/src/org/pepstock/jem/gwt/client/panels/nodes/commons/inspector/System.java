/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.nodes.commons.inspector;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.node.ExecutionEnvironment;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component that shows all information of node in inspect mode
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public final class System extends DefaultInspectorItem {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	private NodeInfoBean node = null;

	/**
	 * Builds the component, using the node instance as argument
	 * 
	 * @param node node instance in inspect mode
	 * 
	 */
	public System(final NodeInfoBean node) {
		this.node = node;

	    // MAIN PANEL
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.setHeight(Sizes.HUNDRED_PERCENT);
	    hp.setSpacing(10);

	    /*
	     * NODE INFO
	     */
	    VerticalPanel sysInfoVp = new VerticalPanel();
	    sysInfoVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(sysInfoVp);
	    hp.setCellWidth(sysInfoVp, "50%");
	    
	    Label sysInfoLabel = new Label("System information");
	    sysInfoLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    sysInfoLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    sysInfoLabel.addStyleName(Styles.INSTANCE.common().bold());
	    sysInfoVp.add(sysInfoLabel);
	    
	    FlexTable layoutSysInfo = new FlexTable();
	    layoutSysInfo.getColumnFormatter().setWidth(0, "50%");
	    layoutSysInfo.getColumnFormatter().setWidth(1, "50%");
	    layoutSysInfo.setCellPadding(10);
	    layoutSysInfo.setWidth(Sizes.HUNDRED_PERCENT);
	    ExecutionEnvironment env = node.getExecutionEnvironment();
	   
	    layoutSysInfo.setHTML(0, 0, "Memory (MB)");
	    layoutSysInfo.setWidget(0, 1, new HTML(String.valueOf(node.getTotalMemory()/1024L/1024L)));

	    layoutSysInfo.setHTML(1, 0, "Available processors");
	    layoutSysInfo.setWidget(1, 1, new HTML(String.valueOf(node.getAvailableProcessors())));

	    layoutSysInfo.setHTML(2, 0, "PID");
	    layoutSysInfo.setWidget(2, 1, new HTML(String.valueOf(node.getProcessId())));

	    layoutSysInfo.setHTML(3, 0, "System architecture");
	    layoutSysInfo.setWidget(3, 1, new HTML(node.getSystemArchitecture()));
	    
	    layoutSysInfo.setHTML(4, 0, "System name");
	    layoutSysInfo.setWidget(4, 1, new HTML(node.getSystemName()));
	    
	    layoutSysInfo.setHTML(5, 0, "Maximum heap size used for jobs (MB)");
	    layoutSysInfo.setWidget(5, 1, new HTML(String.valueOf(env.getMemory())));

	    UITools.setFlexTableStyles(layoutSysInfo, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    sysInfoVp.add(layoutSysInfo);
	    
	    // main
	    add(hp);
	}

	/**
	 * @return the NodeInfoBean
	 */
	public NodeInfoBean getNodeInfoBean() {
		return node;
	}
	
	/**
	 * @param node The NodeInfoBean to set
	 */
	public void setNodeInfoBean(NodeInfoBean node) {
		this.node = node;
	}
}