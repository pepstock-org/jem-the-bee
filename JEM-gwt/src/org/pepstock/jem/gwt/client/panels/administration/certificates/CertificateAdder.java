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
package org.pepstock.jem.gwt.client.panels.administration.certificates;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.XmlResultViewer;
import org.pepstock.jem.gwt.client.panels.administration.certificates.adder.Actions;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component which implements the HTML form to upload a certificate
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class CertificateAdder extends PopupPanel {
	
	//create a FormPanel 
	final FormPanel form = new FormPanel();
	
	//create a file upload widget
	final FileUpload fileUpload = new FileUpload();
	
	final TextBox user = new TextBox();

	/**
	 * GWT RPC service name to call in form action
	 */
	public static final String SERVICE_NAME = "certificateAdder";
	
	/**
	 * Form field name for certificate file
	 */
	public static final String FILE_UPLOAD_FIELD = "certificate";
	
	/**
	 * Form field name for alias (is the user id)
	 */
	public static final String ALIAS_FIELD = "user";
	
	/**
	 * Construct the UI with form to submit to upload the certificate.
	 */
	public CertificateAdder() {
		
		super(true, true);
		setGlassEnabled(true);

		// creates sub component
		DockLayoutPanel mainContainer = new DockLayoutPanel(Unit.PX);

		// adds header 
		// use the 
		mainContainer.addNorth(new CertificateHeader(this), Sizes.INSPECTOR_HEADER_HEIGHT_PX);

		VerticalPanel panel = new VerticalPanel();
		//pass action to the form to point to service handling file 
		//receiving operation.
		form.setAction(GWT.getModuleBaseURL()+SERVICE_NAME);
		// set form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		fileUpload.setName(FILE_UPLOAD_FIELD);
		user.setName(ALIAS_FIELD);
	
	    FlexTable layout = new FlexTable();
	    layout.setCellPadding(5);
	    layout.setWidth(Sizes.HUNDRED_PERCENT);

	    layout.setHTML(0, 0, "Type user ID:");
	    layout.setWidget(0, 1, user);
	    
	    layout.setHTML(1, 0, "Select certificate file for user:");
	    layout.setWidget(1, 1, fileUpload);
	    
		
		//add a label
		panel.add(layout);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// success, shows toast!
				XmlResultViewer.showResult("Certificate added", event.getResults());
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
	
	
	/**
	 * Submit the form, uploading the file
	 */
	public void submit(){
		//get the filename to be uploaded
		String filename = fileUpload.getFilename();
		if (filename.length() == 0) {
			new Toast(MessageLevel.ERROR, "No file has been specified! Please select a file which represents a job!", "File error!").show();
		} else {
			//submit the form
			form.submit();	
		}				
	}
	
	/**
	 * Close the popup panel
	 */
	public void close(){
		hide();
	}
}