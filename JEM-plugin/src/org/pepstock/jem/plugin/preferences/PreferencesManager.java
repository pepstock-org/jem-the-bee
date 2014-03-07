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
 * Preferences Manager which loads and stores preferences
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
		ENVIRONMENTS.clear();
		if(PREFERENCES.nodeExists(PREFERENCES_NODES_ENVIRONMENTS)){
			ISecurePreferences envs = PREFERENCES.node(PREFERENCES_NODES_ENVIRONMENTS);
			String[] keys = envs.keys();
			for (int i = 0; i < keys.length; i++) {
				String xml;
				try{
					xml = envs.get(keys[i],"n/a");
					Coordinate cc = (Coordinate)STREAM.fromXML(xml);
					ENVIRONMENTS.put(cc.getName(),cc);
				} catch(StorageException e){
					LogAppl.getInstance().ignore(e.getMessage(), e);
					if (e.getErrorCode() == StorageException.NO_PASSWORD){
						return;
					}
					throw e;
				} catch(Exception e){
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// nop
					// messa inc ase che venga cambiato il modello delle coordinate
				}
			}
		}
	}
	
	/**
	 * Stores teh preferences 
	 * @throws StorageException if any Eclipse error occurs
	 */
	public static void store() throws StorageException{
		ISecurePreferences envs = PREFERENCES.node(PREFERENCES_NODES_ENVIRONMENTS);
		envs.clear();
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
