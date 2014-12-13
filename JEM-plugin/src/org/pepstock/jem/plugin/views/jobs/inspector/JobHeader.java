/*******************************************************************************
* Copyright (c) 2012-2014 pepstock.org.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     Andrea "Stock" Stocchero
******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.event.EnvironmentEventListener;
import org.pepstock.jem.plugin.util.Images;

/**
 * Header to display job name and JEM environment, when both are available.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class JobHeader extends Composite implements EnvironmentEventListener{
	
	private static final String NOT_AVAILABLE = "n/a";
	
	private Job job = null;
	
	private Label icon;
	
	private Label environment;
	
	private Label jobName;
	
	/**
	 * Creates the composite by parent
	 * @param parent parent composite 
	 */
    public JobHeader(Composite parent) {
	    super(parent, SWT.NONE);
	    setLayout(new GridLayout(5, false));
	    setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false, 0, 0));

	    // sets JOB icons
	    icon = new Label(this, SWT.NONE);

	    // set label with jobname
		Label jobLabel = new Label(this, SWT.NONE);
		// sets label of job name
		jobLabel.setText("Job: ");
		jobLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		jobName = new Label(this, SWT.NONE);
		jobName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jobName.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		// changes font of label
	    FontData fontData = jobName.getFont().getFontData()[0];
	    Font font = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.ITALIC | SWT.BOLD));
		jobName.setFont(font);
		
		// shows the environment where job is
		Label envLabel = new Label(this, SWT.NONE);
		envLabel.setText("Environment: ");
		envLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		// sets all graphic attributes
		environment = new Label(this, SWT.NONE);
		environment.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		environment.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		environment.setFont(font);
	    
		// checks if is connected
		if (Client.getInstance().isLogged()){
			// connect to default environment
			environmentConnected(null);
		} else {
			// or disconnect to default environment
			environmentDisconnected(null);
		}
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
		if (job != null){
			// changes label
			jobName.setText(job.getName());
			jobName.getParent().pack();
		} else {
			// changes label
			jobName.setText(NOT_AVAILABLE);
			jobName.getParent().pack();
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentConnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentConnected(EnvironmentEvent event) {
    	// changes icon
		icon.setImage(Images.ONLINE_FAVICON);
		// reset job name text
		jobName.setText(NOT_AVAILABLE);
		// sets environment
		environment.setText(Client.getInstance().getCurrent().getName());
		jobName.getParent().pack();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentDisconnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentDisconnected(EnvironmentEvent event) {
    	// changes icon, offline
    	icon.setImage(Images.OFFLINE_FAVICON);
    	// resets text
    	jobName.setText(NOT_AVAILABLE);
    	// resets environment
		environment.setText(NOT_AVAILABLE);
		jobName.getParent().pack();
    }
}