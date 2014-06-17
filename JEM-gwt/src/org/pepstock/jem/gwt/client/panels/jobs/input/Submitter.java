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
package org.pepstock.jem.gwt.client.panels.jobs.input;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.JobHeader;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Component which shows all job information. Can be called to see a job.
 * If job is in Input or routing, you don't have any output information to show.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Submitter extends AbstractInspector {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	/**
	 * 
	 */
	public static final String SERVICE_NAME = "submitter";
	
	/**
	 * 
	 */
	public static final String FILE_UPLOAD_FIELD = "jcl";

	private JobHeader header = new JobHeader("Submit a Job", this);
	private FlexTable content = new FlexTable();
	private FormPanel form = new FormPanel();
	private FileUpload fileUpload = new FileUpload();
	private HorizontalPanel actionButtonPanel = new HorizontalPanel();
	private Button submitButton = new Button("Submit");
	private Button switchButton = new Button("Multi-file Submitter");
	
	/**
	 * Construct the UI without output information.<br>
	 * Happens when the job is INPUT or ROUTING.
	 * 
	 * @param job
	 */
	public Submitter() {
		super(true);
		setGlassEnabled(true);

		// builds the form
		//pass action to the form to point to service handling file receiving operation
		form.setAction(GWT.getModuleBaseURL()+SERVICE_NAME);
		// set form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		// build the content panel
		content.setCellSpacing(10);
	    content.setWidth(Sizes.HUNDRED_PERCENT);

	    content.setHTML(0, 0, "Job JCL file:");
		fileUpload.setName(FILE_UPLOAD_FIELD);
	    content.setWidget(0, 1, fileUpload);

		// builds action buttons
	    submitButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
	    submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
		});
	    switchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//Submitter.this.hide();
				EventBus.INSTANCE.fireEventFromSource(new org.pepstock.jem.gwt.client.events.SubmitterClosedEvent(true), Submitter.this);
			}
		});
	    
	    // builds the action button panel
	    actionButtonPanel.setSpacing(10);
	    actionButtonPanel.add(submitButton);
	    actionButtonPanel.add(switchButton);
	    actionButtonPanel.setCellWidth(switchButton, Sizes.HUNDRED_PERCENT);
	    actionButtonPanel.setCellHorizontalAlignment(switchButton, HasHorizontalAlignment.ALIGN_RIGHT);
	}

	/**
	 * 
	 */
	public void submit() {
		//get the filename to be uploaded
		String filename = fileUpload.getFilename();
		if (filename.length() == 0) {
			new Toast(MessageLevel.ERROR, "No file has been specified! Please select a file which represents a Job!", "File error!").show();
		} else {
			//submit the form
			form.submit();			          
		}				
	}
	
	@Override
	public FlexTable getHeader() {
		return header;
	}

	@Override
	public Panel getContent() {
		return content;
	}

	@Override
	public Panel getActions() {
		return actionButtonPanel;
	}

}