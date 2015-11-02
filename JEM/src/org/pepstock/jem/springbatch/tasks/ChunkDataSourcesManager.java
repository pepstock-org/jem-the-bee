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
package org.pepstock.jem.springbatch.tasks;

import java.io.File;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;

/**
 * Manages the JNDI context for Chunks. Be aware that FTP resources (defined out-of-the-box) couldn't be used here.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public final class ChunkDataSourcesManager {

	/**
	 * To avoid any instantiation
	 */
	private ChunkDataSourcesManager() {

	}

	/**
	 * Creates a JNDI context with all resources defined for chunk and usable inside of ItemReader, ItemProcessor and ItemWriter.
	 * 
	 * @param dataSourceList list of data sources definitions
	 * @return JNDI context
	 * @throws SpringBatchException if any excetpion occurs
	 * @throws NamingException if any excetpion occurs
	 * @throws UnknownHostException if any excetpion occurs
	 * @throws RemoteException if any excetpion occurs
	 */
	static InitialContext createJNDIContext(List<DataSource> dataSourceList) throws SpringBatchException, NamingException, RemoteException, UnknownHostException {
		// new initial context for JNDI
		InitialContext ic = ContextUtils.getContext();
		// loads IC
		loadJNDIContext(ic, dataSourceList);
		return ic;
	}

	
	/**
	 * Loads a JNDI context with all resources defined for chunk and usable inside of ItemReader, ItemProcessor and ItemWriter.
	 * @param ic JNDI context, already created
	 * @param dataSourceList list of data sources definitions
	 * @throws SpringBatchException if any excetpion occurs
	 * @throws NamingException if any excetpion occurs
	 * @throws UnknownHostException if any excetpion occurs
	 * @throws RemoteException if any excetpion occurs
	 */
	static void loadJNDIContext(InitialContext ic, List<DataSource> dataSourceList) throws SpringBatchException, NamingException, RemoteException, UnknownHostException {
		SpringBatchSecurityManager batchSM = (SpringBatchSecurityManager) System.getSecurityManager();
		// scans all datasource passed
		for (DataSource source : dataSourceList) {
			// checks if datasource is well defined
			if (source.getResource() == null) {
				throw new SpringBatchException(SpringBatchMessage.JEMS016E);
			} else if (source.getName() == null) {
				// if name is missing, it uses the same string 
				// used to define the resource
				source.setName(source.getResource());
			}

			// gets the RMi object to get resources
			CommonResourcer resourcer = InitiatorManager.getCommonResourcer();
			// lookups by RMI for the database
			Resource res = resourcer.lookup(JobId.VALUE, source.getResource());
			if (!batchSM.checkResource(res)) {
				throw new SpringBatchException(SpringBatchMessage.JEMS017E, res.toString());
			}

			// all properties create all StringRefAddrs necessary
			Map<String, ResourceProperty> properties = res.getProperties();

			// scans all properteis set by JCL
			for (Property property : source.getProperties()) {
				if (property.isCustom()){
					if (res.getCustomProperties() == null){
						res.setCustomProperties(new PropertiesWrapper());
					}
					if (!res.getCustomProperties().containsKey(property.getName())){
						res.getCustomProperties().put(property.getName(), property.getValue());
					} else {
						throw new SpringBatchException(SpringBatchMessage.JEMS018E, property.getName(), res);	
					}
				} else {
					// if a key is defined FINAL, throw an exception
					for (ResourceProperty resProperty : properties.values()) {
						if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()) {
							throw new SpringBatchException(SpringBatchMessage.JEMS018E, property.getName(), res);
						}
					}
					ResourcePropertiesUtil.addProperty(res, property.getName(), property.getValue());
				}
			}
			// creates a JNDI reference
			Reference ref = null;
			try {
				ref = resourcer.lookupReference(JobId.VALUE, res.getType());
				if (ref == null) {
					throw new SpringBatchException(SpringBatchMessage.JEMS019E, res.getName(), res.getType());
				}
			} catch (Exception e) {
				throw new SpringBatchException(SpringBatchMessage.JEMS019E, e, res.getName(), res.getType());
			}
			

			// loads all properties into RefAddr
			for (ResourceProperty property : properties.values()) {
				ref.add(new StringRefAddr(property.getName(), replaceProperties(property.getValue())));
			}
			
			// loads custom properties in a string format
			if (res.getCustomProperties() != null && !res.getCustomProperties().isEmpty()){
				// loads all entries and substitute variables
				for (Entry<String, String> entry : res.getCustomProperties().entrySet()){
					String value = replaceProperties(entry.getValue());
					entry.setValue(value);
				}
				// adds to reference
				ref.add(new StringRefAddr(CommonKeys.RESOURCE_CUSTOM_PROPERTIES, res.getCustomPropertiesString()));	
			}

			// binds the object with format {type]/[name]
			LogAppl.getInstance().emit(SpringBatchMessage.JEMS024I, res);
			ic.rebind(source.getName(), ref);
		}
	}
	
	/**
	 * Replaces inside of property value system variables or properties loaded by Spring
	 * @param value property value to change
	 * @return value changed
	 */
	private static String replaceProperties(String value){
		String changed = null;
		// if property starts with jem.data
		// I need to ask to DataPaths Container in which data path I can put the file
		if (value.startsWith("${"+ConfigKeys.JEM_DATA_PATH_NAME+"}")){
			// takes the rest of file name
			String fileName = StringUtils.substringAfter(value, "${"+ConfigKeys.JEM_DATA_PATH_NAME+"}");
			// checks all paths
			try {
				// gets datapaths
				PathsContainer paths = DataPathsContainer.getInstance().getPaths(fileName);
				// is relative!
				// creates a file with dataPath as parent, plus file name  
				File file = new File(paths.getCurrent().getContent(), fileName);
				// the absolute name of the file is property value
				changed = file.getAbsolutePath();
			} catch (InvalidDatasetNameException e) {
				throw new BuildException(e);
			}
		} else {
			// uses SB utilities to changed all properties
			changed = JobsProperties.getInstance().replacePlaceHolders(value);
		}
		return changed;
	}


	/**
	 * Clears all JDNI defintions
	 * @param context JNDI context to clear
	 * @param dataSourceList list of datasources to unbind
	 */
	static void clearJNDIContext(InitialContext context, List<DataSource> dataSourceList){
		for (DataSource source : dataSourceList){
			// unbinds all resources
			try {
				context.unbind(source.getName());
			} catch (NamingException e) {
				// ignore
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
	}
	
	
}
