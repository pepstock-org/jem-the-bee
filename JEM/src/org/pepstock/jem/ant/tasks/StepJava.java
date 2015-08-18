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
package org.pepstock.jem.ant.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.Result;
import org.pepstock.jem.annotations.SetFields;
import org.pepstock.jem.ant.AntKeys;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceLoaderReference;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.node.tasks.jndi.DataPathsReference;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;

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
	
	private String id = DataDescriptionStep.DEFAULT_ID;
	
	private String name = null;
	
	private int order = 0;

	private final List<DataDescription> list = new ArrayList<DataDescription>();
	
	private final List<DataSource> sources = new ArrayList<DataSource>();
	
	private final List<Lock> locks = new ArrayList<Lock>(); 
	
	private String classname = null;
	
	private String resultProperty = null;
	
	private Class<?> clazz = null;
	
	private ReturnCode sharedRC = SharedReturnCode.getInstance(); 

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

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.taskdefs.Java#setResultProperty(java.lang.String)
	 */
	@Override
	public void setResultProperty(String resultProperty) {
		super.setResultProperty(resultProperty);
		this.resultProperty = resultProperty;
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
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.taskdefs.Java#setClassname(java.lang.String)
	 */
	@Override
	public void setClassname(String s) throws BuildException {
		super.setClassname(s);
		this.classname = s;
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
		// sets the current step
		StepsContainer.getInstance().setCurrent(this);
		// reset the return code
		sharedRC.setRC(Result.SUCCESS);
		// checks if the result property is set
		// if not, it sets automatically
		// one based on target, task and id
		if (resultProperty == null){
			setResultProperty(ReturnCodesContainer.getInstance().createKey(this));
		}
		
		int returnCode = Result.SUCCESS;
		// this boolean is necessary to understand if I have an exception 
		// before calling the main class
		boolean isExecutionStarted = false;
		
		AntBatchSecurityManager batchSM = (AntBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);

		// object serializer and deserializer into XML
		XStream xstream = new XStream();

		List<DataDescriptionImpl> ddList = null;
		InitialContext ic = null;
		try {
			// gets all data description requested by this task
			ddList = ImplementationsContainer.getInstance().getDataDescriptionsByItem(this);
			// new intial context for JNDI
			ic = ContextUtils.getContext();

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
				if (source.getResource() == null){
					throw new BuildException(AntMessage.JEMA027E.toMessage().getFormattedMessage());
				} else if (source.getName() == null) {
					// if name is missing, it uses the same string 
					// used to define the resource
					source.setName(source.getResource());
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
					if (property.isCustom()){
						if (res.getCustomProperties() == null){
							res.setCustomProperties(new HashMap<String, String>());
						}
						if (!res.getCustomProperties().containsKey(property.getName())){
							res.getCustomProperties().put(property.getName(), property.getText().toString());
						} else {
							throw new BuildException(AntMessage.JEMA028E.toMessage().getFormattedMessage(property.getName(), res));
						}
					} else {
						// if a key is defined FINAL, throw an exception
						for (ResourceProperty resProperty : properties.values()){
							if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()){
								throw new BuildException(AntMessage.JEMA028E.toMessage().getFormattedMessage(property.getName(), res));
							}
						}
						ResourcePropertiesUtil.addProperty(res, property.getName(), property.getText().toString());
					}
				}

				// creates a JNDI reference
				Reference ref = getReference(resourcer, res, source, ddList);

				// loads all properties into RefAddr
				for (ResourceProperty property : properties.values()){
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
				
				// binds the object with [name]
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
			
			// changes the main class to apply the annotations of JEM
			setCustomMainClass();
			
			batchSM.setInternalAction(false);
			// executes the java main class defined in JCL
			// setting the boolean to TRUE
			isExecutionStarted = true;
			// tried to set fields where
			// annotations are used
			super.execute();
		} catch (BuildException e) {
			returnCode = Result.ERROR;
			throw e;
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
			batchSM.setInternalAction(true);
			// only if I don't have any error
			// gets the return code
			if (returnCode == Result.SUCCESS){
				// checks if launcher is used
				// if not, gets return code from class
				if (clazz != null){
					returnCode = ReturnCodesContainer.getInstance().getReturnCode(clazz);
				} else {
					// gets return code by proxy due to different classloader 
					returnCode = sharedRC.getRC();
				}
			} 
			// stores the return code
			ReturnCodesContainer.getInstance().setReturnCode(getProject(), this, resultProperty, returnCode);
			
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
					if (source.getName() != null){
						// unbinds all resources
						try {
							ic.unbind(source.getName());
						} catch (NamingException e) {
							// ignore
							LogAppl.getInstance().ignore(e.getMessage(), e);
							log(AntMessage.JEMA037E.toMessage().getFormattedMessage(e.getMessage()));
						}
					}
				}
				// checks if has exception using the stringbuffer
				// used to collect exception string. 
				// Stringbuffer is not empty, throws an exception
				if (exceptions.length() > 0){
					log(StringUtils.center("ATTENTION", 40, "-"));
					log(exceptions.toString());
				}
				batchSM.setInternalAction(false);
			}
		}
	}
	
	/**
	 * Creates a JNDI reference looking up via RMI to JEM node, using the data source specified on JCL and resource.
	 * <br>
	 * List of data description are necessary for the resources which could be used as streams on datasets.
	 * 
	 * @param resourcer singleton to get CommonResource object by RMI
	 * @param res resource of JEM
	 * @param source data source defined in the JCL
	 * @param dataDescriptionImplList list of data description defined on the step
	 * @return JNDI reference
	 */
	private Reference getReference(CommonResourcer resourcer, Resource res, DataSource source, List<DataDescriptionImpl> ddList) {
		// creates a JNDI reference
		Reference ref = null;
		try {
			// gets JNDI reference
			ref = resourcer.lookupReference(JobId.VALUE, res.getType());
			// if null, exception
			if (ref == null){
				throw new BuildException(AntMessage.JEMA030E.toMessage().getFormattedMessage(res.getName(), res.getType()));
			}
			// if reference needs data set descriptions, because is possible to use it
			// as data source on data description
			// calls load method
			if (ref instanceof ResourceLoaderReference){
				ResourceLoaderReference loader = (ResourceLoaderReference) ref;
				loader.loadResource(res, ddList, source.getName());
			}
		} catch (Exception e) {
			throw new BuildException(AntMessage.JEMA030E.toMessage().getFormattedMessage(res.getName(), res.getType()), e);
		} 
		return ref;
	}
	
	/**
	 * Change the main class of the stepjava becuase this is necessary if it wants to apply
	 * the JEm annotation to the main java class developed with business logic.
	 * 
	 * @throws NamingException if is not ableto get the right info from JNDI
	 */
	private void setCustomMainClass() throws NamingException{
		// sets here the annotations
		try {
			// if has got a classpath, change the main class with a JEM one
			if (super.getCommandLine().haveClasspath()){
				Class<?> clazz = JavaMainClassLauncher.class;
				// gets where the class is located
				// becuase it must be added to classpath
				CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
				if ( codeSource != null) {
					// gets URL
					URL url = codeSource.getLocation();
					if (url != null){
						// add at the ends of parameters the classname
						super.createArg().setValue(getClassname());
						// changes class name
						super.setClassname(JavaMainClassLauncher.class.getName());
						// adds URL to classpath
						super.createClasspath().add(new Path(getProject(), FileUtils.toFile(url).getAbsolutePath()));
					}
				}
			} else {
				// if no classpath, can substitute here
				clazz = Class.forName(getClassname());
				SetFields.applyByAnnotation(clazz);
			}
		} catch (ClassNotFoundException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
		}
	}
	
	/**
	 * Replaces inside of property value system variables or properties loaded by ANT
	 * @param value property value to change
	 * @return value changed
	 */
	private String replaceProperties(String value){
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
			// uses ANT utilities to changed all properties
			changed = getProject().replaceProperties(value);
		}
		return changed;
	}

}
