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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.editor.Mode;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminEditor;
import org.pepstock.jem.gwt.client.panels.administration.commons.EditorContainer;
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
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ConfigPanel extends VerticalPanel implements ResizeCapable, InspectListener<NodeInfoBean> {
	
	private static final String JEM_ID = "jemEditorID";
	
	private static final String AFFINITY_ID = "affinityEditorID";

	private TabPanel tabPanel = new TabPanel();

	private NodeConfigEditor nodeConfig = new NodeConfigEditor(JEM_ID, ConfigKeys.JEM_CONFIG);

	private PolicyEditor affinity = new PolicyEditor(AFFINITY_ID);

	private NodeInfoBean node = null;
	
	private EditorContainer editorContainer = new EditorContainer();
	
	/**
	 * 
	 */
	public ConfigPanel() {
		editorContainer.add(tabPanel);
		// sets editors
		List<AdminEditor> list = new LinkedList<AdminEditor>();
		list.add(nodeConfig);
		list.add(affinity);
		editorContainer.setEditors(list);
	}

	

	/**
	 * @return the editorContainer
	 */
	public EditorContainer getEditorContainer() {
		return editorContainer;
	}



	@Override
	public void inspect(NodeInfoBean object) {
		if (!isVisible()){
			setVisible(true);
		}
		node = object;

		if (tabPanel.getWidgetCount() == 0){

			tabPanel.add(nodeConfig, "Node");
			tabPanel.add(affinity, "Affinity policy");

			tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

				@Override
				public void onSelection(final SelectionEvent<Integer> event) {
					if (!tabPanel.isVisible()) {
						tabPanel.setVisible(true);
					}
					if (event.getSelectedItem() == 0) {
						if (!nodeConfig.isChanged()){
							inspect(event.getSelectedItem());
						}
					} else {
						if (!affinity.isChanged()){
							inspect(event.getSelectedItem());
						}
					}
				}
			});
		}

		
		
		affinity.setNode(object);
		nodeConfig.setNode(object);
		
		nodeConfig.setChanged(false);
		affinity.setChanged(false);
		
		editorContainer.setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				editorContainer.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop());
			}
		});
		
		tabPanel.selectTab(0, true);
	}

	private void inspect(final int what) {
		Loading.startProcessing();
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				// service 0
				Services.NODES_MANAGER.getNodeConfigFile(node, (what == 0) ? ConfigKeys.JEM_CONFIG : ConfigKeys.AFFINITY, new GetNodeConfigFileAsyncCallback(what));
			}
		});
	}

	private class GetNodeConfigFileAsyncCallback extends ServiceAsyncCallback<ConfigurationFile> {
		
		private final int what;
		
		public GetNodeConfigFileAsyncCallback(final int what) {
			this.what = what;
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			if (what == 0) {
				nodeConfig.setContent(null);
				nodeConfig.startEditor();
				nodeConfig.setReadOnly(true);
			} else {
				affinity.setContent(null);
				affinity.startEditor();
				affinity.setReadOnly(true);
			}
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get CONFIG file error!").show();
		}

		@Override
		public void onJemSuccess(ConfigurationFile result) {
			// sets content to a panel to show it
			if (result != null) {
				if (what == 0) {
					nodeConfig.setConfigurationFile(result);
					nodeConfig.setContent(result.getContent());
					nodeConfig.startEditor();
					nodeConfig.setReadOnly(false);
				} else {
					boolean found = false;
					for (Mode mode : Mode.values()){
						if (mode.getName().endsWith(result.getType())){
							found = true;
						}
					}
					affinity.setConfigurationFile(result);
					affinity.setContent(result.getContent());
					if (found){
						affinity.setLanguage(result.getType());
					} else {
						new Toast(MessageLevel.ERROR, "The script language '"+result.getType()+"' is not supported or not configured. Plain text syntax is used.", "Script language unknown!").show();
						affinity.setLanguage(Mode.TEXT.getName());
					}
					affinity.startEditor();
					affinity.setReadOnly(false);
				}
			} else {
				if (what == 0) {
					nodeConfig.setContent(null);
					nodeConfig.startEditor();
					nodeConfig.setReadOnly(true);
				} else {
					affinity.setContent(null);
					affinity.startEditor();
					affinity.setReadOnly(true);
				}
				new Toast(MessageLevel.ERROR, "The result, while retrieving the configuration file, is null", "Get CONFIG file null!").show();						}
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
	@Override
	public void onResize(int availableWidth, int availableHeight) {
		tabPanel.setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));

		int syntaxHighlighterHeight = availableHeight - Sizes.TABBAR_HEIGHT_PX - Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT - Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM;

		int syntaxHighlighterWidth = availableWidth - Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT - Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT - Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM - Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM;

		nodeConfig.onResize(syntaxHighlighterWidth, syntaxHighlighterHeight);
		affinity.onResize(syntaxHighlighterWidth, syntaxHighlighterHeight);
		
		if (editorContainer.isShowing()){
			editorContainer.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop());
		}
	}
}