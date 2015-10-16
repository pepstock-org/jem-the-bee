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
package org.pepstock.jem.gwt.client;


import org.pepstock.jem.gwt.client.commons.ImageAndTextButton;
import org.pepstock.jem.gwt.client.commons.ImageAndTextButton.TextPosition;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.SharedObjects;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.TimeDisplayUtils;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.services.InfoService;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;
import org.pepstock.jem.util.TimeUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

/**
 * Header component with JEM logo and user information. 
 * @author Andrea "Stock" Stocchero
 *
 */
public class SmallHeader extends Grid {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.header().ensureInjected();
	}

	/**
	 * The interval between header information refresh
	 */
	protected static final int ENV_BOX_UPDATE_INTERVAL = (int) (1 * TimeUtils.MINUTE);
	
	private static final int COLSPAN = 4;
	private static final int ROWS = 1;
	private static final int COLUMNS = 3;
	
	private final FlexTable environmentInfoBox = new FlexTable();
	private final Image logoImage = new Image(Images.INSTANCE.logoSmall());
	private final EnvironmentBoxUpdateTimer envBoxUpdateTimer = new EnvironmentBoxUpdateTimer();
	private final ImageAndTextButton btnLogout = new ImageAndTextButton(Images.INSTANCE.powerOff32(), "Logoff", TextPosition.BOTTOM);
	
	/**
	 * Contrsucts all components
	 */
	public SmallHeader() {
		/*
		 *	-------------------------------------
		 *  | LOGO | ENV INFO    	| USER INFO |
		 *  ------------------------------------- 
		 */
		//1 row, 3 column
		super(ROWS, COLUMNS);	
		setWidth(Sizes.HUNDRED_PERCENT);
		addStyleName(Styles.INSTANCE.common().noWrap());
		getColumnFormatter().setWidth(ColumnIndex.COLUMN_2, "50%");
		getColumnFormatter().setWidth(ColumnIndex.COLUMN_3, "50%");
		getRowFormatter().setVerticalAlign(RowIndex.ROW_1, HasVerticalAlignment.ALIGN_MIDDLE);
		
		
		// load images. see Resources Bundle
		setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_1, logoImage);

		// environment box
		environmentInfoBox.addStyleName(Styles.INSTANCE.header().infoBox());
		
		ColumnFormatter eibColumnFormatter = environmentInfoBox.getColumnFormatter();
		eibColumnFormatter.setWidth(ColumnIndex.COLUMN_1, "25%");
		eibColumnFormatter.setWidth(ColumnIndex.COLUMN_2, "25%");
		eibColumnFormatter.setWidth(ColumnIndex.COLUMN_3, "25%");
		eibColumnFormatter.setWidth(ColumnIndex.COLUMN_4, "25%");
		
		FlexCellFormatter eibCellFormatter = environmentInfoBox.getFlexCellFormatter();
		/*
		 * --------------------------------------
		 * | JEM Env Info:						|
		 * | Key0	Value0		Key1	Value1  |
		 * | Key2 	Value2		Key3	Value3	|
		 * --------------------------------------	
		 */
		eibCellFormatter.setColSpan(RowIndex.ROW_1, ColumnIndex.COLUMN_1, COLSPAN);

		environmentInfoBox.setText(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "JEM environment overview: ");
		eibCellFormatter.addStyleName(RowIndex.ROW_1, ColumnIndex.COLUMN_1, Styles.INSTANCE.header().infoBoxHeader());
		environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "Name");
		environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_3, "Nodes");
		environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_1, "Jobs in execution");
		environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_3, "Uptime");
		UITools.setColumnKeyValueStyle(environmentInfoBox, Styles.INSTANCE.common().bold(), true);
		setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_2, environmentInfoBox);

		// initial state
		//env name
		environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_2, JemConstants.UPDATING_BRACKETS);	
		//node number (web)
		environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_4, JemConstants.UPDATING_BRACKETS);
		//job in execution
		environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_2, JemConstants.UPDATING_BRACKETS);	
		//uptime
		environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_4, JemConstants.UPDATING_BRACKETS);		
		
		// schedule the update timer
		envBoxUpdateTimer.run();
		envBoxUpdateTimer.scheduleRepeating(ENV_BOX_UPDATE_INTERVAL);

		// user info box (contains logout button)
		/* 
		 * ----------------------------------------------
		 * | User Info:									|
		 * | Key0	Value0		Key1 	Value1	LOGOFF	|
		 * | Key2	Value2		Key3	Value3  BUTTON	|
		 * ----------------------------------------------
		 */
		FlexTable userInfoBox = new FlexTable();
		userInfoBox.addStyleName(Styles.INSTANCE.header().infoBox());
		
		ColumnFormatter uibColumnFormatter = userInfoBox.getColumnFormatter();
		uibColumnFormatter.setWidth(ColumnIndex.COLUMN_1, "25%");
		uibColumnFormatter.setWidth(ColumnIndex.COLUMN_2, "25%");
		uibColumnFormatter.setWidth(ColumnIndex.COLUMN_3, "25%");
		uibColumnFormatter.setWidth(ColumnIndex.COLUMN_4, "25%");
		
		FlexCellFormatter uibCellFormatter = userInfoBox.getFlexCellFormatter();
		uibCellFormatter.setColSpan(RowIndex.ROW_1, ColumnIndex.COLUMN_1, 5);
		uibCellFormatter.setRowSpan(RowIndex.ROW_1, ColumnIndex.COLUMN_5, 3);
		uibCellFormatter.setAlignment(RowIndex.ROW_1, ColumnIndex.COLUMN_5, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		userInfoBox.setText(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "User information: ");
		uibCellFormatter.addStyleName(RowIndex.ROW_1, ColumnIndex.COLUMN_1, Styles.INSTANCE.header().infoBoxHeader());
		LoggedUser user = CurrentUser.getInstance().getUser();
		userInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "ID");
		uibCellFormatter.addStyleName(RowIndex.ROW_2, ColumnIndex.COLUMN_1, Styles.INSTANCE.common().bold());
		userInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_2, user.getId());

		userInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_3, "Org. Unit ID");
		uibCellFormatter.addStyleName(RowIndex.ROW_2, ColumnIndex.COLUMN_3, Styles.INSTANCE.common().bold());
		userInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_3, "Org. Unit Name");
		uibCellFormatter.addStyleName(RowIndex.ROW_3, ColumnIndex.COLUMN_3, Styles.INSTANCE.common().bold());
		
		userInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_1, "Name");
		uibCellFormatter.addStyleName(RowIndex.ROW_3, ColumnIndex.COLUMN_1, Styles.INSTANCE.common().bold());
		userInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_2, user.getName());
		
		String orgUnitId = JemConstants.NONE_BRACKETS;
		String orgUnitName = JemConstants.NONE_BRACKETS;
		if (user.getOrganizationalUnit() != null) {
			if (!(user.getOrganizationalUnit().getId() == null || user.getOrganizationalUnit().getId().isEmpty())) {
				orgUnitId = user.getOrganizationalUnit().getId();
			}
			if (!(user.getOrganizationalUnit().getName() == null || user.getOrganizationalUnit().getName().isEmpty())) {
				orgUnitName = user.getOrganizationalUnit().getName();
			}
		}
		userInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_4, orgUnitId);
		userInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_4, orgUnitName);
		
		// logoff button (and handler)
		btnLogout.addClickHandler(new LogoutClickHandler());
		userInfoBox.setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_5, btnLogout);
		setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_3, userInfoBox);
	}
	
	private class LogoutClickHandler implements ClickHandler {
		
		@Override
		public void onClick(ClickEvent event) {
			btnLogout.setEnabled(false);
			
			// call LOGIN manager to forse the logoff on server
			Services.LOGIN_MANAGER.logoff(CurrentUser.getInstance().getUser().getPreferences(), new LogoffAsynchCallback());
		}
		
		private class LogoffAsynchCallback extends ServiceAsyncCallback<Boolean> {
			
			@Override
			public void onJemFailure(Throwable caught) {
				new Toast(MessageLevel.ERROR, caught.getMessage(), "Logout error!").show();
				btnLogout.setEnabled(true);
			}

			@Override
			public void onJemSuccess(Boolean result) {
				// put  a NULL the common instance of user
				CurrentUser.getInstance().setUser(null);
				// reload the page so ask again the login
				Window.Location.reload();
			}

			@Override
            public void onJemExecuted() {
				// ignore
            }
		}
	}
	
	
	private class EnvironmentBoxUpdateTimer extends Timer {
		
		@Override
		public void run() {
			environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_2, JemConstants.UPDATING_BRACKETS);
			environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_4, JemConstants.UPDATING_BRACKETS);
			environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_2, JemConstants.UPDATING_BRACKETS);
			environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_4, JemConstants.UPDATING_BRACKETS);
			
			Services.INFO_SERVICE.getEnvironmentInformation(new GetEnvironmentInformationAsyncCallback(System.currentTimeMillis()));
		}
		
		private class GetEnvironmentInformationAsyncCallback extends ServiceAsyncCallback<String[]> {
			
			private final long start;
			
			private GetEnvironmentInformationAsyncCallback(long start) {
				this.start = start;
			}
			
			@Override
			public void onJemSuccess(String[] result) {
				environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_2, result[InfoService.Indexes.NAME.getIndex()]);
				environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_4, result[InfoService.Indexes.NODES_COUNT.getIndex()]);
				environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_2, result[InfoService.Indexes.EXECUTION_JOB_COUNT.getIndex()]);
				try {
					long elapsed = System.currentTimeMillis() - start;
					SharedObjects.setClusterDifferenceTime(System.currentTimeMillis()-Long.parseLong(result[InfoService.Indexes.CURRENT_TIME.getIndex()]) - elapsed);
					SharedObjects.setExecutionEnvironment(result[InfoService.Indexes.NAME.getIndex()]);
					
					Long uptime = Long.parseLong(result[InfoService.Indexes.STARTED_TIME.getIndex()]);
					String readableUptime = TimeDisplayUtils.getReadableTimeDiff(uptime, TimeDisplayUtils.VERBOSE);
					environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_4, readableUptime);
				} catch (Exception e) {
					LogClient.getInstance().warning(e.getMessage(), e);
					environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_4, JemConstants.UNAVAILABLE_BRACKETS);
				}
				
			}
			
			@Override
			public void onJemFailure(Throwable caught) {
				environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_2, JemConstants.UNAVAILABLE_BRACKETS);
				environmentInfoBox.setText(RowIndex.ROW_2, ColumnIndex.COLUMN_4, JemConstants.UNAVAILABLE_BRACKETS);
				environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_2, JemConstants.UNAVAILABLE_BRACKETS);
				environmentInfoBox.setText(RowIndex.ROW_3, ColumnIndex.COLUMN_4, JemConstants.UNAVAILABLE_BRACKETS);
			}

			@Override
            public void onJemExecuted() {
				// do nothing
            }
		}
	}
	
}