/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.resources;

import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.NewResourceHeader;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.Resource;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * An inspector that let the user to view, modify and save a new {@link Resource}, with same properties of an existing one 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class CloneResourceInspector extends ExistingResourceInspector {

	/**
	 * Builds a {@link CloneResourceInspector}
	 * @param resource the cloned resource
	 */
	public CloneResourceInspector(Resource resource) {
		super(resource);
		// clean resource name to let user choose a new one
		getResource().setName(null);
	}

	@Override
	public void save() {
		if (checkMandatoryAttributes()) {
			// checks if the name is valid for new resource
			if (getResource().getName() != null && !getResource().getName().trim().isEmpty()) {
				Services.COMMON_RESOURCES_MANAGER.addCommonResource(getResource(), new ServiceAsyncCallback<Boolean>() {
					@Override
					public void onJemSuccess(Boolean result) {
						// do nothing
					}
	
					@Override
					public void onJemFailure(Throwable caught) {
						new Toast(MessageLevel.ERROR, caught.getMessage(), "Add resource command error!").show();
					}

					@Override
                    public void onJemExecuted() {
						// hide the popup
						hide(); 
                    }
				});
			} else {
				new Toast(MessageLevel.ERROR, "Please type a valid not-empty resource name", "Invalid resource name!").show();
			}
		}
	}

	@Override
	public FlexTable getHeader() {
		return new NewResourceHeader(getResource(), this);
	}
	
}
