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
package org.pepstock.jem.gwt.client.panels.jobs.commons.inspector;

import org.pepstock.jem.Job;
import org.pepstock.jem.RoutingInfo;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This component displays all routing information about the job in inspect mode 
 *  
 * @author Marco "Fuzzo" Cuccato
 */
public class RouteInfo extends DefaultInspectorItem {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	private Job job = null;

	/**
	 * Build the component
	 * @param job the job instance
	 */
	public RouteInfo(Job job) {
		this.job = job;
		
	    // MAIN PANEL
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.setHeight(Sizes.HUNDRED_PERCENT);
	    hp.setSpacing(10);
	    
	    /*
	     * ROUTE
	     */
	    VerticalPanel routeVp = new VerticalPanel();
	    routeVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(routeVp);
	    hp.setCellWidth(routeVp, "50%");

	    Label routeLabel = new Label("Job routing information");
	    routeLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    routeLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    routeLabel.addStyleName(Styles.INSTANCE.common().bold());
	    routeVp.add(routeLabel);
	    
	    FlexTable layoutRouteInfo = new FlexTable();
	    layoutRouteInfo.getColumnFormatter().setWidth(0, "50%");
	    layoutRouteInfo.getColumnFormatter().setWidth(1, "50%");
	    layoutRouteInfo.setCellPadding(10);
	    layoutRouteInfo.setWidth(Sizes.HUNDRED_PERCENT);
	    RoutingInfo routingInfo = job.getRoutingInfo();
	    
	    layoutRouteInfo.setHTML(0, 0, "Route ID");
	    layoutRouteInfo.setWidget(0, 1, new HTML(routingInfo.getId()));
	    
	    layoutRouteInfo.setHTML(1, 0, "Routed time");
	    layoutRouteInfo.setWidget(1, 1, new HTML(JemConstants.DATE_TIME_FULL.format(routingInfo.getRoutedTime())));
	    
	    layoutRouteInfo.setHTML(2, 0, "Submitted time");
	    layoutRouteInfo.setWidget(2, 1, new HTML(JemConstants.DATE_TIME_FULL.format(routingInfo.getSubmittedTime())));

	    layoutRouteInfo.setHTML(3, 0, "Submission environment");
	    layoutRouteInfo.setWidget(3, 1, new HTML(routingInfo.getEnvironment()));
	    
	    layoutRouteInfo.setHTML(4, 0, "Committed output");
	    Boolean isOutputCommitted = job.getRoutingInfo().isOutputCommitted();
	    layoutRouteInfo.setWidget(4, 1, new HTML(isOutputCommitted != null && isOutputCommitted ? JemConstants.YES : JemConstants.NO));
	    
	    UITools.setFlexTableStyles(layoutRouteInfo, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    routeVp.add(layoutRouteInfo);
	    
	    // main
	    add(hp);
	}

	/**
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
	}
}