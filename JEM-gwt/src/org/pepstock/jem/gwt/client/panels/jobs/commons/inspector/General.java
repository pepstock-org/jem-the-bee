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
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

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

	    layoutJob.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Name");
	    layoutJob.setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_2, new HTML(job.getName()));
	    
	    layoutJob.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "ID");
	    layoutJob.setWidget(RowIndex.ROW_2, ColumnIndex.COLUMN_2, new HTML(job.getId()));
	    
	    layoutJob.setHTML(RowIndex.ROW_3, ColumnIndex.COLUMN_1, "User");
	    layoutJob.setWidget(RowIndex.ROW_3, ColumnIndex.COLUMN_2, new HTML(job.getUser()));
	    
    	layoutJob.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_1, "Process ID");
	    if (job.getProcessId() == null){
	    	layoutJob.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_2, "");
	    } else {
	    	layoutJob.setWidget(RowIndex.ROW_4, ColumnIndex.COLUMN_2, new HTML(job.getProcessId()));
	    }
	    
	    layoutJob.setHTML(RowIndex.ROW_5, ColumnIndex.COLUMN_1, "Submitted");
	    layoutJob.setWidget(RowIndex.ROW_5, ColumnIndex.COLUMN_2, new HTML(JemConstants.DATE_TIME_FULL.format(job.getSubmittedTime())));

    	layoutJob.setHTML(RowIndex.ROW_6, ColumnIndex.COLUMN_1, "Started");
	    if (job.getStartedTime() == null){
	    	layoutJob.setHTML(RowIndex.ROW_6, ColumnIndex.COLUMN_1, "");
	    } else {
	    	layoutJob.setWidget(RowIndex.ROW_6, ColumnIndex.COLUMN_2, new HTML(JemConstants.DATE_TIME_FULL.format(job.getStartedTime())));
	    }

    	layoutJob.setHTML(RowIndex.ROW_7, ColumnIndex.COLUMN_1, "Ended");
	    if (job.getEndedTime() == null){
	    	layoutJob.setHTML(RowIndex.ROW_7, ColumnIndex.COLUMN_2, "");
	    } else {
	    	layoutJob.setWidget(RowIndex.ROW_7, ColumnIndex.COLUMN_2, new HTML(JemConstants.DATE_TIME_FULL.format(job.getEndedTime())));
	    }

    	layoutJob.setHTML(RowIndex.ROW_8, ColumnIndex.COLUMN_1, "Current Step");
	    if ((job.getCurrentStep() == null) || (job.getEndedTime() != null)){
	    	layoutJob.setHTML(RowIndex.ROW_8, ColumnIndex.COLUMN_2, "");
	    } else {
	    	layoutJob.setWidget(RowIndex.ROW_8, ColumnIndex.COLUMN_2, new HTML(job.getCurrentStep().getName()));
	    }
	    
    	layoutJob.setHTML(RowIndex.ROW_9, ColumnIndex.COLUMN_1, "Result");
	    if (job.getResult() == null){
	    	layoutJob.setHTML(RowIndex.ROW_9, ColumnIndex.COLUMN_2, "");
	    } else {
	    	layoutJob.setWidget(RowIndex.ROW_9, ColumnIndex.COLUMN_2, new HTML(String.valueOf(job.getResult().getReturnCode())));
	    	if (job.getResult().getExceptionMessage() != null){
	    		layoutJob.setHTML(RowIndex.ROW_10, ColumnIndex.COLUMN_1, "Exception");
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
	    		layoutJob.setHTML(RowIndex.ROW_10, ColumnIndex.COLUMN_2, message);
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
	    layoutJcl.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Environment");
	    layoutJcl.setWidget(RowIndex.ROW_1, ColumnIndex.COLUMN_2, new HTML(job.getJcl().getEnvironment()));
	    layoutJcl.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "Domain");
	    layoutJcl.setWidget(RowIndex.ROW_2, ColumnIndex.COLUMN_2, new HTML(job.getJcl().getDomain()));
	    layoutJcl.setHTML(RowIndex.ROW_3, ColumnIndex.COLUMN_1, "Affinities");
	    layoutJcl.setWidget(RowIndex.ROW_3, ColumnIndex.COLUMN_2, new HTML(job.getJcl().getAffinity()));

	    layoutJcl.setHTML(RowIndex.ROW_4, ColumnIndex.COLUMN_1, "Memory");
	    layoutJcl.setWidget(RowIndex.ROW_4, ColumnIndex.COLUMN_2, new HTML(String.valueOf(job.getJcl().getMemory())));

	    layoutJcl.setHTML(RowIndex.ROW_5, ColumnIndex.COLUMN_1, "Priority");
	    layoutJcl.setWidget(RowIndex.ROW_5, ColumnIndex.COLUMN_2, new HTML(String.valueOf(job.getJcl().getPriority())));

	    layoutJcl.setHTML(RowIndex.ROW_6, ColumnIndex.COLUMN_1, "Hold");
	    layoutJcl.setWidget(RowIndex.ROW_6, ColumnIndex.COLUMN_2, new HTML(job.getJcl().isHold() ? JemConstants.YES : JemConstants.NO));
	    
    	layoutJcl.setHTML(RowIndex.ROW_7, ColumnIndex.COLUMN_1, "Node");
	    if (job.getMemberLabel() == null){
	    	layoutJcl.setHTML(RowIndex.ROW_7, ColumnIndex.COLUMN_2, "");
	    } else {
	    	layoutJcl.setWidget(RowIndex.ROW_7, ColumnIndex.COLUMN_2, new HTML(job.getMemberLabel()));
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
