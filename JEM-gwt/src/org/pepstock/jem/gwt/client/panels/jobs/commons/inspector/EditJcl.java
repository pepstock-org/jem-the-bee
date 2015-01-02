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

import org.pepstock.jem.Jcl;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.editor.Editor;
import org.pepstock.jem.gwt.client.editor.actions.Discard;
import org.pepstock.jem.gwt.client.editor.actions.Indent;
import org.pepstock.jem.gwt.client.editor.actions.SelectAll;
import org.pepstock.jem.gwt.client.editor.modifiers.XmlModifier;
import org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspector;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Component that edits the JCL, using ACE editor
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class EditJcl extends XmlModifier{

	private String type = null;
	
	private JobInspector inspector = null;

	/**
	 * Constructs the UI using the JCL content 
	 * @param inspector container of editor
	 * @param content JCL content in XML format
	 * @param type JCL type, used for submitting
	 * 
	 */
	public EditJcl(JobInspector inspector, String type) {
		super();
		this.type = type;
		this.inspector = inspector;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setEditorAttributes(org.pepstock.jem.gwt.client.editor.Editor)
	 */
    @Override
    public void setEditorAttributes(Editor editor) {
		super.setEditorAttributes(editor);
		// sets mode returned by JCL
		Jcl jcl = inspector.getJob().getJcl();
		editor.setMode(jcl.getMode());
		editor.setHighlightActiveLine(true);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setMenuItems(com.google.gwt.user.client.ui.MenuBar, boolean)
	 */
    @Override
    public void setMenuItems(MenuBar menu) {
    	Discard discard = new Discard(this);
    	Indent indent = new Indent(this);
    	SelectAll selectAll = new SelectAll(this);

	    menu.addItem(selectAll.getItem());
	    menu.addItem(indent.getItem());

	    // SUBMIT
	    Image imgSubmit = new Image(Images.INSTANCE.editSubmit());
	    imgSubmit.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
	    MenuItem submitMenuItem = new MenuItem(imgSubmit+" Submit", true, new Command() {
			@Override
			public void execute() {
				if (getEditor().getText().trim().length() > 0){
					submit();
				} else {
					new Toast(MessageLevel.WARNING, "JCL content is empty. Submit is not allowed!", "Submit JCL warning!").show();
				}
			}
		});
	    
	    submitMenuItem.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
	    
	    if (getContent() == null || getContent().equalsIgnoreCase(Jcl.CONTENT_NOT_AVAILABLE)){
	    	indent.getItem().setEnabled(false);
	    	selectAll.getItem().setEnabled(false);
	    	submitMenuItem.setEnabled(false);
	    }
	    menu.addSeparator();
	    menu.addItem(discard.getItem());
	    menu.addItem(submitMenuItem);
    }
    
	private void submit() {
		inspector.hide();
		Loading.startProcessing();

		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Services.QUEUES_MANAGER.submit(inspector.getJob().getName(), getEditor().getText(), type,
						new SubmitAsyncCallback());
			}
		});
	}

   private static class SubmitAsyncCallback extends ServiceAsyncCallback<String> {
		@Override
		public void onJemSuccess(String result) {
			new Toast(MessageLevel.INFO, result, "Job submitted!").show();
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Submit job error!").show();
		}

		@Override
       public void onJemExecuted() {
			Loading.stopProcessing();
       }
   }
}