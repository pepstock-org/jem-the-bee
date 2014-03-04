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
package org.pepstock.jem.gwt.client.panels.resources.inspector;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.gwt.client.commons.CSVUtil;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.jppf.NetworkEditConfig;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.JdbcResource;
import org.pepstock.jem.node.resources.JppfResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public final class JPPFAttributesPanel  extends ResourcesPropertiesPanel{

	private NetworkEditConfig config = null;
	
	private List<String> list = new ArrayList<String>();
	
	/**
	 * @param resource 
	 * 
	 */
	public JPPFAttributesPanel(Resource resource) {
		super(resource, true);
	    loadProperties();
	    config = new NetworkEditConfig(list);
	    add(config);
	}

	/**
	 * 
	 */
	public final void loadProperties(){
		for (ResourceProperty property : getResource().getProperties().values()){
			if (property.getName().equalsIgnoreCase(JppfResource.ADDRESSES)){
				String[] addresses = CSVUtil.splitAndTrim(property.getValue());
				if (addresses != null){
					for (int i=0; i<addresses.length; i++){
						list.add(addresses[i]);	
					}
				}
			}
		}
	}
	
	/**
	 * Check for mandatory attributes.
	 */
	public boolean checkMandatory(){

		if (list.isEmpty()){
			new Toast(MessageLevel.ERROR, "No address has been defined. You must define one address at least.", "No address").show();
			return false;
		}
		
		setPropertyValue(JppfResource.ADDRESSES, CSVUtil.getCSVPhrase(list));
		return true;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	/**
	 * Initialize the {@link ResourcesPropertiesPanel#getResource()} in case
	 * of creation (not editing) of a new {@link JdbcResource}. 
	 */
	public void initializeResource(){
	    //is NEW?
		getResource().setType(JppfResource.TYPE);
	}
	
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	config.onResize(availableWidth, availableHeight);
    }
}