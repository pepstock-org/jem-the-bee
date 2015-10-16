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
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

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

	private static final long KB = 1024L;
	
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
	    layoutSysInfo.getColumnFormatter().setWidth(ColumnIndex.COLUMN_1, "50%");
	    layoutSysInfo.getColumnFormatter().setWidth(RowIndex.ROW_2, "50%");
	    layoutSysInfo.setCellPadding(10);
	    layoutSysInfo.setWidth(Sizes.HUNDRED_PERCENT);
	    ExecutionEnvironment env = node.getExecutionEnvironment();
	   
	    layoutSysInfo.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Memory (MB)");
	    layoutSysInfo.setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_2, new HTML(String.valueOf(node.getTotalMemory()/KB/KB)));

	    layoutSysInfo.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "Available processors");
	    layoutSysInfo.setWidget(RowIndex.ROW_2, ColumnIndex.COLUMN_2, new HTML(String.valueOf(node.getAvailableProcessors())));

	    layoutSysInfo.setHTML(RowIndex.ROW_3, ColumnIndex.COLUMN_1, "PID");
	    layoutSysInfo.setWidget(RowIndex.ROW_3, ColumnIndex.COLUMN_2, new HTML(String.valueOf(node.getProcessId())));

	    layoutSysInfo.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_1, "System architecture");
	    layoutSysInfo.setWidget(RowIndex.ROW_4, ColumnIndex.COLUMN_2, new HTML(node.getSystemArchitecture()));
	    
	    layoutSysInfo.setHTML(RowIndex.ROW_5, ColumnIndex.COLUMN_1, "System name");
	    layoutSysInfo.setWidget(RowIndex.ROW_5, ColumnIndex.COLUMN_2, new HTML(node.getSystemName()));
	    
	    layoutSysInfo.setHTML(RowIndex.ROW_6, ColumnIndex.COLUMN_1, "Maximum heap size used for jobs (MB)");
	    layoutSysInfo.setWidget(RowIndex.ROW_6, ColumnIndex.COLUMN_2, new HTML(String.valueOf(env.getMemory())));

	    layoutSysInfo.setHTML(RowIndex.ROW_7, ColumnIndex.COLUMN_1, "Java Virtual Machine");
	    layoutSysInfo.setWidget(RowIndex.ROW_7, ColumnIndex.COLUMN_2, new HTML("Vendor: "+node.getJavaVendor()+", Version: "+node.getJavaVersion()));

	    
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
