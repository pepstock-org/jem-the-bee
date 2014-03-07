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
package org.pepstock.jem.gwt.client.panels.jobs.commons;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector;
import org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter;
import org.pepstock.jem.gwt.client.editor.SyntaxHighlighter;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.BrowseJcl;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.EditJcl;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.General;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.JobHeader;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.Output;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.RouteInfo;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.SystemActivity;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Component which shows all job information. Can be called to see a job.
 * If job is in Input or routing, you don't have any output information to show.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class JobInspector extends AbstractTabPanelInspector {
	
	private Job job = null;
	
	private OutputTree outputTree = null;
	
	private AbstractSyntaxHighlighter jcl = null;
	
	private SyntaxHighlighter output = null;
	
	// tab panel with General, JCL and output views
	private TabPanel main = new TabPanel();

	/**
	 * Construct the UI without output information.<br>
	 * Happens when the job is INPUT or ROUTING.
	 * 
	 * @param job
	 */
	public JobInspector(Job job) {
		this(job, null);
	}
	
	/**
	 * Construct the UI 
	 * 
	 * @param job job instance
	 * @param outputTree  output data. if <code>null</code>, job is INPUT or ROUTING
	 * 
	 */
	public JobInspector(Job job, OutputTree outputTree) {
		super();
		this.job = job;
		this.outputTree = outputTree;

		main.add(new General(job), "General");
		// checks id user is authorized to submit job
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_SUBMIT)){
			// if yes, adds a editor to change the JCL
			jcl = new EditJcl(this, job.getJcl().getType());
		} else {
			// otherwise only a viewer
			jcl = new BrowseJcl();
		}
		jcl.setContent(job.getJcl().getContent());
		main.add(jcl, "JCL");
		
		// tests if job is still running 
		if ((job.getProcessId() != null) &&
				(job.getEndedTime() == null)) {	
				main.add(new SystemActivity(job), "Activity");
		}
		
		// adds output only if we are in RUNNING or OUTPUT view
		if (outputTree != null){
			output = new Output(job, outputTree);
			main.add(output, "Output");
		}
		
		// adds ROUTE INFO if available
		if (job.getRoutingInfo().getRoutedTime() != null) {
			main.add(new RouteInfo(job), "Route Info");
		}
		
		// selection handler
		main.addSelectionHandler(new MainTabPanelSelectionHandler());
		
		// selects general		
		main.selectTab(0);
	}

	private class MainTabPanelSelectionHandler implements SelectionHandler<Integer> {
		
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			// if SystemActivity is selected, sets FOCUS
			if (event.getSelectedItem() == 2){
				Widget w = main.getWidget(2);
				if (w instanceof SystemActivity){
					SystemActivity sa = (SystemActivity)w;
					sa.setFocus(true);
				}
			} else if (event.getSelectedItem() == 1){
				// if JCL is set nd in EDIT mode, starts Editor
				Widget w = main.getWidget(1);
				if (w instanceof SyntaxHighlighter){
					SyntaxHighlighter jclSelected = (SyntaxHighlighter)w;
					jclSelected.startEditor();
				}
			}
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
	}

	/**
	 * @return the outputTree
	 */
	public OutputTree getOutputTree() {
		return outputTree;
	}

	/**
	 * @param outputTree the outputTree to set
	 */
	public void setOutputTree(OutputTree outputTree) {
		this.outputTree = outputTree;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
	    return new JobHeader(job.getName(), this);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getActions()
	 */
    @Override
    public CellPanel getActions() {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector#getTabPanel()
	 */
    @Override
    public TabPanel getTabPanel() {
	    return main;
    }
	
	@Override
	public void hide(){
		// overrides method to destroy editor
		super.hide();
		if (jcl != null){
			jcl.destroyEditor();
		}
		if (output != null){
			output.destroyEditor();
		}
	}
	
	@Override
	public void hide(boolean autoClose){
		// overrides method to destroy editor
		super.hide(autoClose);
		if (jcl != null){
			jcl.destroyEditor();
		}
		if (output != null){
			output.destroyEditor();
		}
	}
	
}