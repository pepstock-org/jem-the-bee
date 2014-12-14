/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.plugin.event.PreferencesEnvironmentEventListener;

import com.thoughtworks.xstream.XStream;

/**
 * Preferences Manager which loads and stores preferences.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class PreferencesManager {

	private static final XStream STREAM = new XStream();
	
	private static final ISecurePreferences PREFERENCES = SecurePreferencesFactory.getDefault();
	
	private static final String PREFERENCES_NODES_ENVIRONMENTS = "jem.preferences.nodes.environments";
	
	/**
	 * Nodes on preferences
	 */
	public static final Map<String,Coordinate> ENVIRONMENTS = new ConcurrentHashMap<String,Coordinate>();
	
	private static final List<PreferencesEnvironmentEventListener> ENVIRONMENT_LISTENERS = new ArrayList<PreferencesEnvironmentEventListener>();

	/**
	 * Private constructor to avoid new instantiations 
	 */
	private PreferencesManager() {
	}

	/**
	 * Loads preferences.
	 * @throws StorageException if any Eclipse error occurs
	 */
	public static void load() throws StorageException{
		// clears all environments
		ENVIRONMENTS.clear();
		// check if preferences have got the key used to store coordinates
		if(PREFERENCES.nodeExists(PREFERENCES_NODES_ENVIRONMENTS)){
			// gets preferences
			ISecurePreferences envs = PREFERENCES.node(PREFERENCES_NODES_ENVIRONMENTS);
			String[] keys = envs.keys();
			// scans all keys
			for (int i = 0; i < keys.length; i++) {
				try{
					// gets xml by key
					String xml = envs.get(keys[i], "n/a");
					// de-serializes from XML
					Coordinate cc = (Coordinate)STREAM.fromXML(xml);
					// adds on the enviroments with coordinate name as key
					ENVIRONMENTS.put(cc.getName(),cc);
				} catch(StorageException e){
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// preferences must be encrypted by password
					if (e.getErrorCode() == StorageException.NO_PASSWORD){
						return;
					}
					throw e;
				} catch(Exception e){
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// XML parser error. Ignore
				}
			}
		}
	}
	
	/**
	 * Stores the preferences 
	 * @throws StorageException if any Eclipse error occurs
	 */
	public static void store() throws StorageException{
		// gets the secure preferences
		// and clears, removing all coordinates
		ISecurePreferences envs = PREFERENCES.node(PREFERENCES_NODES_ENVIRONMENTS);
		envs.clear();
		// loads all coordinates as XML 
		for(Coordinate coordinate : ENVIRONMENTS.values()){
			String xml = STREAM.toXML(coordinate);
			envs.put(coordinate.getName(), xml, true);
		}
	}
	
	/**
	 * returns the list of listeners
	 * @return the envlisteners
	 */
	static List<PreferencesEnvironmentEventListener> getEnvlisteners() {
		return ENVIRONMENT_LISTENERS;
	}

	/**
	 * Adds a new environment preferences listener. 
	 * @param listener a preference listener
	 */
	public static void addEnvironmentEventListener(PreferencesEnvironmentEventListener listener){
		ENVIRONMENT_LISTENERS.add(listener);
	}

	/**
	 * Removes a new environment preferences listener.
	 * @param listener a preference listener
	 */
	public static void removeEnvironmentEventListener(PreferencesEnvironmentEventListener listener){
		ENVIRONMENT_LISTENERS.remove(listener);
	}
}
