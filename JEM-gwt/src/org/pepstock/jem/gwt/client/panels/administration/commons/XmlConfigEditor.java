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
package org.pepstock.jem.gwt.client.panels.administration.commons;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.editor.Editor;
import org.pepstock.jem.gwt.client.editor.actions.Discard;
import org.pepstock.jem.gwt.client.editor.actions.Indent;
import org.pepstock.jem.gwt.client.editor.actions.SelectAll;
import org.pepstock.jem.gwt.client.editor.modifiers.XmlModifier;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.ConfigurationFile;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Component that edits XML configuration file, using ACE editor
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class XmlConfigEditor extends XmlModifier implements AdminEditor{
	
    // SAVE disabled
    private static final Image IMG_SAVED_DISABLED = new Image(Images.INSTANCE.editSaveDisabled());
    // SAVE enabled
    private static final Image IMG_SAVE = new Image(Images.INSTANCE.editSave());
    
    static {
	    IMG_SAVED_DISABLED.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
	    IMG_SAVE.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    }
    
	private String editType = null;
	
	private String description = null;
	
	private ConfigurationFile configurationFile = null;
	
	private MenuItem save = null;
	
	/**
	 * Constructs editor
	 * 
	 * @param id element ID of editor
	 * @param editType type of file to edit
	 * @param description description of file 
	 * 
	 */
	public XmlConfigEditor(String id, String editType, String description) {
		super(id);
		this.editType = editType;
		this.description = description;
	}
	
	/**
	 * Returns configuration file
	 * @return the configurationFile
	 */
	public ConfigurationFile getConfigurationFile() {
		return configurationFile;
	}

	/**
	 * Sets configuration file
	 * @param configurationFile the configurationFile to set
	 */
	public void setConfigurationFile(ConfigurationFile configurationFile) {
		this.configurationFile = configurationFile;
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.administration.commons.AdminEditor#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.administration.commons.AdminEditor#setDescription(String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
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
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setMenuItems(com.google.gwt.user.client.ui.MenuBar)
	 */
    @Override
    public void setMenuItems(MenuBar menu) {
    	// adds actions
    	Discard discard = new Discard(this);
    	Indent indent = new Indent(this);
    	SelectAll selectAll = new SelectAll(this);

    	// adds content check (no syntax)
	    Image imgSelectAll = new Image(Images.INSTANCE.editCheck());
	    imgSelectAll.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
	    MenuItem check = new MenuItem(imgSelectAll +" Check", true, new Command() {
			@Override
			public void execute() {
				if (getEditor().getText().trim().length() > 0){
					check();
				} else {
					new Toast(MessageLevel.WARNING, "XML content of "+getDescription()+" is empty. Check is not allowed!", "Unable to check XML content!").show();
				}

			}
		});
	    check.getElement().getStyle().setFontWeight(FontWeight.NORMAL);

		menu.addItem(selectAll.getItem());
		menu.addItem(indent.getItem());
		menu.addItem(check);

	    // SAVE action
	    save = new MenuItem(IMG_SAVED_DISABLED+" Save", true, new Command() {
			@Override
			public void execute() {
				if (getEditor().getText().trim().length() > 0){
					configurationFile.setContent(getEditor().getText());
					save();
				} else {
					new Toast(MessageLevel.WARNING, "XML content of "+getDescription()+" is empty. Check is not allowed!", "Unable to check XML content!").show();
				}
			}
		});
	    save.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
	    onChange(false);
	    
	    menu.addSeparator();
	    menu.addItem(discard.getItem());
	    menu.addItem(save);
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#onCHange(boolean)
	 */
    @Override
    public void onChange(boolean changed){
    	// when the text is changed, changes the icon and color
    	// of text
    	save.setEnabled(changed);
    	if (changed){
    		save.setHTML(IMG_SAVE+" Save");
    		save.getElement().getStyle().setColor("#000");
    	} else {
    		save.setHTML(IMG_SAVED_DISABLED+" Save");
    		save.getElement().getStyle().setColor("#ccc");
    	}
    }
    
    /**
     * Check remotely if config file is consistent
     */
    private void check(){
 		Loading.startProcessing();
 		
 	    Scheduler scheduler = Scheduler.get();
 	    scheduler.scheduleDeferred(new ScheduledCommand() {
 			
 			@Override
 			public void execute() {
 				Services.NODES_MANAGER.checkConfigFile(getEditor().getText(), editType, new CheckConfigFileAsyncCallback());
 			
 			}
 	    });
     }

    private class CheckConfigFileAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			new Toast(MessageLevel.INFO, getDescription()+" has been correctly checked.", "Content checked!").show();
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.INFO, getDescription()+": "+caught.getMessage(), "Content check error!").show();
		}
			
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
    	
    }
    
    /**
     * Save remotely the content of configuration file
     */
    public void save(){
 		Loading.startProcessing();
 		
 	    Scheduler scheduler = Scheduler.get();
 	    scheduler.scheduleDeferred(new ScheduledCommand() {
 			
 			@Override
 			public void execute() {
 				Services.NODES_MANAGER.saveEnvConfigFile(getConfigurationFile(), editType, new SaveEnvConfigFile());
 			}
 	    });
    	
    }
    
    private class SaveEnvConfigFile extends ServiceAsyncCallback<ConfigurationFile> {
		@Override
		public void onJemSuccess(ConfigurationFile result) {
			setConfigurationFile(result);
			setContent(result.getContent());
			setChanged(false);
			new Toast(MessageLevel.INFO, getDescription()+" has been correctly saved.", "Content saved!").show();
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, getDescription()+": "+caught.getMessage(), "Content save error!").show();
		}
			
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
    }
    
}