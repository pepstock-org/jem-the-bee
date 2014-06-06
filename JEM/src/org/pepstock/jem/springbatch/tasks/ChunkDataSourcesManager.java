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
package org.pepstock.jem.springbatch.tasks;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.HttpResource;
import org.pepstock.jem.node.resources.JdbcResource;
import org.pepstock.jem.node.resources.JmsResource;
import org.pepstock.jem.node.resources.JppfResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.HttpReference;
import org.pepstock.jem.node.tasks.jndi.JdbcReference;
import org.pepstock.jem.node.tasks.jndi.JmsReference;
import org.pepstock.jem.node.tasks.jndi.JppfReference;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;

/**
 * Manages teh JNDI context for Chunks. Be aware that FTP resources (defined out-of-the-box) couldn't be used here.
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
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.pepstock.jem.node.tasks.jndi.JemContextFactory");
		// new initial context for JNDI
		InitialContext ic = new InitialContext();
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
				// if a key is defined FINAL, throw an exception
				for (ResourceProperty resProperty : properties.values()) {
					if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()) {
						throw new SpringBatchException(SpringBatchMessage.JEMS018E, property.getName(), res);
					}
				}
				res.setProperty(property.getName(), property.getValue());
			}
			// creates a JNDI reference
			Reference ref = null;
			// only JBDC, JMS and FTP types are accepted
			if (res.getType().equalsIgnoreCase(JdbcResource.TYPE)) {
				// creates a JDBC reference (uses DBCP Apache)
				ref = new JdbcReference();

			} else if (res.getType().equalsIgnoreCase(JppfResource.TYPE)) {
				// creates a JPPF reference
				ref = new JppfReference();

			} else if (res.getType().equalsIgnoreCase(JmsResource.TYPE)) {
				// creates a JMS reference (uses javax.jms)
				ref = new JmsReference();

			} else if (res.getType().equalsIgnoreCase(HttpResource.TYPE)) {
				// creates a HTTP reference (uses org.apache.http)
				ref = new HttpReference();

			} else {
				try {
					ref = resourcer.lookupCustomResource(JobId.VALUE, res.getType());
					if (ref == null) {
						throw new SpringBatchException(SpringBatchMessage.JEMS019E, res.getName(), res.getType());
					}
				} catch (Exception e) {
					throw new SpringBatchException(SpringBatchMessage.JEMS019E, e, res.getName(), res.getType());
				}
			}

			// loads all properties into RefAddr
			for (ResourceProperty property : properties.values()) {
				ref.add(new StringRefAddr(property.getName(), property.getValue()));
			}

			// binds the object with format {type]/[name]
			LogAppl.getInstance().emit(SpringBatchMessage.JEMS024I, res);
			ic.rebind(source.getName(), ref);
		}
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
