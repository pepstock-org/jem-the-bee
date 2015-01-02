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
package org.pepstock.jem.gwt.client.security;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.UserPreference;
import org.pepstock.jem.util.TimeUtils;

import com.google.gwt.user.client.Timer;

/**
 * Contains the logged user.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class CurrentUser {
	
	private static final CurrentUser CURRENT_USER =  new CurrentUser();
	
	/**
	 * The interval between header information refresh
	 */
	protected static final int PREFERENCES_STORE_INTERVAL = (int) (30 * TimeUtils.SECOND);
	
	private long previousUpdateTime = 0L;
	
	private long lastUpdateTime = 0L;
	
	/**
	 * Logged User instance! used to check permissions 
	 */
	private LoggedUser user = null;
	
	private CurrentUser(){
		Timer preferencesStoreTimer = new Timer() {

			@Override
			public void run() {
				if (user != null){
					storePreferences();
				}
			}
		}; 
		preferencesStoreTimer.run();
		preferencesStoreTimer.scheduleRepeating(PREFERENCES_STORE_INTERVAL);
	}
	
	/**
	 * Stores the user preferences
	 */
	public void storePreferences(){
		if (previousUpdateTime != lastUpdateTime){
			Services.LOGIN_MANAGER.storePreferences(CurrentUser.getInstance().getUser().getPreferences(), new ServiceAsyncCallback<Boolean>() {

				@Override
				public void onJemFailure(Throwable caught) {
					LogClient.getInstance().warning("Exception while saving user preferences", caught);
				}

				@Override
				public void onJemSuccess(Boolean result) {
					previousUpdateTime = lastUpdateTime;
				}

				@Override
                public void onJemExecuted() {
					// ignore
                }
			});
		}
	}
	
	/**
	 * @return
	 */
	public static final CurrentUser getInstance(){
		return CURRENT_USER;
	}

	/**
	 * @return the uSER
	 */
	public LoggedUser getUser() {
		return user;
	}

	/**
	 * @param user the uSER to set
	 */
	public void setUser(LoggedUser user) {
		this.user = user;
	}

	/**
	 * Returns the preference value of the provided key
	 * @param key the preference key
	 * @return the value associated to the key 
	 */
	public String getStringPreference(String key){
		if (key != null){
			UserPreference pref = CurrentUser.getInstance().getUser().getPreferences().get(key);
			if (pref != null && pref.isValueString()){
				return pref.getValueString();
			}
		}
		return null;
	}
	
	/**
	 * Returns the preference values of the provided key
	 * @param key the preference key
	 * @return the values associated to the key
	 */
	public List<String> getListPreference(String key){
		if (key != null){
			UserPreference pref = CurrentUser.getInstance().getUser().getPreferences().get(key);
			if (pref != null && !pref.isValueString()){
				return pref.getValueList();
			}
		}
		return new LinkedList<String>();
	}
	
	/**
	 * Set the value of the preference identified by key
	 * @param key the preference key
	 * @param value the preference value
	 */
	public void setStringPreference(String key, String value){
		if (key != null){
			UserPreference pref = CurrentUser.getInstance().getUser().getPreferences().get(key);
			if (pref == null){
				pref = new UserPreference();
			}
			pref.setValueString(value);
			getUser().getPreferences().put(key, pref);
			setLastUpdateTime();
		}
	}
	
	/**
	 * Set the values of the preference identified by key 
	 * @param key the preference key
	 * @param values the preference values
	 */
	public void setListPreference(String key, List<String> values){
		if (key != null){
			UserPreference pref = new UserPreference();
			pref.setValueList(values);
			getUser().getPreferences().put(key, pref);
			setLastUpdateTime();
		}
	}
	
	/**
	 * @return the lastUpdateTime
	 */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * the lastUpdateTime to set
	 */
	public void setLastUpdateTime() {
		this.lastUpdateTime = System.currentTimeMillis();
	}

}
