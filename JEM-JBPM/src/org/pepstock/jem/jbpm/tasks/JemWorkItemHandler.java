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
package org.pepstock.jem.jbpm.tasks;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.lang.StringUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.Result;
import org.pepstock.jem.jbpm.JBpmKeys;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.Task;
import org.pepstock.jem.jbpm.annotations.ToBeExecuted;
import org.pepstock.jem.jbpm.tasks.workitems.CustomMethodWorkItem;
import org.pepstock.jem.jbpm.tasks.workitems.DelegatedWorkItem;
import org.pepstock.jem.jbpm.tasks.workitems.MainClassWorkItem;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceLoaderReference;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.DataPathsReference;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;

import com.thoughtworks.xstream.XStream;

/**
 * Is a work item of JBPM which is able to call different kinds of JAVA class, managing their execution.<br>
 * Is able to call:<br>
 * <ul>
 * <li> Instance of <code>JemWorkItem</code> interface
 * <li> Main java class
 * <li> Any method of any class but with specific signatures
 * </ul>
 * It creates all JEM structures (data descriptions, data sources, locks) all via JNDI for the executed classes.
 * 
 * @see CustomMethodWorkItem	
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JemWorkItemHandler implements WorkItemHandler {
	
	private static final String METHOD_DELIMITER = "#";

	private static final String CLASS_NAME_KEY = "jem.workItem.className";
	
	private static final String RESULT_KEY = "jem.workItem.result";

	/* (non-Javadoc)
	 * @see org.kie.api.runtime.process.WorkItemHandler#abortWorkItem(org.kie.api.runtime.process.WorkItem, org.kie.api.runtime.process.WorkItemManager)
	 */
    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do nothing
	}


	/* (non-Javadoc)
	 * @see org.kie.api.runtime.process.WorkItemHandler#executeWorkItem(org.kie.api.runtime.process.WorkItem, org.kie.api.runtime.process.WorkItemManager)
	 */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	// gets the java class for workitem
		String className = (String) workItem.getParameter(CLASS_NAME_KEY);
		// must be defined
		if (className == null){
			throw new JemRuntimeException(JBpmMessage.JEMM001E.toMessage().getFormattedMessage(CLASS_NAME_KEY));
		}
		// removes all blank 
		className = className.trim();
		// if it still has got blanks, exception
		if (className.contains(" ")){
			throw new JemRuntimeException(JBpmMessage.JEMM056E.toMessage().getFormattedMessage(className));
		}
		
		// checks if there a method to call
		String methodName = null;
		if (className.contains(METHOD_DELIMITER)){
			methodName = StringUtils.substringAfter(className, METHOD_DELIMITER);
			className = StringUtils.substringBefore(className, METHOD_DELIMITER);
		}
		
		// defines the wrapper
		JemWorkItem wrapper = null;
		
		// load class
		Class<?> clazz;
        try {
	        clazz = Class.forName(className);
	        // if not method has been set (and ONLY if there isn't) 
	        // scans method to see if there the annotation
	        // on the method to call
	        if (methodName == null){
	        	for (Method m : clazz.getDeclaredMethods()){
	        		// checks if there is the annotation
	        		// and not already set
	        		if (m.isAnnotationPresent(ToBeExecuted.class) && methodName == null){
	        			//set method name
	        			methodName = m.getName();
	        		}
	        	}
	        }
        } catch (ClassNotFoundException e) {
        	LogAppl.getInstance().emit(JBpmMessage.JEMM006E, e, className);
			throw new JemRuntimeException(JBpmMessage.JEMM006E.toMessage().getFormattedMessage(className), e);
        }
		// if it has got the method instantiated
		if (methodName != null){
			try {
				wrapper = new CustomMethodWorkItem(clazz, methodName);
			} catch (InstantiationException e) {
				LogAppl.getInstance().emit(JBpmMessage.JEMM006E, e, className);
				throw new JemRuntimeException(JBpmMessage.JEMM006E.toMessage().getFormattedMessage(className), e);
			} catch (IllegalAccessException e) {
				LogAppl.getInstance().emit(JBpmMessage.JEMM006E, e, className);
				throw new JemRuntimeException(JBpmMessage.JEMM006E.toMessage().getFormattedMessage(className), e);
			}
		} else if (MainClassWorkItem.hasMainMethod(clazz)){
			wrapper = new MainClassWorkItem(clazz);
		} else {
			try {
				// load by Class.forName of workItem
				Object instance = Class.forName(className).newInstance();
				// check if it's a JemWorkItem. if not,
				// exception occurs. 
				if (instance instanceof JemWorkItem) {
					wrapper = new DelegatedWorkItem(instance);
				} else {
					LogAppl.getInstance().emit(JBpmMessage.JEMM004E, className);
					throw new JemRuntimeException(JBpmMessage.JEMM004E.toMessage().getFormattedMessage(className));
				}
			} catch (InstantiationException e) {
				LogAppl.getInstance().emit(JBpmMessage.JEMM006E, e, className);
				throw new JemRuntimeException(JBpmMessage.JEMM006E.toMessage().getFormattedMessage(className), e);
			} catch (IllegalAccessException e) {
				LogAppl.getInstance().emit(JBpmMessage.JEMM006E, e, className);
				throw new JemRuntimeException(JBpmMessage.JEMM006E.toMessage().getFormattedMessage(className), e);
			} catch (ClassNotFoundException e) {
				LogAppl.getInstance().emit(JBpmMessage.JEMM006E, e, className);
				throw new JemRuntimeException(JBpmMessage.JEMM006E.toMessage().getFormattedMessage(className), e);
			}
		}

		// gets the current task
		Task currentTask = CompleteTasksList.getInstance().getTaskByWorkItemID(workItem.getId());
		if (currentTask == null){
			throw new JemRuntimeException(JBpmMessage.JEMM002E.toMessage().getFormattedMessage(workItem.getId()));
		}
		// initialize the listener passing parameters
		// properties
		try {
			int returnCode = execute(currentTask, wrapper, workItem.getParameters());
			LogAppl.getInstance().emit(JBpmMessage.JEMM057I, currentTask.getId(), returnCode);
			currentTask.setReturnCode(returnCode);
			Map<String, Object> output = new HashMap<String, Object>();
			output.put(RESULT_KEY, returnCode);
			manager.completeWorkItem(workItem.getId(), output);	
		} catch (Exception e) {
			currentTask.setReturnCode(Result.ERROR);
			throw new JemRuntimeException(e);
		}
	}
    
    /**
     * Executes the work item, creating all JEM features and therefore reachable by JNDI.
     * 
     * @param task current JBPM task
     * @param item sub workitem to execute
     * @param parms list of parameters, created by JBPM for this work item
     * @return return code of work item execution
     * @throws JemException if any error occurs
     */
    private int execute(Task task, JemWorkItem item, Map<String, Object> parms) throws JemException{
		// this boolean is necessary to understand if I have an exception 
		// before calling the main class
		boolean isExecutionStarted = false;
		
		JBpmBatchSecurityManager batchSM = (JBpmBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);

		// object serializer and deserializer into XML
		XStream xstream = new XStream();
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.pepstock.jem.node.tasks.jndi.JemContextFactory");

		List<DataDescriptionImpl> ddList = null;
		InitialContext ic = null;
		try {
			// gets all data description requested by this task
			ddList = ImplementationsContainer.getInstance().getDataDescriptionsByItem(task);
			// new intial context for JNDI
			ic = new InitialContext();

			// LOADS DataPaths Container
			Reference referencePaths = new DataPathsReference();
			// loads dataPaths on static name
			String xmlPaths = xstream.toXML(DataPathsContainer.getInstance());
			// adds the String into a data stream reference
			referencePaths.add(new StringRefAddr(StringRefAddrKeys.DATAPATHS_KEY, xmlPaths));
			// re-bind the object inside the JNDI context
			ic.rebind(JBpmKeys.JBPM_DATAPATHS_BIND_NAME, referencePaths);
			
			// scans all datasource passed
			for (DataSource source : task.getDataSources()){
				// checks if datasource is well defined
				if (source.getResource() == null){
					throw new MessageException(JBpmMessage.JEMM027E);
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
					throw new MessageException(JBpmMessage.JEMM028E, res.toString());
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
							throw new MessageException(JBpmMessage.JEMM028E, property.getName(), res);
						}
					} else {
						// if a key is defined FINAL, throw an exception
						for (ResourceProperty resProperty : properties.values()){
							if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()){
								throw new MessageException(JBpmMessage.JEMM028E, property.getName(), res);
							}
						}
						ResourcePropertiesUtil.addProperty(res, property.getName(), property.getText().toString());
					}
				}

				// creates a JNDI reference
				Reference ref = null;
				try {
					ref = resourcer.lookupReference(JobId.VALUE, res.getType());
					if (ref == null){
						throw new MessageException(JBpmMessage.JEMM030E, res.getName(), res.getType());
					}
					if (ref instanceof ResourceLoaderReference){
						ResourceLoaderReference loader = (ResourceLoaderReference) ref;
						loader.loadResource(res, ddList, source.getName());
					}
				} catch (Exception e) {
					throw new MessageException(JBpmMessage.JEMM030E, e, res.getName(), res.getType());
				} 
	
				// loads all properties into RefAddr
				for (ResourceProperty property : properties.values()){
					ref.add(new StringRefAddr(property.getName(), property.getValue()));
				}
				
				// loads custom properties in a string format
				if (res.getCustomProperties() != null && !res.getCustomProperties().isEmpty()){
					ref.add(new StringRefAddr(CommonKeys.RESOURCE_CUSTOM_PROPERTIES, res.getCustomPropertiesString()));	
				}
				
				// binds the object with format [type]/[name]
				LogAppl.getInstance().emit(JBpmMessage.JEMM035I, res);
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

					LogAppl.getInstance().emit(JBpmMessage.JEMM034I, ddImpl);
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
			batchSM.setInternalAction(false);
			// executes the java main class defined in JCL
			// setting the boolean to TRUE
			isExecutionStarted = true;
			return item.execute(parms);
		} catch (RuntimeException e) {
			throw e;			
		} catch (Exception e) {
			throw new JemException(e);
		} finally {
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
						LogAppl.getInstance().emit(JBpmMessage.JEMM036E, e.getMessage());
						if (exceptions.length() == 0){
							exceptions.append(JBpmMessage.JEMM036E.toMessage().getFormattedMessage(e.getMessage()));
						} else { 
							exceptions.append(JBpmMessage.JEMM036E.toMessage().getFormattedMessage(e.getMessage())).append("\n");
						}
					}
					// unbinds all data sources
					try {
						ic.unbind(ddImpl.getName());
					} catch (NamingException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						LogAppl.getInstance().emit(JBpmMessage.JEMM037E, e.getMessage());
					}
				}
				for (DataSource source : task.getDataSources()){
					// unbinds all resources
					try {
						ic.unbind(source.getName());
					} catch (NamingException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						LogAppl.getInstance().emit(JBpmMessage.JEMM037E, e.getMessage());
					}
				}
				// checks if has exception using the stringbuffer
				// used to collect exception string. 
				// Stringbuffer is not empty, throws an exception
				if (exceptions.length() > 0){
					LogAppl.getInstance().emit(JBpmMessage.JEMM055E, exceptions.toString());
				}
			}
		}

    }

}