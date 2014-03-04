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
package org.pepstock.jem.gwt.client.panels.jobs.commons;

import java.util.Map;
import java.util.Map.Entry;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.XmlResultViewer;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.JobHeader;
import org.pepstock.jem.gwt.client.panels.jobs.commons.submitter.Actions;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component which shows all job information. Can be called to see a job.
 * If job is in Input or routing, you don't have any output information to show.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Submitter extends PopupPanel {
	
	//create a FormPanel 
	final FormPanel form = new FormPanel();
	
	//create a file upload widget
	final FileUpload fileUpload = new FileUpload();
	
	private boolean errorRetrievingJCLType = false;
	
	final ListBox list = new ListBox();

	/**
	 * 
	 */
	public static final String SERVICE_NAME = "submitter";
	/**
	 * 
	 */
	public static final String FILE_UPLOAD_FIELD = "jcl";
	
	/**
	 * 
	 */
	public static final String TYPE_FIELD = "type";
	
	/**
	 * Construct the UI without output information.<br>
	 * Happens when the job is INPUT or ROUTING.
	 * 
	 * @param job
	 */
	public Submitter() {
		
		super(true, true);
		setGlassEnabled(true);

		// creates sub component
		DockLayoutPanel mainContainer = new DockLayoutPanel(Unit.PX);

		// adds header with job name
		mainContainer.addNorth(new JobHeader("Submit a Job", this), Sizes.INSPECTOR_HEADER_HEIGHT_PX);

		VerticalPanel panel = new VerticalPanel();
		//pass action to the form to point to service handling file 
		//receiving operation.
		form.setAction(GWT.getModuleBaseURL()+SERVICE_NAME);
		// set form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		fileUpload.setName(FILE_UPLOAD_FIELD);

		Services.QUEUES_MANAGER.getJclTypes(new GetJclTypesAsyncCallback());
		list.setName(TYPE_FIELD);
		
	    FlexTable layout = new FlexTable();
	    layout.setCellPadding(10);
	    layout.setWidth(Sizes.HUNDRED_PERCENT);

	    layout.setHTML(0, 0, "Job JCL file:");
	    layout.setWidget(0, 1, fileUpload);
	    
	    layout.setHTML(1, 0, "JCL type:");
	    layout.setWidget(1, 1, list);
		
		//add a label
		panel.add(layout);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String value = list.getValue(list.getSelectedIndex());
				CurrentUser.getInstance().setStringPreference(PreferencesKeys.JOB_SUBMIT_TYPE, value);
				XmlResultViewer.showResult("JOB submitted", event.getResults());
				close();
			}
		});
		
		panel.setSpacing(10);
		
		// adds ActionsButtonPanel to south
		Actions actions = new Actions(this);
		panel.add(actions);

		// Add form to the root panel.      
		form.add(panel);

		mainContainer.add(form);
		setWidget(mainContainer);
	}
	
	private class GetJclTypesAsyncCallback extends ServiceAsyncCallback<Map<String, String>> {
		@Override
		public void onJemSuccess(Map<String, String> result) {
			int count=0;
			list.setSelectedIndex(0);
			String pref = CurrentUser.getInstance().getStringPreference(PreferencesKeys.JOB_SUBMIT_TYPE);
			for (Entry<String, String> entry : result.entrySet()){
				list.addItem(entry.getValue(), entry.getKey());
				if ((pref != null) && (pref.equalsIgnoreCase(entry.getKey()))){
					list.setSelectedIndex(count);
				} else {
					count++;
				}
			}
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			close();
			errorRetrievingJCLType = true;
			new Toast(MessageLevel.ERROR, caught.getMessage(), "JCL type Error!").show();
		}

		@Override
        public void onJemExecuted() {
			// do nothing
        }
	}
	
	/**
	 * 
	 */
	public void submit(){
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
	public void center(){
		if (errorRetrievingJCLType) {
			return;
		}
		super.center();
	}
	/**
	 * 
	 */
	public void cancel(){
		close();
	}

	/**
	 * 
	 */
	public void close(){
		hide();
	}
}