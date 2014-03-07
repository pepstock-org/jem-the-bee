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
package org.pepstock.jem.jppf;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tools.ant.BuildException;
import org.jppf.utils.JPPFConfiguration;
import org.jppf.utils.TypedProperties;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.ant.AntKeys;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.DataDescription;
import org.pepstock.jem.ant.tasks.DataSet;
import org.pepstock.jem.ant.tasks.StepJava;
import org.pepstock.jem.ant.tasks.managers.DataDescriptionManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

import com.thoughtworks.xstream.XStream;

/**
 * ANT task implementation to use JPPF for parallel execution.<br>
 * Extends StepJava so it has got all functionalities of a normal java JEM task.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class StepJPPF extends StepJava {
	
	private String runnable = null;
	
	private String address = null;
	
	private int parallelTaskNumber = Keys.DEFAULT_PARALLEL_TASK_NUMBER;

	private String datasource = null;
	
	private String delimiter = null;
	
	private String delimiterString = null;
	
	private String chunkableDataDescription = null;
	
	private String mergedDataDescription = null;

	/**
	 * @return the parallelTaskNumber
	 */
	public int getParallelTaskNumber() {
		return parallelTaskNumber;
	}

	/**
	 * @param parallelTaskNumber the parallelTaskNumber to set
	 */
	public void setParallelTaskNumber(int parallelTaskNumber) {
		this.parallelTaskNumber = parallelTaskNumber;
	}

	/**
	 * @return the runnable
	 */
	public String getRunnable() {
		return runnable;
	}

	/**
	 * @param runnable the runnable to set
	 */
	public void setRunnable(String runnable) {
		this.runnable = runnable;
	}

	/**
	 * @return the datasource
	 */
	public String getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * @return the delimiterString
	 */
	public String getDelimiterString() {
		return delimiterString;
	}

	/**
	 * @param delimiterString the delimiterString to set
	 */
	public void setDelimiterString(String delimiterString) {
		this.delimiterString = delimiterString;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	
	/**
	 * @return the chunkableDataDescription
	 */
	public String getChunkableDataDescription() {
		return chunkableDataDescription;
	}

	/**
	 * @param chunkableDataDescription the chunkableDataDescription to set
	 */
	public void setChunkableDataDescription(String chunkableDataDescription) {
		this.chunkableDataDescription = chunkableDataDescription;
	}

	
	/**
	 * @return the mergedDataDescription
	 */
	public String getMergedDataDescription() {
		return mergedDataDescription;
	}

	/**
	 * @param mergedDataDescription the mergedDataDescription to set
	 */
	public void setMergedDataDescription(String mergedDataDescription) {
		this.mergedDataDescription = mergedDataDescription;
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava).<br>
	 * Serializes all information set by attributes on XML element. These information
	 * are necessary on main program to create the right JPPF job.
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		// Runnable is a mandatory attribute
		if (runnable == null){
			throw new BuildException(JPPFMessage.JEMJ001E.toMessage().getFormattedMessage());
		}
		
		LogAppl.getInstance();
		
		super.setClassname(StepJPPF.class.getName());

		// gest a uuid
		// this will be the DataDescription name
		// to use during lookup
		UUID uuid = UUID.randomUUID();

		// creates manually a new DD (without ANT knowing)
		DataDescription dd = new DataDescription();
		// sets UUID string as DDNAME to avoid overriding
		dd.setName(uuid.toString());
		// creates a dataset
		DataSet ds = new DataSet();

		// reads attributes and
		// writes them on a dataset
		XStream xstream = new XStream();
		TypedProperties props = new TypedProperties();
		
		/*----------------------+
		 | Load JPPF properties | 
		 +----------------------*/
		props.setProperty(Keys.JEM_JPPF_DISCOVERY_ENABLED, Boolean.FALSE.toString());
		
		if (address != null){
			try {
				JPPFUtil.loadTypedProperties(props, address);
			} catch (JPPFMessageException e) {
				throw new BuildException(e.getMessage(), e);
			}
		}

		/*----------------------+
		 | Load JEM properties  | 
		 +----------------------*/
		if (datasource != null){
			props.setProperty(Keys.JEM_DATASOURCE, datasource);
		}
		props.setProperty(Keys.JEM_RUNNABLE, runnable);
		props.setProperty(Keys.JEM_TASK_NUMBER, String.valueOf(Math.max(1, parallelTaskNumber)));
		
		String jobName = (getProject().getProperty(AntKeys.ANT_JOB_NAME) != null) ? getProject().getProperty(AntKeys.ANT_JOB_NAME) : getProject().getName();
		props.setProperty(Keys.JEM_JOB_NAME, jobName);
		props.setProperty(Keys.JEM_TASK_NAME, super.getTargetName());
		
		// both delimiter and delimiterString are not allowed
		if ((delimiter != null) && (delimiterString != null)){
			throw new BuildException(JPPFMessage.JEMJ027E.toMessage().getFormattedMessage());
		}
		
		// delimiter is not null but 
		// chunkable data description is nul throws an exception. Not Allowed.
		if ((delimiter != null || delimiterString != null) && chunkableDataDescription == null){
			throw new BuildException(JPPFMessage.JEMJ013E.toMessage().getFormattedMessage());
		}
		// if there is data description for chunk
		// sets all properties 
		if (chunkableDataDescription != null){
			containsDataDescription(chunkableDataDescription, false);
			props.setProperty(Keys.JEM_CHUNKABLE_DATA_DESCRIPTION, chunkableDataDescription);
			if (delimiter != null){
				props.setProperty(Keys.JEM_DELIMITER, delimiter);
			} else if (delimiterString != null){
				props.setProperty(Keys.JEM_DELIMITER_STRING, delimiterString);
			}
		}
		// if there is data description for merging
		// sets all properties 
		if (mergedDataDescription != null){
			containsDataDescription(mergedDataDescription, true);
			props.setProperty(Keys.JEM_MERGED_DATA_DESCRIPTION, mergedDataDescription);
		}

		// serializes properties in XML
		ds.addText(xstream.toXML(props));
		dd.addDataSet(ds);
		super.addDataDescription(dd);

		try {
			DataDescriptionManager.createDataDescriptionImpl(dd, this);
		} catch (IOException e) {
			throw new BuildException(AntMessage.JEMA003E.toMessage().getFormattedMessage(), e);
		}

		// sets UUID string as argument for main
		super.createArg().setValue(uuid.toString());

		super.execute();
	}
	
	/**
	 * Checks if there is a data description with the name of chunk or merging data description 
	 * @param name name of data description to find
	 * @param write if must be check for an output or input
	 * @throws BuildException is not all controls end correctly 
	 */
	private void containsDataDescription(String name, boolean write) throws BuildException{
		boolean found = false;
		// scans data description
		for (DataDescription ddescription : getDataDescriptions()){
			// is the same datadescritpion
			if (ddescription.getName().equalsIgnoreCase(name)){
				if (write){
					// mustn't be in shr
					if (ddescription.getDisposition().equalsIgnoreCase(Disposition.SHR)){
						throw new BuildException(JPPFMessage.JEMJ014E.toMessage().getFormattedMessage(name));
					}
				} else {
					// must be in shr
					if (!ddescription.getDisposition().equalsIgnoreCase(Disposition.SHR)){
						throw new BuildException(JPPFMessage.JEMJ015E.toMessage().getFormattedMessage(name));
					}
					// chunk works only with a single dataset 
					if (ddescription.isMultiDataset()){
						throw new BuildException(JPPFMessage.JEMJ016E.toMessage().getFormattedMessage(name));
					}
					// chunk works only with files
					DataSet ddataset = ddescription.getDatasets().iterator().next();
					if (ddataset.isDatasource()){
						throw new BuildException(JPPFMessage.JEMJ017E.toMessage().getFormattedMessage(name));
					} else if (ddataset.isInline()) {
						throw new BuildException(JPPFMessage.JEMJ018E.toMessage().getFormattedMessage(name));
					}
				}
				found = true;
				break;
			}
		}
		if (!found){
			// not found
			throw new BuildException(JPPFMessage.JEMJ019E.toMessage().getFormattedMessage(name));
		}
	}

	/**
	 * Main program. REads from datadescription all info necessary to create a JPPF client
	 * and submit job on grid.
	 * 
	 * @param args datadescription name of TypedProperties, necessary to initialize JPPF client
	 * @throws JPPFMessageException if any exception occurs
	 * @throws NamingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws JPPFMessageException, NamingException {
		LogAppl.getInstance();
		
		// new initial context to access by JNDI to DataDescriptions
		InitialContext ic = ContextUtils.getContext();

		// creates XML streamer
		XStream xstream = new XStream();

		// try to load configuration from data description
		// if data description is allocated
		TypedProperties propsFromDataDescritpion = JPPFUtil.loadTypedPropertiesFromDataDescription(ic);

		// read what was inserted as attributes in the XMLelement
		// gets the argument (data descritpion name)
		String uuid = args[0];
		Object ddin = (Object) ic.lookup(uuid);
		InputStream configInputStream = (InputStream) ddin;
		// loads properties
		TypedProperties propsFromTag = (TypedProperties)xstream.fromXML(configInputStream);

		// checks if we're using the resources
		if (propsFromTag.getProperty(Keys.JEM_DATASOURCE) != null){
			// if yes, gets 
			String datasource = propsFromTag.getProperty(Keys.JEM_DATASOURCE);
			Object configResource = (Object) ic.lookup(datasource);
			if (configResource instanceof TypedProperties){
				TypedProperties props = (TypedProperties)configResource;
				
				// properties on Resource definition go on override on
				// properties on ANT task and on 
				// properties read from data description
				JPPFConfiguration.getProperties().putAll(propsFromDataDescritpion);
				JPPFConfiguration.getProperties().putAll(propsFromTag);
				JPPFConfiguration.getProperties().putAll(props);
				
			} else {
				throw new JPPFMessageException(JPPFMessage.JEMJ010E, Keys.JEM_DATASOURCE, configResource.getClass().getName(), TypedProperties.class.getName());
			}
			
		} else {
			// properties on ANT task go on override on 
			// properties read from data description
			JPPFConfiguration.getProperties().putAll(propsFromDataDescritpion);
			JPPFConfiguration.getProperties().putAll(propsFromTag);
		}

		// serializes the initial context with all resources
		// because they are necessary on grid to runnable
		String icXml = xstream.toXML(ic);
		ExecuteManager.submit(icXml);
	}

}