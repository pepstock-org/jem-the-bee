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
package org.pepstock.jem.gwt.client.panels.jobs.input;

import java.util.Map;
import java.util.Map.Entry;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.XmlResultViewer;
import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.JobHeader;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * Component which shows all job information. Can be called to see a job.
 * If job is in Input or routing, you don't have any output information to show.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class LegacySubmitter extends AbstractInspector implements Submitter {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private JobHeader header = new JobHeader("Submit a Job", this);
	private FlexTable content = new FlexTable();
	private FormPanel form = new FormPanel();
	private FileUpload fileUpload = new FileUpload();
	private ListBox typesList = new ListBox();
	private HorizontalPanel actionButtonPanel = new HorizontalPanel();
	private Button submitButton = new Button("Submit");
	
	/**
	 * Construct the UI without output information.<br>
	 * Happens when the job is INPUT or ROUTING.
	 * 
	 * @param job
	 */
	public LegacySubmitter() {
		super(true);
		setGlassEnabled(true);

		// builds the form
		//pass action to the form to point to service handling file receiving operation
		form.setAction(GWT.getModuleBaseURL()+SERVICE_NAME);
		// set form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String preferredJclType = typesList.getValue(typesList.getSelectedIndex());
				CurrentUser.getInstance().setStringPreference(PreferencesKeys.JOB_SUBMIT_TYPE, preferredJclType);
				XmlResultViewer.showResult("JOB submitted", event.getResults());
				hide();
			}
		});
		
		// retrieve the data for building content panel
		fileUpload.setName(FILE_UPLOAD_FIELD);
		Services.QUEUES_MANAGER.getJclTypes(new GetJclTypesAsyncCallback());
		typesList.setName(TYPE_FIELD);
		
		// build the content panel
		content.setCellSpacing(10);
	    content.setWidth(Sizes.HUNDRED_PERCENT);

	    content.setHTML(0, 0, "Job JCL file:");
	    content.setWidget(0, 1, fileUpload);
	    
	    content.setHTML(1, 0, "JCL type:");
	    content.setWidget(1, 1, typesList);

	    // add the content to form
	    form.setWidget(content);
	    
		// builds action buttons
	    submitButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
	    submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
		});

	    // builds the action button panel
	    actionButtonPanel.setSpacing(10);
	    actionButtonPanel.add(submitButton);
	    
	    // propose the switch only if supported by client browser
	    if (Uploader.isAjaxUploadWithProgressEventsSupported()) {
	    	Button switchButton = new Button("Multi-file Submitter");
		    switchButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					EventBus.INSTANCE.fireEventFromSource(new org.pepstock.jem.gwt.client.events.SubmitterClosedEvent(true), LegacySubmitter.this);
				}
			});
		    actionButtonPanel.add(switchButton);
		    actionButtonPanel.setCellWidth(switchButton, Sizes.HUNDRED_PERCENT);
		    actionButtonPanel.setCellHorizontalAlignment(switchButton, HasHorizontalAlignment.ALIGN_RIGHT);
		    actionButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    }
	}

	/**
	 * 
	 */
	public void submit() {
		//get the filename to be uploaded
		String filename = fileUpload.getFilename();
		if (filename.isEmpty()) {
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
		return form;
	}

	@Override
	public Panel getActions() {
		return actionButtonPanel;
	}

	private class GetJclTypesAsyncCallback extends ServiceAsyncCallback<Map<String, String>> {
		@Override
		public void onJemSuccess(Map<String, String> result) {
			int count=0;
			typesList.setSelectedIndex(0);
			String pref = CurrentUser.getInstance().getStringPreference(PreferencesKeys.JOB_SUBMIT_TYPE);
			for (Entry<String, String> entry : result.entrySet()){
				typesList.addItem(entry.getValue(), entry.getKey());
				if ((pref != null) && (pref.equalsIgnoreCase(entry.getKey()))){
					typesList.setSelectedIndex(count);
				} else {
					count++;
				}
			}
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			hide();
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Cannot retrieve JCL types!").show();
		}

		@Override
        public void onJemExecuted() {
			// do nothing
        }
	}

}