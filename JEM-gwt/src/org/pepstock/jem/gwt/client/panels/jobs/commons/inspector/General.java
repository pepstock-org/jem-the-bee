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
package org.pepstock.jem.gwt.client.panels.jobs.commons.inspector;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component that shows all information of job in inspect mode
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class General extends DefaultInspectorItem {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	private Job job = null;
	
	/**
	 * Builds the component, using the job instance as argument
	 * 
	 * @param job job instance in inspect mode
	 * 
	 */
	public General(final Job job) {
		setJob(job);
	    
	    // MAIN PANEL
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.setHeight(Sizes.HUNDRED_PERCENT);
	    hp.setSpacing(10);
	    
	    /*
	     * JOB
	     */
	    VerticalPanel jobVp = new VerticalPanel();
	    jobVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(jobVp);
	    hp.setCellWidth(jobVp, "50%");

	    Label jobLabel = new Label("Job information");
	    jobLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    jobLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    jobLabel.addStyleName(Styles.INSTANCE.common().bold());
	    jobVp.add(jobLabel);
	    
	    FlexTable layoutJob = new FlexTable();
	    layoutJob.setCellPadding(10);
	    layoutJob.setWidth(Sizes.HUNDRED_PERCENT);

	    layoutJob.setHTML(0, 0, "Name");
	    layoutJob.setWidget(0, 1, new HTML(job.getName()));
	    
	    layoutJob.setHTML(1, 0, "ID");
	    layoutJob.setWidget(1, 1, new HTML(job.getId()));
	    
	    layoutJob.setHTML(2, 0, "User");
	    layoutJob.setWidget(2, 1, new HTML(job.getUser()));
	    
    	layoutJob.setHTML(3, 0, "Process ID");
	    if (job.getProcessId() == null){
	    	layoutJob.setHTML(3, 1, "");
	    } else {
	    	layoutJob.setWidget(3, 1, new HTML(job.getProcessId()));
	    }
	    
	    layoutJob.setHTML(4, 0, "Submitted");
	    layoutJob.setWidget(4, 1, new HTML(JemConstants.DATE_TIME_FULL.format(job.getSubmittedTime())));

    	layoutJob.setHTML(5, 0, "Started");
	    if (job.getStartedTime() == null){
	    	layoutJob.setHTML(5, 0, "");
	    } else {
	    	layoutJob.setWidget(5, 1, new HTML(JemConstants.DATE_TIME_FULL.format(job.getStartedTime())));
	    }

    	layoutJob.setHTML(6, 0, "Ended");
	    if (job.getEndedTime() == null){
	    	layoutJob.setHTML(6, 1, "");
	    } else {
	    	layoutJob.setWidget(6, 1, new HTML(JemConstants.DATE_TIME_FULL.format(job.getEndedTime())));
	    }

    	layoutJob.setHTML(7, 0, "Current Step");
	    if ((job.getCurrentStep() == null) || (job.getEndedTime() != null)){
	    	layoutJob.setHTML(7, 1, "");
	    } else {
	    	layoutJob.setWidget(7, 1, new HTML(job.getCurrentStep().getName()));
	    }
	    
    	layoutJob.setHTML(8, 0, "Result");
	    if (job.getResult() == null){
	    	layoutJob.setHTML(8, 1, "");
	    } else {
	    	layoutJob.setWidget(8, 1, new HTML(String.valueOf(job.getResult().getReturnCode())));
	    	if (job.getResult().getExceptionMessage() != null){
	    		layoutJob.setHTML(9, 0, "Exception");
	    		String fullMessage = job.getResult().getExceptionMessage();
	    		
	    		// extract the main exception, otherwise the message is too long
	    		int indexFirstRow = fullMessage.indexOf('\n');
	    		int indexFirstException = fullMessage.indexOf("nested exception");
	    		String message = null;
	    		if (indexFirstException == -1){
	    			if (indexFirstRow == -1){

	    				message = fullMessage;
	    			} else {
	    				message = fullMessage.substring(0, indexFirstRow);	
	    			}
	    		} else {
	    			message = fullMessage.substring(0, indexFirstException);
	    		}
	    		layoutJob.setHTML(9, 1, message);
	    	}
	    }

	    UITools.setFlexTableStyles(layoutJob, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    jobVp.add(layoutJob);

	    /*
	     * JCL
	     */
	    VerticalPanel jclVp = new VerticalPanel();
	    jclVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(jclVp);
	    hp.setCellWidth(jclVp, "50%");
	    
	    Label jclLabel = new Label("Jcl information");
	    jclLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    jclLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    jclLabel.addStyleName(Styles.INSTANCE.common().bold());
	    jclVp.add(jclLabel);

	    FlexTable layoutJcl = new FlexTable();
	    layoutJcl.setCellPadding(10);
	    layoutJcl.setWidth(Sizes.HUNDRED_PERCENT);
	    
	    // Add some standard form options
	    layoutJcl.setHTML(0, 0, "Environment");
	    layoutJcl.setWidget(0, 1, new HTML(job.getJcl().getEnvironment()));
	    layoutJcl.setHTML(1, 0, "Domain");
	    layoutJcl.setWidget(1, 1, new HTML(job.getJcl().getDomain()));
	    layoutJcl.setHTML(2, 0, "Affinities");
	    layoutJcl.setWidget(2, 1, new HTML(job.getJcl().getAffinity()));

	    layoutJcl.setHTML(3, 0, "Memory");
	    layoutJcl.setWidget(3, 1, new HTML(String.valueOf(job.getJcl().getMemory())));

	    layoutJcl.setHTML(4, 0, "Priority");
	    layoutJcl.setWidget(4, 1, new HTML(String.valueOf(job.getJcl().getPriority())));

	    layoutJcl.setHTML(5, 0, "Hold");
	    layoutJcl.setWidget(5, 1, new HTML(job.getJcl().isHold() ? JemConstants.YES : JemConstants.NO));
	    
    	layoutJcl.setHTML(6, 0, "Node");
	    if (job.getMemberLabel() == null){
	    	layoutJcl.setHTML(6, 1, "");
	    } else {
	    	layoutJcl.setWidget(6, 1, new HTML(job.getMemberLabel()));
	    }

	    UITools.setFlexTableStyles(layoutJcl, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    jclVp.add(layoutJcl);

	    // main
	    add(hp);
	}

	/**
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
	}
}