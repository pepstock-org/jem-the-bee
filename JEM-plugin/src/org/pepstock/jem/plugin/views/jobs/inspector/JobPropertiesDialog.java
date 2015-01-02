/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.inspector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.util.TimeDisplayUtils;
import org.pepstock.jem.util.DateFormatter;

/**
 * Dialog which shows the job execution details, both job itself and JCL ones.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JobPropertiesDialog extends Dialog {
	
	// fixed dimension of dialog
	private static final int DEFAULT_WIDTH = 600;
	
	private static final int DEFAULT_HEIGHT = 464;
	
	private static final int DEFAULT_MARGIN_VERTICAL = 15; 
	
	private static final int DEFAULT_MARGIN_HORIZONTAL = 10;

	private Job job;

	/**
	 * Creates the dialog using the necessary composite parent and job to show
	 * @param parentShell parent of dialog
	 * @param job job instance
	 */
	public JobPropertiesDialog(Shell parentShell, Job job) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.job = job;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    protected Control createDialogArea(Composite parent) {
    	// sets title
		getShell().setText("Job Informations " + job.getName());

		// main composite
		Composite main = new Composite(parent, SWT.LEFT);
		GridLayout mainLayout = new GridLayout(1, false);
		// sets fixed size
		mainLayout.marginTop = 0;
		mainLayout.marginBottom = DEFAULT_MARGIN_VERTICAL;
		mainLayout.marginLeft = DEFAULT_MARGIN_HORIZONTAL;
		mainLayout.marginRight = DEFAULT_MARGIN_HORIZONTAL;
		mainLayout.marginWidth = 0;
		mainLayout.marginHeight = 0;
		main.setLayout(mainLayout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// sets fixed size
		data.widthHint = DEFAULT_WIDTH;
		data.heightHint = DEFAULT_HEIGHT;
		main.setLayoutData(data);
		
		// set header
		new JobPropertiesHeader(main, job);
	
		// 2 tabbed panel: 1 for job information
		// 1 for JCL ones
		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// tab for JOB
		TabItem jobTab = new TabItem(folder, SWT.NONE);
		jobTab.setText("Job information");
		// tab for JCL
		TabItem jclTab = new TabItem(folder, SWT.NONE);
		jclTab.setText("Jcl information");

		// Creates composite to display job info
		Composite jobComposite = new Composite(folder, SWT.NONE);
		Composite jclComposite = new Composite(folder, SWT.NONE);
		GridLayout compLayout = new GridLayout(2, false);
		// job with FILL both, horizontally and vertically
		jobComposite.setLayout(compLayout);
		jobComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		// jcl with FILL both, horizontally and vertically
		jclComposite.setLayout(compLayout);
		jclComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		// creates tab
		createJobTab(jobComposite);
		createJclTab(jclComposite);
		// adds composite to TAB panel
		jobTab.setControl(jobComposite);
		jclTab.setControl(jclComposite);
		return main;
	}
    
    /**
     * Creates a text (always disabled) object with null value
     * @param parent parent of text field
     * @param labelText label for text
     * @return text field instance
     */
    private Text createText(Composite parent, String labelText){
    	return createText(parent, labelText, null);
    }
    
    /**
     * Creates a text (always disabled) object with initial value
     * @param parent parent of text field
     * @param labelText label for text
     * @param value initial value for text
     * @return text field instance
     */
    private Text createText(Composite parent, String labelText, String value){
    	// creates label
		Label label = new Label(parent, SWT.NONE);
		// set label text
		label.setText(labelText);
		// uses a text box to show value
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// but not editable
		text.setEditable(false);
		if (value != null){
			// sets value only is not null
			text.setText(value);
		}
		return text;
    }

    /**
     * Creates tab item with all JOB information
     * @param composite parent composite
     */
	private void createJobTab(Composite composite) {
		// creates all labels and textboxes
		createText(composite, "Name:", job.getName());
		createText(composite, "ID:", job.getId());
		createText(composite, "User:", (job.isUserSurrogated()) ? job.getJcl().getUser() : job.getUser());
		createText(composite, "Process ID:", job.getProcessId());
		createText(composite, "Submitted:", DateFormatter.getDate(job.getSubmittedTime(), TimeDisplayUtils.TIMESTAMP_FORMAT));
		createText(composite, "Started:",
				job.getStartedTime() != null ? DateFormatter.getDate(job.getStartedTime(), TimeDisplayUtils.TIMESTAMP_FORMAT) : "");
		createText(composite, "Ended:",
				job.getEndedTime() != null ? DateFormatter.getDate(job.getEndedTime(), TimeDisplayUtils.TIMESTAMP_FORMAT) : "");
		// sets current steps, checking if there is a valid value
		Text currentStep = createText(composite, "Current Step:");
		if ((job.getCurrentStep() == null) || (job.getEndedTime() != null)){
	    	currentStep.setText("");
	    } else {
	    	currentStep.setText(job.getCurrentStep().getName());
	    }
		// shows the result of job if ended
		Text result= createText(composite, "Result:");
		// if result is null means the job is not ended
		if (job.getResult() == null){
			result.setText("");
		} else {
			// shows only if result not null
			// before the return code
			result.setText(String.valueOf(job.getResult().getReturnCode()));
			Label label = new Label(composite, SWT.NONE);
			// and then the exception...
			label.setText("Exception:");
			//... using a text area
			Text exception = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			exception.setLayoutData(new GridData(GridData.FILL_BOTH));
			exception.setEditable(false);
			// it shows ONLY the first line
			// of exception stack trace
    		String fullMessage = job.getResult().getExceptionMessage();
    		if (fullMessage != null){
    			// checks if there is line terminator
    			int indexFirstRow = fullMessage.indexOf('\n');
    			String message = null;
    			// gets the first line
    			if (indexFirstRow == -1){
    				message = fullMessage;
    			} else {
    				message = fullMessage.substring(0, indexFirstRow);	
    			}
    			// sets the first line
    			exception.setText(message);
    		}
		}
	}

	/**
     * Creates tab item with all JCL information
     * @param composite parent composite
	 */
	private void createJclTab(Composite composite) {
		// creates all text for JCL attributes
		createText(composite, "Environment:", job.getJcl().getEnvironment());
		createText(composite, "Domain:", job.getJcl().getDomain());
		createText(composite, "Affinities:", job.getJcl().getAffinity());
		createText(composite, "Memory:", String.valueOf(job.getJcl().getMemory()));
		createText(composite, "Priority:", String.valueOf(job.getJcl().getPriority()));
		// uses a check box for hold
		Label holdLabel = new Label(composite, SWT.NONE);
		holdLabel.setText("Hold");
		Button hold = new Button(composite, SWT.CHECK);
		hold.setSelection(job.getJcl().isHold());
		hold.setEnabled(false);
		// sets node
		createText(composite, "Node:", job.getMemberLabel());
	}
}
