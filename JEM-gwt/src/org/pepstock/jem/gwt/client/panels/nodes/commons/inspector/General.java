/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.TimeDisplayUtils;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.util.TimeUtils;

import com.google.gwt.user.client.Timer;
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
public final class General extends DefaultInspectorItem{

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	// 20 sec
	private static final int RUNNING_TIME_REFRESH_INTERVAL = 20 * (int)TimeUtils.SECOND;
	
	private NodeInfoBean node = null;
	private Timer elapsedTimer = null;

	/**
	 * Builds the component, using the node instance as argument
	 * 
	 * @param node node instance in inspect mode
	 * 
	 */
	public General(final NodeInfoBean node) {
		this.node = node;
		
	    // MAIN PANEL
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.setHeight(Sizes.HUNDRED_PERCENT);
	    hp.setSpacing(10);
	    
	    /*
	     * NODE INFO
	     */
	    VerticalPanel nodeVp = new VerticalPanel();
	    nodeVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(nodeVp);
	    hp.setCellWidth(nodeVp, "50%");
	    
	    Label nodeLabel = new Label("Node information");
	    nodeLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    nodeLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    nodeLabel.addStyleName(Styles.INSTANCE.common().bold());
	    nodeVp.add(nodeLabel);
	    
	    final FlexTable layoutNode = new FlexTable();
	    layoutNode.setCellPadding(10);
	    layoutNode.setWidth(Sizes.HUNDRED_PERCENT);

	    layoutNode.setHTML(0, 0, "Label");
	    layoutNode.setWidget(0, 1, new HTML(node.getLabel()));
	    layoutNode.setHTML(1, 0, "Status");
	    layoutNode.setWidget(1, 1, new HTML(node.getStatus()));
	    
	    layoutNode.setHTML(2, 0, "Host");
	    layoutNode.setWidget(2, 1, new HTML(node.getHostname()));
	    layoutNode.setHTML(3, 0, "IP:Port");
	    layoutNode.setWidget(3, 1, new HTML(node.getIpaddress() + ":" + node.getPort()));
	    
	    // running time
	    
	    layoutNode.setHTML(4, 0, "Running time");
	    layoutNode.setWidget(4, 1, new HTML(JemConstants.UPDATING_BRACKETS));
	    elapsedTimer = new Timer() {
			@Override
			public void run() {
				layoutNode.setWidget(4, 1, new HTML(JemConstants.UPDATING_BRACKETS));
				String displayed;
				try {
					displayed = TimeDisplayUtils.getReadableTimeDiff(node.getStartedTime(), TimeDisplayUtils.VERBOSE); 
				} catch (Exception e) {
					LogClient.getInstance().warning(e.getMessage(), e);
					displayed = JemConstants.UNAVAILABLE_BRACKETS;
				}
				layoutNode.setWidget(4, 1, new HTML(displayed));
			}
		};
		elapsedTimer.run();
		elapsedTimer.scheduleRepeating(RUNNING_TIME_REFRESH_INTERVAL);
		
	    layoutNode.setHTML(5, 0, "Key");
	    layoutNode.setWidget(5, 1, new HTML(node.getKey()));
	    
	    layoutNode.setHTML(6, 0, "RMI Port");
	    layoutNode.setWidget(6, 1, new HTML(String.valueOf(node.getRmiPort())));
	    
	    layoutNode.setHTML(7, 0, "Jem Version");
	    layoutNode.setWidget(7, 1, new HTML(node.getJemVersion()));
	    
	    UITools.setFlexTableStyles(layoutNode, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    nodeVp.add(layoutNode);
	    
	    /*
	     * EXECUTION ENVIRONMENT
	     */
	    VerticalPanel envVp = new VerticalPanel();
	    envVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(envVp);
	    hp.setCellWidth(envVp, "50%");
	    
	    Label envLabel = new Label("Execution environment");
	    envLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    envLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    envLabel.addStyleName(Styles.INSTANCE.common().bold());
	    envVp.add(envLabel);
	    
	    FlexTable layoutEnvironment = new FlexTable();
	    layoutEnvironment.setCellPadding(10);
	    layoutEnvironment.setWidth(Sizes.HUNDRED_PERCENT);
	    ExecutionEnvironment env = node.getExecutionEnvironment();

	    layoutEnvironment.setHTML(0, 0, "Name");
	    layoutEnvironment.setWidget(0, 1, new HTML(env.getEnvironment()));
	    layoutEnvironment.setHTML(1, 0, "Domain");
	    layoutEnvironment.setWidget(1, 1, new HTML(env.getDomain()));
	    layoutEnvironment.setHTML(2, 0, "Static Affinities");
	    layoutEnvironment.setWidget(2, 1, new HTML(env.getStaticAffinities().toString()));
	    layoutEnvironment.setHTML(3, 0, "Dynamic Affinities");
	    layoutEnvironment.setWidget(3, 1, new HTML(env.getDynamicAffinities().toString()));
	    layoutEnvironment.setHTML(4, 0, "Parallel jobs");
	    layoutEnvironment.setWidget(4, 1, new HTML(String.valueOf(env.getParallelJobs())));
	    layoutEnvironment.setHTML(5, 0, "Node type");
	    layoutEnvironment.setWidget(5, 1, new HTML(String.valueOf(node.getType())));

	    
	    UITools.setFlexTableStyles(layoutEnvironment, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    envVp.add(layoutEnvironment);
	    
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

	@Override
	protected void onDetach() {
		try {
			elapsedTimer.cancel();
		} catch (Exception e) {
			LogClient.getInstance().warning(e.getMessage(), e);
		}
		super.onDetach();
	}
}