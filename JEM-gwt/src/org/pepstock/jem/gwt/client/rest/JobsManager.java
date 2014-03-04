/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.rest;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.gwt.server.rest.JobsManagerImpl;
import org.pepstock.jem.gwt.server.rest.entities.JclContent;
import org.pepstock.jem.gwt.server.rest.entities.JobId;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputFileContent;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputListArgument;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.gwt.server.rest.entities.Jobs;
import org.pepstock.jem.gwt.server.rest.entities.ReturnedObject;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.AbstractRestManager;
import org.pepstock.jem.util.RestClient;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobsManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient
	 *            REST client instance
	 */
	public JobsManager(RestClient restClient) {
		super(restClient);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException
	 */
	public Jobs getInputQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerImpl.JOBS_MANAGER_INPUT_PATH, jobNameFilter);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException
	 */
	public Jobs getRunningQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerImpl.JOBS_MANAGER_RUNNING_PATH, jobNameFilter);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException
	 */
	public Jobs getOutputQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerImpl.JOBS_MANAGER_OUTPUT_PATH, jobNameFilter);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException
	 */
	public Jobs getRoutingQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerImpl.JOBS_MANAGER_ROUTING_PATH, jobNameFilter);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException
	 */
	public void hold(Jobs jobs) throws JemException {
		doAction(JobsManagerImpl.JOBS_MANAGER_HOLD_PATH, jobs);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException
	 */
	public void release(Jobs jobs) throws JemException {
		doAction(JobsManagerImpl.JOBS_MANAGER_RELEASE_PATH, jobs);
	}

	/**
	 * 
	 * @param preJob
	 * @return
	 * @throws JemException
	 */
	public String submit(PreJob preJob) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<JobId>> generic = new GenericType<JAXBElement<JobId>>() {

		};
		try {
			JAXBElement<JobId> jaxbContact = resource.path(JobsManagerImpl.JOBS_MANAGER_PATH).path(JobsManagerImpl.JOBS_MANAGER_SUBMIT_PATH).accept(MediaType.APPLICATION_XML).post(generic, preJob);
			JobId object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object.getId();
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * @param jobs
	 * @return
	 * @throws JemException
	 */
	public JclContent getJcl(Jobs jobs) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<JclContent>> generic = new GenericType<JAXBElement<JclContent>>() {

		};
		try {
			JAXBElement<JclContent> jaxbContact = resource.path(JobsManagerImpl.JOBS_MANAGER_PATH).path(JobsManagerImpl.JOBS_MANAGER_JCL_CONTENT_PATH).accept(MediaType.APPLICATION_XML).post(generic, jobs);
			JclContent object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}

			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * @param jobs
	 * @return
	 * @throws JemException
	 */
	public JobOutputTreeContent getOutputTree(Jobs jobs) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<JobOutputTreeContent>> generic = new GenericType<JAXBElement<JobOutputTreeContent>>() {

		};
		try {
			JAXBElement<JobOutputTreeContent> jaxbContact = resource.path(JobsManagerImpl.JOBS_MANAGER_PATH).path(JobsManagerImpl.JOBS_MANAGER_OUTPUT_TREE_PATH).accept(MediaType.APPLICATION_XML).post(generic, jobs);
			JobOutputTreeContent object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}

			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * @param jobFileContent
	 * @return
	 * @throws JemException
	 */
	public OutputFileContent getOutputFileContent(JobOutputListArgument jobFileContent) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<JobOutputFileContent>> generic = new GenericType<JAXBElement<JobOutputFileContent>>() {

		};
		try {
			JAXBElement<JobOutputFileContent> jaxbContact = resource.path(JobsManagerImpl.JOBS_MANAGER_PATH).path(JobsManagerImpl.JOBS_MANAGER_OUTPUT_FILE_CONTENT_PATH).accept(MediaType.APPLICATION_XML).post(generic, jobFileContent);
			JobOutputFileContent object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object.getOutputFileContent();
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException
	 */
	private Jobs getJobs(String method, String filter) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<Jobs>> generic = new GenericType<JAXBElement<Jobs>>() {

		};
		try {
			JAXBElement<Jobs> jaxbContact = resource.path(JobsManagerImpl.JOBS_MANAGER_PATH).path(method).accept(MediaType.APPLICATION_XML).post(generic, filter);
			Jobs object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * 
	 * @param method
	 * @param jobs
	 * @throws JemException
	 */
	private void doAction(String method, Jobs jobs) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<ReturnedObject>> generic = new GenericType<JAXBElement<ReturnedObject>>() {

		};
		try {
			JAXBElement<ReturnedObject> jaxbContact = resource.path(JobsManagerImpl.JOBS_MANAGER_PATH).path(method).accept(MediaType.APPLICATION_XML).post(generic, jobs);
			ReturnedObject object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
		}
	}
}