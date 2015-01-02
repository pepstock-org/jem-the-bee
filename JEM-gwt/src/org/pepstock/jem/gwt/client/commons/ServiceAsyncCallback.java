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
package org.pepstock.jem.gwt.client.commons;

import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.log.JemException;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Custom service async call back to catch exception and check if we have JemException (which occurs when you have a 
 * RPC exception due to JEM) or another exception (usually which occurs when the app server is down).  
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * @param <T>
 */
public abstract class ServiceAsyncCallback<T> implements AsyncCallback<T> {
	
	/**
	 * Called when RPC service ends correctly
	 * 
	 * @param result object returned from service
	 */
	public abstract void onJemSuccess(T result);

	/**
	 * Called when a JemException occurs
	 * 
	 * @param caught Jem exception
	 */
	public abstract void onJemFailure(Throwable caught);
	
	/**
	 * Called to perform commons actions, in case of both success and failure, for instance removing loading panel.
	 */
	public abstract void onJemExecuted();
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
	 */
    @Override
    public final void onFailure(Throwable caught) {
    	LogClient.getInstance().warning(caught.getMessage(), caught);
    	onJemExecuted();
    	if (caught instanceof JemException){
    		onJemFailure(caught);	
    	} else {
    		ReloadConfirmMessageBox rcmb = new ReloadConfirmMessageBox("SEVERE internal error", 
    				"Impossible to execute any RPC calls.<br>Probably application server is down.<br>Please reload the application.");
    		rcmb.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton buttonPressed) {
					// reload the page so ask again the login
					Window.Location.reload();
				}
			});
    		rcmb.open();
    	}
    }

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
	 */
    @Override
    public final void onSuccess(T result) {
    	onJemExecuted();
	    onJemSuccess(result);
    }
}
