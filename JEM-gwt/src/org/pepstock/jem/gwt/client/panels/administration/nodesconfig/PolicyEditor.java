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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.editor.Editor;
import org.pepstock.jem.gwt.client.editor.actions.Discard;
import org.pepstock.jem.gwt.client.editor.actions.SelectAll;
import org.pepstock.jem.gwt.client.editor.modifiers.PolicyModifier;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminEditor;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.node.configuration.ConfigKeys;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Component that edits Xml configuration file, using ACE editor
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class PolicyEditor extends PolicyModifier implements AdminEditor{
	
    // SAVE
    private static final Image IMG_SAVE_DISABLED = new Image(Images.INSTANCE.editSaveDisabled());

    private static final Image IMG_SAVE = new Image(Images.INSTANCE.editSave());
    
    static {
	    IMG_SAVE_DISABLED.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
	    IMG_SAVE.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    }
	
	private static final String DESCRIPTION = "Policy";

	private String description = DESCRIPTION;
	
	private NodeInfoBean node = null;
	
	private ConfigurationFile configurationFile = null;
	
	private MenuItem save = null;
	
	/**
	 * Constructs editor
	 * @param id element ID of editor
	 * @param description description about this 
	 * 
	 */
	public PolicyEditor(String id) {
		super(id);
		
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the node
	 */
	public NodeInfoBean getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(NodeInfoBean node) {
		this.node = node;
	}
	
	/**
	 * @return the configurationFile
	 */
	public ConfigurationFile getConfigurationFile() {
		return configurationFile;
	}

	/**
	 * @param configurationFile the configurationFile to set
	 */
	public void setConfigurationFile(ConfigurationFile configurationFile) {
		this.configurationFile = configurationFile;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setEditorAttributes(org.pepstock.jem.gwt.client.editor.Editor)
	 */
    @Override
    public void setEditorAttributes(Editor editor) {
    	super.setEditorAttributes(editor);
    	if (save != null){
    		onChange(false);
    	}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setMenuItems(com.google.gwt.user.client.ui.MenuBar, boolean)
	 */
    @Override
    public void setMenuItems(MenuBar menu) {
    	Discard discard = new Discard(this);
    	SelectAll selectAll = new SelectAll(this);

	    Image imgSelectAll = new Image(Images.INSTANCE.editCheck());
	    imgSelectAll.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
	    MenuItem check = new MenuItem(imgSelectAll +" Check", true, new Command() {
			@Override
			public void execute() {
				if (getNode() != null){
					if (getEditor().getText().trim().length() > 0){
						check();
					} else {
						new Toast(MessageLevel.WARNING, "Policy content is empty. Check is not allowed!", "Unable to check policy content!").show();
					}
				}
			}
		});

	    check.getElement().getStyle().setFontWeight(FontWeight.NORMAL);

	    menu.addItem(selectAll.getItem());
	    menu.addItem(check);
	    
	    // save
	    save = new MenuItem(IMG_SAVE_DISABLED+" Save", true, new Command() {
			@Override
			public void execute() {
				if (getEditor().getText().trim().length() > 0){
					configurationFile.setContent(getEditor().getText());
					save();
				} else {
					new Toast(MessageLevel.WARNING, "Policy is empty. Check is not allowed!", "Unable to check policy!").show();
				}
			}
		});
	    save.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
	    onChange(false);
	    
	    menu.addSeparator();
	    menu.addItem(discard.getItem());
	    menu.addItem(save);

    }
    
    private void check(){
 		Loading.startProcessing();
 		
 	    Scheduler scheduler = Scheduler.get();
 	    scheduler.scheduleDeferred(new ScheduledCommand() {
 			
 			@Override
 			public void execute() {
 				Services.NODES_MANAGER.checkAffinityPolicy(getNode(), getEditor().getText(), new CheckAffinityPolicyAsyncCallback());
 			
 			}
 	    });
     }
    
    private static class CheckAffinityPolicyAsyncCallback extends ServiceAsyncCallback<Result> {

    	@Override
		public void onJemSuccess(Result result) {
			new Toast(MessageLevel.INFO, "Policy has been correctly checked. "+result, "Policy checked!").show();
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Policy check error!").show();
		}
			
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
    	
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#onCHange(boolean)
	 */
    @Override
    public void onChange(boolean changed){
    	save.setEnabled(changed);
    	if (changed){
    		save.setHTML(IMG_SAVE+" Save");
    		save.getElement().getStyle().setColor("#000");
    	} else {
    		save.setHTML(IMG_SAVE_DISABLED+" Save");
    		save.getElement().getStyle().setColor("#ccc");
    	}
    }
    
    private void save(){
 		Loading.startProcessing();
 		
 	    Scheduler scheduler = Scheduler.get();
 	    scheduler.scheduleDeferred(new ScheduledCommand() {
 			
 			@Override
 			public void execute() {
 				Services.NODES_MANAGER.saveNodeConfigFile(getNode(), getConfigurationFile(), ConfigKeys.AFFINITY, new SaveNodeConfigFile());
 			
 			}
 	    });
    }
    
    private class SaveNodeConfigFile extends ServiceAsyncCallback<ConfigurationFile> {
		@Override
		public void onJemSuccess(ConfigurationFile result) {
			setConfigurationFile(result);
			setContent(result.getContent());
			setChanged(false);
			new Toast(MessageLevel.INFO, "Policy has been correctly saved.", "Policy saved!").show();
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Policy save error!").show();
		}
			
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
    }
    
}