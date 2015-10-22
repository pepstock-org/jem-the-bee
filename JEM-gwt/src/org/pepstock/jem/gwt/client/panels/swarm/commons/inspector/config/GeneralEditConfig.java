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
import org.pepstock.jem.gwt.client.commons.MandatoryNumericalTextBox;
import org.pepstock.jem.gwt.client.commons.MandatoryPasswordTextBox;
import org.pepstock.jem.gwt.client.commons.MandatoryTextBox;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Alessandro Zambrini
 * 
 */
public final class GeneralEditConfig extends DefaultInspectorItem {

	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private static final String ADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
					"$";

	private static final int COLUMN_WIDTH = 200;
	
	private SwarmConfiguration swarmConfiguration = null;

	private MandatoryTextBox groupName = new MandatoryTextBox();

	private MandatoryPasswordTextBox groupPassword = new MandatoryPasswordTextBox();

	private MandatoryPasswordTextBox confirmedGroupPassword = new MandatoryPasswordTextBox();

	private MandatoryNumericalTextBox port = new MandatoryNumericalTextBox();
	
	private TextBox netInterface = new TextBox();

	private CheckBox isConfigurationEnabled = new CheckBox();
	
	private Label user = new Label();
	
	private Label lastModified = new Label();

	private final FlexTable table = new FlexTable();
	
	
	/**
	 * @param swarmConfiguration
	 * 
	 */
	public GeneralEditConfig(SwarmConfiguration swarmConfiguration) {
		this.setSwarmConfiguration(swarmConfiguration);
		this.setWidth(Sizes.HUNDRED_PERCENT);
		table.setWidth(Sizes.HUNDRED_PERCENT);
		table.setCellPadding(5);

		table.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Enabled:");
		table.setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_2, isConfigurationEnabled);

