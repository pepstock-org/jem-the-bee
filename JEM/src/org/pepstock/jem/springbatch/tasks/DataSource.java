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

import java.io.File;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.resources.impl.jdbc.JdbcFactory;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.SpringBatchRuntimeException;
import org.springframework.jdbc.datasource.AbstractDataSource;


/**
 * Represents a logical name of a database, addressable by name both a java code
 * (JNDI).<br>
 * Example: <br>
 * <code>&lt;dataSource name="jndiname" database="logicalDBname" /&gt;<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataSource extends AbstractDataSource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private String resource = null;
	
	private List<Property> properties = new ArrayList<Property>();
	
	/**
	 * Empty constructor
	 */
	public DataSource() {
	}

	/**
	 * Returns the name of datasource. This is mandatory value because is
	 * used to access to resources by name.
	 * 
	 * @return the name of data description
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of datasource. This is mandatory value because is
	 * used to access to resources by name.
	 * 
	 * @param name the name of data description
	 */
	public void setName(String name) {
		// checks if the name is empty
		if (name.trim().length() == 0){
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS013E);
		}
		this.name = name;
	}

	/**
	 * Returns the name of common resource to access to. This is mandatory value because must
	 * be defined inside of JEM resources.
	 * 
	 * @return the resource name
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the name of common resource to access to. This is mandatory value because must
	 * be defined inside of JEM resources.
	 * @param resource the resource name to set
	 */
	public void setResource(String resource) {
		// checks if the name is empty
		if (resource.trim().length() == 0){
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS014E);
		}
		this.resource = resource;
	}

	/**
	 * Sets properties definition.
	 * @param properties the properties to set
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	/**
	 * Returns the list of properties defined into data source
	 * 
	 * @return the list of properties defined into data source
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return getConnectionImpl();
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnectionImpl();
	}
	
	
	private Connection getConnectionImpl() throws SQLException{
		try {
			SpringBatchSecurityManager batchSM = (SpringBatchSecurityManager)System.getSecurityManager();
			// checks if datasource is well defined
			if (getResource() == null){
				throw new SQLException(SpringBatchMessage.JEMS016E.toMessage().getFormattedMessage());
			} else if (getName() == null) {
				// if name is missing, it uses the same string 
				// used to define the resource
				setName(getResource());
			}
			// gets the RMi object to get resources
			CommonResourcer resourcer = InitiatorManager.getCommonResourcer();
			// lookups by RMI for the database 
			Resource res = resourcer.lookup(JobId.VALUE, getResource());
			if (!batchSM.checkResource(res)){
				throw new SQLException(SpringBatchMessage.JEMS017E.toMessage().getFormattedMessage(res.toString()));
			}
			// all properties create all StringRefAddrs necessary
			Map<String, ResourceProperty> props = res.getProperties();

			// scans all properteis set by JCL
			for (Property property : getProperties()){
				if (property.isCustom()){
					if (res.getCustomProperties() == null){
						res.setCustomProperties(new HashMap<String, String>());
					}
					if (!res.getCustomProperties().containsKey(property.getName())){
						res.getCustomProperties().put(property.getName(), property.getValue());
					} else {
						throw new SQLException(SpringBatchMessage.JEMS018E.toMessage().getFormattedMessage(property.getName(), res));	
					}
				} else {
					// if a key is defined FINAL, throw an exception
					for (ResourceProperty resProperty : props.values()){
						if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()){
							throw new SQLException(SpringBatchMessage.JEMS018E.toMessage().getFormattedMessage(property.getName(), res));
						}
					}
					ResourcePropertiesUtil.addProperty(res, property.getName(), property.getValue());
				}
			}
			// creates a JNDI reference
			Reference ref = null;
			try {
				ref = resourcer.lookupReference(JobId.VALUE, res.getType());
				if (ref == null){
					throw new SQLException(SpringBatchMessage.JEMS019E.toMessage().getFormattedMessage(res.getName(), res.getType()));
				}
			} catch (Exception e) {
				throw new SQLException(SpringBatchMessage.JEMS019E.toMessage().getFormattedMessage(res.getName(), res.getType()), e);
			} 

			// loads all properties into RefAddr
			for (ResourceProperty property : props.values()){
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
			
			JdbcFactory factory = new JdbcFactory();
			javax.sql.DataSource ds = (javax.sql.DataSource)factory.getObjectInstance(ref, null, null, null);
			return ds.getConnection();
		} catch (RemoteException e) {
			throw new SQLException(e.getMessage(), e);
		} catch (UnknownHostException e) {
			throw new SQLException(e.getMessage(), e);
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		}
	}
	/**
	 * Returns the string representation of data description.
	 * 
	 * @return the string representation of data description
	 */
	@Override
	public String toString() {
		return "[datasource=" + getName() + ", resource=" + getResource() + "]";
	}
	
	/**
	 * Replaces inside of property value system variables or properties loaded by Spring
	 * @param value property value to change
	 * @return value changed
	 */
	private String replaceProperties(String value){
		String changed = null;
		// if property starts with jem.data
		// I need to ask to DataPaths Container in which data path I can put the file
		if (value.startsWith("${"+ConfigKeys.JEM_DATA_PATH_NAME+"}")){
			// takes teh rest of file name
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

}