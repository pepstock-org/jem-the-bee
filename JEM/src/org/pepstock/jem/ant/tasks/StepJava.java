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
package org.pepstock.jem.ant.tasks;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.Result;
import org.pepstock.jem.ant.AntKeys;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.ant.tasks.managers.ImplementationsContainer;
import org.pepstock.jem.ant.tasks.managers.ReturnCodesContainer;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.resources.FtpResource;
import org.pepstock.jem.node.resources.HttpResource;
import org.pepstock.jem.node.resources.JdbcResource;
import org.pepstock.jem.node.resources.JmsResource;
import org.pepstock.jem.node.resources.JppfResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.DataPathsReference;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.FtpReference;
import org.pepstock.jem.node.tasks.jndi.HttpReference;
import org.pepstock.jem.node.tasks.jndi.JdbcReference;
import org.pepstock.jem.node.tasks.jndi.JmsReference;
import org.pepstock.jem.node.tasks.jndi.JppfReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;
import org.pepstock.jem.util.Parser;

import com.thoughtworks.xstream.XStream;

/**
 * Is <code>Java</code> ANT task implementation, where is possible to declare
 * all files and resources job needs to be executed.<br>
 * All the files references are passed by JNDI to java class which is able to
 * have the file name just requesting by name (data description name attribute)
 * and without coding them inside the code.<br>
 * All the datasources references are passed by JNDI to java class which is able to
 * have the a datasource just requesting by name (data source name attribute)
 * and without coding them inside the code.<br>
 * <b>The idea is to have the same business logic and then the same code for
 * different customers and then using different resources</b>.<br>
 * JCL has this goal and ANT and this implementation as well.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class StepJava extends Java  implements DataDescriptionStep {
	
	/**
	 * Internal property used to save the result of step
	 */
    public static final String RESULT_KEY = "step-java.result";
	
	private String id = DataDescriptionStep.DEFAULT_ID;
	
	private String name = null;
	
	private int order = 0;

	private final List<DataDescription> list = new ArrayList<DataDescription>();
	
	private final List<DataSource> sources = new ArrayList<DataSource>();
	
	private final List<Lock> locks = new ArrayList<Lock>(); 

	/**
	 * Calls super constructor and set fail-on-error to <code>true</code>.
	 * 
	 * @see org.apache.tools.ant.taskdefs.Java#setFailonerror(boolean)
	 */
	public StepJava() {
		super();
		super.setFailonerror(true);
	}

	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}


	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return (name == null) ? getTaskName() : name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.DataDescriptionItem#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return getOwningTarget().getName();
	}


	/**
	 * Called by ANT engine to add data description object defined inside the
	 * task element.
	 * 
	 * @see DataDescription
	 * @param dd data description object
	 */
	public void addDataDescription(DataDescription dd) {
		for (DataSet dataset : dd.getDatasets()){
			if (dataset.isInline() && dataset.isReplaceProperties()){
				String changed = getProject().replaceProperties(dataset.getText().toString());
				dataset.setTextBuffer(new StringBuilder(changed));
			}
		}
		list.add(dd);
	}

	/**
	 * Returns the list of data descriptions
	 * 
	 * @return the list of data descriptions
	 */
	@Override
	public List<DataDescription> getDataDescriptions() {
		return list;
	}

	/**
	 * Called by ANT engine to add datasource object defined inside the
	 * task element.
	 * 
	 * @param ds datasource object
	 */
	public void addDataSource(DataSource ds){
		sources.add(ds);
	}


	/**
	 * Called by ANT engine to add lock object defined inside the
	 * task element.
	 * @param lock
	 */
	public void addLock(Lock lock){
		locks.add(lock);
	}

	/**
	 * Returns the list of locks
	 * 
	 * @return the list of locks
	 */
	@Override
	public List<Lock> getLocks() {
		return locks;
	}

	/**
	 * Prepares the files required by ANT file using the data description, locks
	 * them, and prepares the right file name for GDG. Afterwards calls the java
	 * main class defined in the task.
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		int returnCode = Result.SUCCESS;
		// this boolean is necessary to understand if I have an exception 
		// before calling the main class
		boolean isExecutionStarted = false;
		
		AntBatchSecurityManager batchSM = (AntBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);

		// object serializer and deserializer into XML
		XStream xstream = new XStream();
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.pepstock.jem.node.tasks.jndi.JemContextFactory");

		List<DataDescriptionImpl> ddList = null;
		InitialContext ic = null;
		try {
			// gets all data description requested by this task
			ddList = ImplementationsContainer.getInstance().getDataDescriptionsByItem(this);
			// new intial context for JNDI
			ic = new InitialContext();

			// LOADS DataPaths Container
			Reference referencePaths = new DataPathsReference();
			// loads dataPaths on static name
			String xmlPaths = xstream.toXML(DataPathsContainer.getInstance());
			// adds the String into a data stream reference
			referencePaths.add(new StringRefAddr(StringRefAddrKeys.DATAPATHS_KEY, xmlPaths));
			// re-bind the object inside the JNDI context
			ic.rebind(AntKeys.ANT_DATAPATHS_BIND_NAME, referencePaths);
			
			// scans all datasource passed
			for (DataSource source : sources){
				// checks if datasource is well defined
				if ((source.getName() == null) || (source.getResource() == null)){
					throw new BuildException(AntMessage.JEMA027E.toMessage().getFormattedMessage());
				}
				
				// gets the RMi object to get resources
				CommonResourcer resourcer = InitiatorManager.getCommonResourcer();
				// lookups by RMI for the database 
				Resource res = resourcer.lookup(JobId.VALUE, source.getResource());
				if (!batchSM.checkResource(res)){
					throw new BuildException(AntMessage.JEMA028E.toMessage().getFormattedMessage(res.toString()));
				}
				
				// all properties create all StringRefAddrs necessary  
				Map<String, ResourceProperty> properties = res.getProperties();
				// scans all properteis set by JCL
				for (Property property : source.getProperties()){
					// if a key is defined FINAL, throw an exception
					for (ResourceProperty resProperty : properties.values()){
						if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()){
							throw new BuildException(AntMessage.JEMA028E.toMessage().getFormattedMessage(property.getName(), res));
						}
					}
					res.setProperty(property.getName(), property.getText().toString());
				}

				// creates a JNDI reference
				Reference ref = null;
				// only JBDC, JMS and FTP types are accepted
				if (res.getType().equalsIgnoreCase(JdbcResource.TYPE)) {
					// creates a JDBC reference (uses DBCP Apache)
					ref = new JdbcReference();

				} else if (res.getType().equalsIgnoreCase(JmsResource.TYPE)) {
					// creates a JDBC reference (uses javax.jms)
					ref = new JmsReference();

				} else if (res.getType().equalsIgnoreCase(HttpResource.TYPE)) {
					// creates a HTTP reference (uses org.apache.http)
					ref = new HttpReference();

				} else if (res.getType().equalsIgnoreCase(JppfResource.TYPE)) {
					// creates a JDBC reference (uses javax.jms)
					ref = new JppfReference();

				} else if (res.getType().equalsIgnoreCase(FtpResource.TYPE)) {
					// creates a FTP reference (uses Commons net Apache)
					ref = new FtpReference();
					
					// checks if I have a dataset linked to a datasource
					for (DataDescriptionImpl ddImpl : ddList) {
						for (DataSetImpl ds: ddImpl.getDatasets()){
							// if has resource linked
							// checks if teh name is the same
							if (ds.getType() == DataSetType.RESOURCE && ds.getDataSource().equalsIgnoreCase(source.getName())){
								// sets file name (remote one)
								res.setProperty(FtpResource.REMOTE_FILE, ds.getName());
								// sets if wants to have a OutputStream or InputStream using
								// disposition of dataset
								if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR)){
									res.setProperty(FtpResource.ACTION_MODE, FtpResource.ACTION_WRITE);
								} else {
									res.setProperty(FtpResource.ACTION_MODE, FtpResource.ACTION_READ);
								}
							}
						}
					}
				} else {
					try {
						ref = resourcer.lookupCustomResource(JobId.VALUE, res.getType());
						if (ref == null){
							throw new BuildException(AntMessage.JEMA030E.toMessage().getFormattedMessage(res.getName(), res.getType()));
						}
					} catch (Exception e) {
						throw new BuildException(AntMessage.JEMA030E.toMessage().getFormattedMessage(res.getName(), res.getType()), e);
					} 
				}

				// loads all properties into RefAddr
				for (ResourceProperty property : properties.values()){
					ref.add(new StringRefAddr(property.getName(), property.getValue()));
				}
				
				// binds the object with format [type]/[name]
				log(AntMessage.JEMA035I.toMessage().getFormattedMessage(res));
				ic.rebind(source.getName(), ref);
			}

			
			// if list of data description is empty, go to execute java main
			// class
			if (!ddList.isEmpty()) {

				// after locking, checks for GDG
				// is sure here the root (is a properties file) of GDG is locked
				// (doesn't matter if in READ or WRITE)
				// so can read a consistent data from root and gets the right
				// generation
				// starting from relative position
				for (DataDescriptionImpl ddImpl : ddList) {
					// creates a reference, accessible by name. Is data stream
					// reference because
					// contains a stream of data which represents a object
					Reference reference = new DataStreamReference();
					// loads GDG generation!! it meeans the real file name of
					// generation
					GDGManager.load(ddImpl);

					log(AntMessage.JEMA034I.toMessage().getFormattedMessage(ddImpl));
					// serialize data descriptor object into xml string
					// in this way is easier pass to object across different
					// classloader, by JNDI.
					// This xml, by reference, will be used by DataStreamFactory
					// when
					// java main class requests a resource by a JNDI call
					String xml = xstream.toXML(ddImpl);
					// adds the String into a data stream reference
					reference.add(new StringRefAddr(StringRefAddrKeys.DATASTREAMS_KEY, xml));
					// re-bind the object inside the JNDI context
					ic.rebind(ddImpl.getName(), reference);
				}

			}
			// sets fork to false
			// in this way java main class runs inside the same process
			// this is mandatory if wants to JNDI without any network
			// connection, like RMI
			super.setFork(false);
			batchSM.setInternalAction(false);
			// executes the java main class defined in JCL
			// setting the boolean to TRUE
			isExecutionStarted = true;
			super.execute();
		} catch (BuildException e1) {
			returnCode = Result.ERROR;
			throw e1;
		} catch (RemoteException e) {
			returnCode = Result.ERROR;
			throw new BuildException(e);
		} catch (IOException e) {
			returnCode = Result.ERROR;
			throw new BuildException(e);
		} catch (NamingException e) {
			returnCode = Result.ERROR;
			throw new BuildException(e);
		} finally {
			String rcObject = System.getProperty(RESULT_KEY);
			if (rcObject != null){
				returnCode = Parser.parseInt(rcObject, Result.SUCCESS);
			}
			ReturnCodesContainer.getInstance().setReturnCode(getProject(), this, returnCode);
			batchSM.setInternalAction(true);
			// checks datasets list
			if (ddList != null && !ddList.isEmpty()) {
				StringBuilder exceptions = new StringBuilder();
				// scans data descriptions
				for (DataDescriptionImpl ddImpl : ddList) {
					try {
						// consolidates the GDG situation
						// changing the root (is a properties file)
						// only if execution started
						if (isExecutionStarted){
							GDGManager.store(ddImpl);
						}
					} catch (IOException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						log(AntMessage.JEMA036E.toMessage().getFormattedMessage(e.getMessage()));
						if (exceptions.length() == 0){
							exceptions.append(AntMessage.JEMA036E.toMessage().getFormattedMessage(e.getMessage()));
						} else { 
							exceptions.append(AntMessage.JEMA036E.toMessage().getFormattedMessage(e.getMessage())).append("\n");
						}
					}
					// unbinds all data sources
					try {
						ic.unbind(ddImpl.getName());
					} catch (NamingException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						log(AntMessage.JEMA037E.toMessage().getFormattedMessage(e.getMessage()));
					}
				}
				for (DataSource source : sources){
					// unbinds all resources
					try {
						ic.unbind(source.getName());
					} catch (NamingException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						log(AntMessage.JEMA037E.toMessage().getFormattedMessage(e.getMessage()));
					}
				}
				// checks if has exception using the stringbuffer
				// used to collect exception string. 
				// Stringbuffer is not empty, throws an exception
				if (exceptions.length() > 0){
					log(StringUtils.center("ATTENTION", 40, "-"));
					log(exceptions.toString());
				}
			}
		}
	}

}
