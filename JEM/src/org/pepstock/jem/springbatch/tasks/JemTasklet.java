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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceLoaderReference;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.util.SetFields;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.thoughtworks.xstream.XStream;

/**
 * Is a JEM implementation of a tasklet.<br>
 * If needs to use special features of JEM (like GDG or looking), must be
 * extended, implementing abstract method.<br>
 * Loads a JNDI file resources using DataDescription so the implementation can access
 * in abstract way to resources defined in JCL.<br>
 * Loads a JNDI datasource resources using DataSource so the implementation can access
 * in abstract way to resources defined in JCL.
 * 
 * @see org.springframework.batch.core.step.tasklet.Tasklet
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class JemTasklet implements Tasklet {

	private List<DataDescription> dataDescriptionList = new ArrayList<DataDescription>();
	
	private List<DataSource> dataSourceList = new ArrayList<DataSource>();
	
	private List<Lock> locks = new ArrayList<Lock>(); 

	/**
	 * Empty constructor
	 */
	public JemTasklet() {
	}

	/**
	 * Returns the list of data description defined for this tasklet.
	 * 
	 * @return the list of data description
	 */
	public final List<DataDescription> getDataDescriptionList() {
		return dataDescriptionList;
	}

	/**
	 * Sets the list of data description
	 * 
	 * @param dataDescriptionList the list of data description
	 */
	public final void setDataDescriptionList(List<DataDescription> dataDescriptionList) {
		this.dataDescriptionList = dataDescriptionList;
	}

	/**
	 * Returns the list of data sources defined for this tasklet.
	 * 
	 * @return the dataSourceList
	 */
	public final List<DataSource> getDataSourceList() {
		return dataSourceList;
	}

	/**
	 * Sets the list of data sources
	 * 
	 * @param dataSourceList the dataSourceList to set
	 */
	public final void setDataSourceList(List<DataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
	}

	/**
	 * @return the locks
	 */
	public final List<Lock> getLocks() {
		return locks;
	}

	/**
	 * @param locks the locks to set
	 */
	public final void setLocks(List<Lock> locks) {
		this.locks = locks;
	}

	/**
	 * Is called by SpringBatch framework to execute business logic.<br>
	 * Prepares datasets (and the files and resources) which could be used from
	 * implementation of this class.<br>
	 * Loads JNDI context so all resources could be used by their name, defined
	 * in JCL.
	 * 
	 * @param stepContribution step contribution, passed by SpringBatch core
	 * @param chunkContext chunk context, passed by SpringBatch core
	 * @return always the status returned by abstract method
	 *         <code>executeByJem</code>
	 * @throws SpringBatchException if a error occurs
	 */
	@Override
	public final RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws SpringBatchException {
		LogAppl.getInstance();
		
		// this boolean is necessary to understand if I have an exception 
		// before calling the main class
		boolean isExecutionStarted = false;
		boolean isAbended = false;
		
		SpringBatchSecurityManager batchSM = (SpringBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.pepstock.jem.node.tasks.jndi.JemContextFactory");
		
		RepeatStatus status = null;

		// extract stepContext because the step name is necessary
		StepContext stepContext = chunkContext.getStepContext();

		List<DataDescriptionImpl> dataDescriptionImplList = ImplementationsContainer.getInstance().getDataDescriptionsByItem(stepContext.getStepName());

		// new initial context for JNDI
		InitialContext ic = null;
		
		try {
			ic = new InitialContext();
			// scans all datasource passed
			for (DataSource source : dataSourceList){
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
				if (!batchSM.checkResource(res)){
					throw new SpringBatchException(SpringBatchMessage.JEMS017E, res.toString());
				}

				// all properties create all StringRefAddrs necessary
				Map<String, ResourceProperty> properties = res.getProperties();

				// scans all properteis set by JCL
				for (Property property : source.getProperties()){
					// if a key is defined FINAL, throw an exception
					for (ResourceProperty resProperty : properties.values()){
						if (resProperty.getName().equalsIgnoreCase(property.getName()) && !resProperty.isOverride()){
							throw new SpringBatchException(SpringBatchMessage.JEMS018E, property.getName(), res);
						}
					}
					res.setProperty(property.getName(), property.getValue());
				}
				// creates a JNDI reference
				Reference ref = null;
				try {
					ref = resourcer.lookupReference(JobId.VALUE, res.getType());
					if (ref == null){
						throw new SpringBatchException(SpringBatchMessage.JEMS019E, res.getName(), res.getType());
					}
					if (ref instanceof ResourceLoaderReference){
						ResourceLoaderReference loader = (ResourceLoaderReference) ref;
						loader.loadResource(res, dataDescriptionImplList, source.getName());
					}
				} catch (Exception e) {
					throw new SpringBatchException(SpringBatchMessage.JEMS019E, e, res.getName(), res.getType());
				}
				
				// loads all properties into RefAddr
				for (ResourceProperty property : properties.values()){
					ref.add(new StringRefAddr(property.getName(), property.getValue()));
				}
				
				// loads custom properties in a string format
				if (res.getCustomProperties() != null && !res.getCustomProperties().isEmpty()){
					ref.add(new StringRefAddr(CommonKeys.RESOURCE_CUSTOM_PROPERTIES, res.getCustomPropertiesString()));	
				}
				
				// binds the object with format {type]/[name]
				LogAppl.getInstance().emit(SpringBatchMessage.JEMS024I, res);
				ic.rebind(source.getName(), ref);
			}

			// check if I have resources which must be locked
			if (!dataDescriptionImplList.isEmpty()) {

				// binds all data description impl to JNDI context
				for (DataDescriptionImpl ddImpl : dataDescriptionImplList) {

					// create reference for JNDI access
					Reference reference = new DataStreamReference();

					// load GDG information, solving the real name of relative
					// position
					GDGManager.load(ddImpl);
					// serialize data description object in XML format
					XStream xstream = new XStream();
					String xml = xstream.toXML(ddImpl);
					// add string xml reference
					reference.add(new StringRefAddr(StringRefAddrKeys.DATASTREAMS_KEY, xml));

					LogAppl.getInstance().emit(SpringBatchMessage.JEMS023I, ddImpl);
					// bind resource using data description name
					ic.rebind(ddImpl.getName(), reference);
				}
			}
			batchSM.setInternalAction(false);
			// execute business logic
			// executes the java class defined in JCL
			// setting the boolean to TRUE
			isExecutionStarted = true;
			SetFields.applyByAnnotation(this);
			status = this.run(stepContribution, chunkContext);
		} catch (NamingException e) {
			isAbended = true;
			throw new SpringBatchException(SpringBatchMessage.JEMS043E, e);
		} catch (IOException e) {
			isAbended = true;
			throw new SpringBatchException(SpringBatchMessage.JEMS044E, e, e.getMessage());
		} catch (Exception e) {
			isAbended = true;
			throw new SpringBatchException(SpringBatchMessage.JEMS045E, e, this.getClass().getName(), e.getMessage());
		} finally {
			batchSM.setInternalAction(true);
			if (!dataDescriptionImplList.isEmpty()) {
				StringBuilder exceptions = new StringBuilder();
				// scans data descriptions
				for (DataDescriptionImpl ddImpl : dataDescriptionImplList) {
					try {
						// commit the GDG index in the root
						// if an exception, write on standard output of job
						// only if execution started
						if (isExecutionStarted){
							GDGManager.store(ddImpl);
						}
					} catch (IOException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);

						LogAppl.getInstance().emit(SpringBatchMessage.JEMS025E, e.getMessage());
						if (exceptions.length() == 0){
							exceptions.append(e.getMessage());
						} else { 
							exceptions.append(e.getMessage()).append("\n");
						}
					}
					// unbinds all data sources
					try {
						ic.unbind(ddImpl.getName());
					} catch (NamingException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						LogAppl.getInstance().emit(SpringBatchMessage.JEMS047E, e.getMessage());
					}
				}
				for (DataSource source : dataSourceList){
					// unbinds all resources
					try {
						ic.unbind(source.getName());
					} catch (NamingException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						LogAppl.getInstance().emit(SpringBatchMessage.JEMS047E, e.getMessage());
					}
				}
				if (exceptions.length() > 0 && !isAbended){
					LogAppl.getInstance().emit(SpringBatchMessage.JEMS025E, StringUtils.center("ATTENTION", 40, "-"));
					LogAppl.getInstance().emit(SpringBatchMessage.JEMS025E, exceptions.toString());
				}
			}
		}
		return status;
	}
	

	/**
	 * Is abstract method to implement with business logic, where it's possible
	 * to access to resources by JNDI.
	 * 
	 * @param stepContribution step contribution
	 * @param chuckContext chunk context
	 * @return status of execution
	 * @throws TaskletException if errors occur
	 */
	public abstract RepeatStatus run(StepContribution stepContribution, ChunkContext chuckContext) throws TaskletException;
}
