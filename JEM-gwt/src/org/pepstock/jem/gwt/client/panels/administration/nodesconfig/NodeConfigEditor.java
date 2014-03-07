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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.commons.XmlConfigEditor;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.configuration.ConfigKeys;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class NodeConfigEditor extends XmlConfigEditor {

	private NodeInfoBean node = null;

	/**
	 * @param id
	 * @param editType
	 */
	public NodeConfigEditor(String id, String editType) {
		super(id, editType, "JEM node configuration");
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

    @Override
    public void save(){
 		Loading.startProcessing();
 		
 	    Scheduler scheduler = Scheduler.get();
 	    scheduler.scheduleDeferred(new ScheduledCommand() {
 			
 			@Override
 			public void execute() {
 				Services.NODES_MANAGER.saveNodeConfigFile(getNode(), getConfigurationFile(), ConfigKeys.JEM_CONFIG, new SaveNodeConfigFile());
 			}
 	    });
    	
    }
    
    private class SaveNodeConfigFile extends ServiceAsyncCallback<ConfigurationFile> {
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
