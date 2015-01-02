/*******************************************************************************
* Copyright (C) 2012-2015 pepstock.org.
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
import org.pepstock.jem.plugin.util.Images;

/**
 * Header of general job information dialog, with job icon and job name.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JobPropertiesHeader extends Composite{

	/**
	 * Creates the header using the necessary composite parent and job to show its name
	 * @param parent parent of header
	 * @param job job instance
	 */
    public JobPropertiesHeader(Composite parent, Job job) {
	    super(parent, SWT.NONE);
	    // sets grid layout
	    setLayout(new GridLayout(3, false));
	    setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false, 0, 0));
	    // sets ICON for JOB
	    Label icon = new Label(this, SWT.NONE);
	    icon.setImage(Images.JOB);
	    // sets job name 
		Label jobLabel = new Label(this, SWT.NONE);
		// fixed label
		jobLabel.setText("Job: ");
		jobLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		// creates label for job
		Label jobName = new Label(this, SWT.NONE);
		jobName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jobName.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		// changes font
	    FontData fontData = jobName.getFont().getFontData()[0];
	    Font font = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.ITALIC | SWT.BOLD));
	    // sets font and job name
		jobName.setFont(font);
		jobName.setText(job.getName());
    }
}