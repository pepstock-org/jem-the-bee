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
package org.pepstock.jem.gwt.client.panels.administration.clusterconfig;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminEditor;
import org.pepstock.jem.gwt.client.panels.administration.commons.EditorContainer;
import org.pepstock.jem.gwt.client.panels.administration.commons.XmlConfigEditor;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.configuration.ConfigKeys;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component which shows in a tabpanel both JEM environment and Hazelcast configuration file. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ConfigPanel extends VerticalPanel implements ResizeCapable {
	
	// ELEMENT ID for Jem environment
	private static final String JEM_ID = "jemEnvEditorID";
	
	// HAZELCAST ID for Jem environment	
	private static final String HAZELCAST_ID = "hazelcastEditorID";

	private TabPanel tabPanel = new TabPanel();

	private XmlConfigEditor envConfig = new XmlConfigEditor(JEM_ID, ConfigKeys.JEM_ENV_CONF, "JEM environment configuration");

	private XmlConfigEditor hazelcast = new XmlConfigEditor(HAZELCAST_ID, ConfigKeys.HAZELCAST_CONFIG, "Hazelcast configuration");
	
	private EditorContainer editorContainer = new EditorContainer();

	/**
	 * Constructs the panel 
	 */
	public ConfigPanel() {
		editorContainer.add(tabPanel);
		// sets editors
		List<AdminEditor> list = new LinkedList<AdminEditor>();
		list.add(envConfig);
		list.add(hazelcast);
		editorContainer.setEditors(list);
	}

	/**
	 * Load configuration files
	 */
	public void load() {
		// if not visible, set visible
		if (!isVisible()){
			setVisible(true);
		}

		// checks if we are at the first call
		if (tabPanel.getWidgetCount() == 0){
		
			tabPanel.add(envConfig, "Environment");
			tabPanel.add(hazelcast, "Hazelcast");

			tabPanel.addSelectionHandler(new TabPanelSelectionHandler());
		}

		// resets change status to false
		envConfig.setChanged(false);
		hazelcast.setChanged(false);
		
		// shows popup editor panel
		editorContainer.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				editorContainer.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop());
			}
		});
		// loads JEM environment configuration
		tabPanel.selectTab(0, true);
	}

	private class TabPanelSelectionHandler implements SelectionHandler<Integer> {
		@Override
		public void onSelection(final SelectionEvent<Integer> event) {
			if (!tabPanel.isVisible()) {
				tabPanel.setVisible(true);
			}
			if (event.getSelectedItem() == 0) {
				// loads by RPC the config file
				// only if the file is not changed, 
				// otherwise shows the text changed
				if (!envConfig.isChanged()){
					inspect(event.getSelectedItem());
				}
			} else {
				// loads by RPC the config file
				// only if the file is not changed, 
				// otherwise shows the text changed
				if (!hazelcast.isChanged()){
					inspect(event.getSelectedItem());
				}
			}
		}
	}
	
	/**
	 * Loads and show the configuration file
	 * @param what if <code>0</code> loads JEM environment configuration file, <code>1</code> loads Hazelcast configuration file 
	 */
	private void inspect(final int what) {
		Loading.startProcessing();
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				// get configuration file
				Services.NODES_MANAGER.getEnvConfigFile((what == 0) ? ConfigKeys.JEM_ENV_CONF : ConfigKeys.HAZELCAST_CONFIG, new GetEnvConfigFileAsyncCallback(what));
			}
		});
	}

	private class GetEnvConfigFileAsyncCallback extends ServiceAsyncCallback<ConfigurationFile> {
		
		private final int what;
		
		public GetEnvConfigFileAsyncCallback(final int what) {
			this.what = what;
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			String description = (what == 0) ? envConfig.getDescription() : hazelcast.getDescription();
			new Toast(MessageLevel.ERROR, description+": "+caught.getMessage(), "Get CONFIG file error!").show();
		}

		@Override
		public void onJemSuccess(ConfigurationFile result) {
			// sets content to a panel to show it
			if (result != null) {
				if (what == 0) {
					envConfig.setConfigurationFile(result);
					envConfig.setContent(result.getContent());
					envConfig.startEditor();
				} else {
					hazelcast.setConfigurationFile(result);
					hazelcast.setContent(result.getContent());
					hazelcast.startEditor();
				}
			} else {
				String description = (what == 0) ? envConfig.getDescription() : hazelcast.getDescription();
				new Toast(MessageLevel.ERROR, "The result, while retrieving "+description+" file, is null,", "Get CONFIG file null!").show();
			}
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
	@Override
	public void onResize(int availableWidth, int availableHeight) {
		tabPanel.setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));

		// removes tab-panel header, top and bottom padding and 1px (border) for
		// the border of menubar
		int syntaxHighlighterHeight = availableHeight - 
				Sizes.TABBAR_HEIGHT_PX - Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM - Sizes.MAIN_TAB_PANEL_BORDER;

		// removes left and right paddings and 2px additional paddings
		int syntaxHighlighterWidth = availableWidth - 
				Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM - 
				Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM;

		// resizes editor
		envConfig.onResize(syntaxHighlighterWidth, syntaxHighlighterHeight);
		hazelcast.onResize(syntaxHighlighterWidth, syntaxHighlighterHeight);
		
		// if editor is showing, resize teh editors container
		if (editorContainer.isShowing()){
			editorContainer.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop());
		}

	}
}