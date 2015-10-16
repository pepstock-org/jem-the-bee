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
package org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class GeneralViewConfig extends DefaultInspectorItem  {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	private SwarmConfiguration swarmConfiguration = null;

	private Label groupName = new Label();

	private Label netInterface = new Label();

	private Label port = new Label();

	private CheckBox isConfigurationEnabled = new CheckBox();

	private Label user = new Label();

	private Label lastModified = new Label();

	/**
	 * @param swarmConfiguration
	 * 
	 */
	public GeneralViewConfig(SwarmConfiguration swarmConfiguration) {
		
	    /*
	     * GENERAL INFO
	     */
	    VerticalPanel nodeVp = new VerticalPanel();
	    nodeVp.setWidth(Sizes.HUNDRED_PERCENT);
	    nodeVp.setHeight(Sizes.HUNDRED_PERCENT);
	    
	    Label genLabel = new Label("General information");
	    genLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    genLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    genLabel.addStyleName(Styles.INSTANCE.common().bold());
	    nodeVp.add(genLabel);

	    final FlexTable layout = new FlexTable();
	    layout.setCellPadding(10);
	    layout.setWidth(Sizes.HUNDRED_PERCENT);

	    layout.setHTML(RowIndex.ROW_1,ColumnIndex.COLUMN_1, "Enabled");
	    layout.setWidget(RowIndex.ROW_1,ColumnIndex.COLUMN_2,isConfigurationEnabled);
	    
	    layout.setHTML(RowIndex.ROW_2,ColumnIndex.COLUMN_1, "Group name");
	    layout.setWidget(RowIndex.ROW_2,ColumnIndex.COLUMN_2, groupName);

	    layout.setHTML(RowIndex.ROW_3,ColumnIndex.COLUMN_1, "Port");
	    layout.setWidget(RowIndex.ROW_3,ColumnIndex.COLUMN_2,  port);
	    
	    layout.setHTML(RowIndex.ROW_4,ColumnIndex.COLUMN_1, "Network interface");
		layout.setWidget(RowIndex.ROW_4,ColumnIndex.COLUMN_2, netInterface);
	    
		layout.setHTML(RowIndex.ROW_5,ColumnIndex.COLUMN_1, "User");
		layout.setWidget(RowIndex.ROW_5,ColumnIndex.COLUMN_2, user);
		
		layout.setHTML(RowIndex.ROW_6,ColumnIndex.COLUMN_1, "Last update");
		layout.setWidget(RowIndex.ROW_6,ColumnIndex.COLUMN_2, lastModified);

	    UITools.setFlexTableStyles(layout, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    nodeVp.add(layout);
    
	    // main
	    add(nodeVp);
	    
	    setSwarmConfiguration(swarmConfiguration);

	}

	/**
	 * @return the swarmConfiguration
	 */
	public SwarmConfiguration getSwarmConfiguration() {
		return swarmConfiguration;
	}

	/**
	 * @param swarmConfiguration
	 *            the swarmConfiguration to set
	 */
	public void setSwarmConfiguration(SwarmConfiguration swarmConfiguration) {
		this.swarmConfiguration = swarmConfiguration;
		loadConfiguration();
	}

	/**
	 * 
	 */
	private void loadConfiguration() {

		groupName.setText(swarmConfiguration.getGroupName());

		port.setText(String.valueOf(swarmConfiguration.getPort()));
	
		isConfigurationEnabled.setValue(swarmConfiguration.isEnabled());
		isConfigurationEnabled.setEnabled(false);
   
		user.setText(swarmConfiguration.getUser());

		if (swarmConfiguration.getLastModified() != null) {
			lastModified.setText(JemConstants.DATE_TIME_FULL.format(swarmConfiguration
					.getLastModified()));
		}
	}

}
