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
package org.pepstock.jem.gwt.server;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.gwt.client.panels.jobs.commons.Submitter;
import org.pepstock.jem.gwt.client.services.SubmitManagerService;
import org.pepstock.jem.gwt.server.services.JobsManager;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Is GWT server service which can provide methods to submit a job.<br>
 * This doesn't implement the usual method because MultiPart is not well supported by RPC of GWT.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SubmitManagerServiceImpl extends FileUploadManager implements SubmitManagerService {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.FileUploadManager#loaded(java.util.List)
	 */
    @Override
    public String loaded(List<FileItem> items) throws JemException {
    	try {
	        JobsManager jobsManager = new JobsManager();
	        
	        String fileName = null;
	        PreJob preJob = null;
	        // scans all files uploaded
	        for (FileItem item : items){
	        	// works only with field of JEM
	        	// other files are ignored
	        	if (item.getFieldName().equalsIgnoreCase(Submitter.FILE_UPLOAD_FIELD)){
	        		// 
	        		fileName = item.getName();
	        		
	        		// reads file uploaded
	        		StringWriter writer = new StringWriter();
	        		IOUtils.copy(item.getInputStream(), writer);
	        		// creates a PreJob to submit
	        		// sets JCL content
	        		preJob = new PreJob();
	        		preJob.setJclContent(writer.toString());
	        		
	        		// creates a empty job
	        		Job job = new Job();
	        		// loads all line arguments (the -D properties).
	        		// could be useful to factories, listeners and during
	        		// job execution to
	        		// job itself
	        		job.setInputArguments(ManagementFactory.getRuntimeMXBean().getInputArguments());

	        		// loads prejob with job
	        		preJob.setJob(job);

	        	} else if (item.getFieldName().equalsIgnoreCase(Submitter.TYPE_FIELD)){
	        		// reads JCl type
	        		String type = item.getString();
	        		// sets JCL type which was an argument
	        		preJob.setJclType(type);
	        	}
	        }
	        // if prejob is not instantiated, means not all 
	        // attibutes (jcl file and type) are set
	        if (preJob != null){
	        	String id;
	        	// submits JOB and get job it to return
	        	id = jobsManager.submit(preJob);
	        	return  UserInterfaceMessage.JEMG034I.toMessage().getFormattedMessage(fileName, id);
	        } else {
	        	throw new JemException(UserInterfaceMessage.JEMG035E.toMessage().getFormattedMessage());
	        }
    	} catch (ServiceMessageException e) {
    		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG048E, e);
    		throw new JemException(UserInterfaceMessage.JEMG048E.toMessage().getFormattedMessage(e.getMessage()));
    	} catch (IOException e) {
    		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG048E, e);
    		throw new JemException(UserInterfaceMessage.JEMG048E.toMessage().getFormattedMessage(e.getMessage()));
    	}
    }

}