		table.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "Group name: <font color=\"red\"><b>*</b></font>");
		table.setWidget(RowIndex.ROW_2, ColumnIndex.COLUMN_2, groupName);

		table.setHTML(RowIndex.ROW_3, ColumnIndex.COLUMN_1, "Group password: <font color=\"red\"><b>*</b></font>");
		table.setWidget(RowIndex.ROW_3, ColumnIndex.COLUMN_2, groupPassword);

		table.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_1, "Confirmed Group password: <font color=\"red\"><b>*</b></font>");
		table.setWidget(RowIndex.ROW_4, ColumnIndex.COLUMN_2, confirmedGroupPassword);
		table.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_3, "");

		table.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_4, "Port: <font color=\"red\"><b>*</b></font>");
		table.setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_5, port);
		table.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_6, "");
		
		table.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_4, "User:");
		table.setWidget(RowIndex.ROW_2, ColumnIndex.COLUMN_5, user);
		
		table.setHTML(RowIndex.ROW_3, ColumnIndex.COLUMN_4, "Last update:");
		table.setWidget(RowIndex.ROW_3, ColumnIndex.COLUMN_5, lastModified);
		
		table.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_1, "<font color=\"red\"><b>*</b></font> Mandatory.");

		FlexCellFormatter cf = table.getFlexCellFormatter();
		cf.setWordWrap(RowIndex.ROW_1, ColumnIndex.COLUMN_1, false);
		cf.setWordWrap(RowIndex.ROW_2, ColumnIndex.COLUMN_1, false);
		cf.setWordWrap(RowIndex.ROW_3, ColumnIndex.COLUMN_1, false);
		cf.setWordWrap(RowIndex.ROW_4, ColumnIndex.COLUMN_1, false);
		cf.setWordWrap(RowIndex.ROW_5, ColumnIndex.COLUMN_1, false);
		cf.setWordWrap(RowIndex.ROW_6, ColumnIndex.COLUMN_1, false);
		cf.setWordWrap(RowIndex.ROW_1, ColumnIndex.COLUMN_4, false);
		cf.setWordWrap(RowIndex.ROW_2, ColumnIndex.COLUMN_4, false);
		cf.setWordWrap(RowIndex.ROW_3, ColumnIndex.COLUMN_4, false);
		cf.setWordWrap(RowIndex.ROW_4, ColumnIndex.COLUMN_4, false);

		cf.addStyleName(RowIndex.ROW_1, ColumnIndex.COLUMN_3, Styles.INSTANCE.common().textRed());
		cf.addStyleName(RowIndex.ROW_2, ColumnIndex.COLUMN_3, Styles.INSTANCE.common().textRed());
		cf.addStyleName(RowIndex.ROW_3, ColumnIndex.COLUMN_3, Styles.INSTANCE.common().textRed());
		cf.addStyleName(RowIndex.ROW_4, ColumnIndex.COLUMN_3, Styles.INSTANCE.common().textRed());
		cf.addStyleName(RowIndex.ROW_1, ColumnIndex.COLUMN_6, Styles.INSTANCE.common().textRed());
		cf.addStyleName(RowIndex.ROW_2, ColumnIndex.COLUMN_6, Styles.INSTANCE.common().textRed());
		
		cf.setWidth(RowIndex.ROW_1, ColumnIndex.COLUMN_1, Sizes.toString(COLUMN_WIDTH));
		cf.setWidth(RowIndex.ROW_1, ColumnIndex.COLUMN_2, Sizes.toString(COLUMN_WIDTH));
		cf.setWidth(RowIndex.ROW_1, ColumnIndex.COLUMN_4, Sizes.toString(COLUMN_WIDTH));
		cf.setWidth(RowIndex.ROW_1, ColumnIndex.COLUMN_5, Sizes.toString(COLUMN_WIDTH));

		add(table);
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
		isConfigurationEnabled.setValue(swarmConfiguration.isEnabled());
		groupName.setText(swarmConfiguration.getGroupName());
		groupPassword.setText(swarmConfiguration.getGroupPassword());
		confirmedGroupPassword.setText(swarmConfiguration.getGroupPassword());
		port.setText(String.valueOf(swarmConfiguration.getPort()));
		user.setText(swarmConfiguration.getUser());
		if (swarmConfiguration.getLastModified() != null){
			lastModified.setText(JemConstants.DATE_TIME_FULL.format(swarmConfiguration.getLastModified()));
		}
	}

	private void clearErrors() {
		clearError(RowIndex.ROW_2, ColumnIndex.COLUMN_3, groupName);
		clearError(RowIndex.ROW_3, ColumnIndex.COLUMN_3,  groupPassword);
		clearError(RowIndex.ROW_4, ColumnIndex.COLUMN_3, confirmedGroupPassword);
		clearError(RowIndex.ROW_1, ColumnIndex.COLUMN_6, port);
	}

	/**
	 * @return
	 */
	public boolean validate() {
		clearErrors();
		boolean ok = true;

		if ((groupName.getText() == null) || groupName.getText().length() == 0) {
			setError(RowIndex.ROW_2, ColumnIndex.COLUMN_3, groupName, "Group name can not be empty");
			ok = false;
		} else {
			swarmConfiguration.setGroupName(groupName.getText());
		}

		if ((groupPassword.getText() == null)
				|| groupPassword.getText().length() == 0) {
			setError(RowIndex.ROW_3, ColumnIndex.COLUMN_3, groupPassword, "Group password can not be empty");
			ok = false;
		} else {
			swarmConfiguration.setGroupPassword(groupPassword.getText());
		}

		if ((confirmedGroupPassword.getText() == null)
				|| !groupPassword.getText().equals(
						confirmedGroupPassword.getText())) {
			setError(RowIndex.ROW_4, ColumnIndex.COLUMN_3, confirmedGroupPassword,
					"Group password is not confirmed");
			ok = false;
		}

		if ((port.getText() == null) || port.getText().length() == 0) {
			setError(RowIndex.ROW_1, ColumnIndex.COLUMN_6, port, "Port can not be empty");
			ok = false;
		} else {
			try {
				int p = Integer.parseInt(port.getText());
				if (p <= 0){
					throw new NumberFormatException();
				}
				swarmConfiguration.setPort(p);
			} catch (NumberFormatException nfe) {
				setError(RowIndex.ROW_1, ColumnIndex.COLUMN_6, port, "Port must be a positive integer");
				ok = false;
			}
		}
		
		if (netInterface.getText() != null && netInterface.getText().length() > 0) {
			if (!netInterface.getText().matches(ADDRESS_PATTERN)){
				setError(RowIndex.ROW_2, ColumnIndex.COLUMN_6, netInterface, "Network interface must a valid ip-address");
				ok = false;
			} else {
				swarmConfiguration.setNetworkInterface(netInterface.getText());
			}
		}
		
		swarmConfiguration.setEnabled(isConfigurationEnabled.getValue());
		return ok;
	}

	/**
	 * 
	 * @param row
	 * @param textBox
	 * @param errorMsg
	 */
	private void setError(int row, int col, TextBox textBox, String errorMsg) {
		table.setHTML(row, col, errorMsg);
		if (!textBox.getStyleName().contains(
				Styles.INSTANCE.common().textBoxError())) {
			textBox.addStyleName(Styles.INSTANCE.common().textBoxError());
		}
	}

	/**
	 * 
	 * @param row
	 * @param textBox
	 * @param errorMsg
	 */
	private void clearError(int row, int col, TextBox textBox) {
		table.setHTML(row, col, "");
		if (textBox.getStyleName().contains(
				Styles.INSTANCE.common().textBoxError())) {
			textBox.removeStyleName(Styles.INSTANCE.common().textBoxError());
		}
	}

}
