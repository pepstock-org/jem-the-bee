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
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OSProcess;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.MemorySize;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component that shows all information of job in inspect mode
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class SystemActivity extends DefaultInspectorItem{
	
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	private Job job = null;
	
	private HorizontalPanel hp = new HorizontalPanel();
	
	private ScrollPanel processesContainer = new ScrollPanel();

	private FlexTable layoutMem = new FlexTable();
	private FlexTable layoutCpu = new FlexTable();
	
	private FlexTable layoutProc = new FlexTable();

	private static final int SPACING = 5;
	
	private static final int FLEXTABLE_HEIGHT = 115;

	private Button getButton = null;

	/**
	 * Builds the component, using the job instance as argument
	 * 
	 * @param job job instance in inspect mode
	 * 
	 */
	public SystemActivity(final Job job) {
		this.job = job;
	    
	    VerticalPanel main = new VerticalPanel();
	    main.setWidth(Sizes.HUNDRED_PERCENT);
	    main.setHeight(Sizes.HUNDRED_PERCENT);
	    main.setSpacing(SPACING);
	    
	    // MAIN PANEL
	    hp.setHeight(Sizes.toString(FLEXTABLE_HEIGHT));
	    hp.setWidth(Sizes.HUNDRED_PERCENT);
	    main.add(hp);
	    /*
	     * JOB
	     */
	    VerticalPanel cpuVp = new VerticalPanel();
	    cpuVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(cpuVp);
	    hp.setCellWidth(cpuVp, "50%");

	    Label cpuLabel = new Label("CPU information");
	    cpuLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    cpuLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    cpuLabel.addStyleName(Styles.INSTANCE.common().bold());
	    cpuVp.add(cpuLabel);
	    
	    layoutCpu.setCellPadding(10);
	    layoutCpu.setWidth(Sizes.HUNDRED_PERCENT);

	    layoutCpu.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Total CPU");
	    layoutCpu.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_2, "");
	    
	    layoutCpu.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_1, "Current CPU");
	    layoutCpu.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_2, "");

	    UITools.setFlexTableStyles(layoutCpu, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    cpuVp.add(layoutCpu);

	    /*
	     * JCL
	     */
	    VerticalPanel memVp = new VerticalPanel();
	    memVp.setWidth(Sizes.HUNDRED_PERCENT);
	    hp.add(memVp);
	    hp.setCellWidth(memVp, "50%");
	    
	    Label memLabel = new Label("Memory information");
	    memLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    memLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    memLabel.addStyleName(Styles.INSTANCE.common().bold());
	    memVp.add(memLabel);
	    
	    layoutMem.setCellPadding(10);
	    layoutMem.setWidth(Sizes.HUNDRED_PERCENT);
	    
	    // Add some standard form options
	    layoutMem.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Current memory utilization");
	    layoutMem.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_2, "");

	    UITools.setFlexTableStyles(layoutMem, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    memVp.add(layoutMem);
	    
	    
	    // P R O C E S S E S
	    processesContainer.setWidth(Sizes.HUNDRED_PERCENT);
	    VerticalPanel procVp = new VerticalPanel();
	    procVp.setWidth(Sizes.HUNDRED_PERCENT);

	    Label procLabel = new Label("PROCESSES information");
	    procLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    procLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    procLabel.addStyleName(Styles.INSTANCE.common().bold());
	    procVp.add(procLabel);
	    
	    layoutProc.setCellPadding(10);
	    layoutProc.setWidth(Sizes.HUNDRED_PERCENT);

	    procVp.add(layoutProc);
	    processesContainer.add(procVp);
	    
	    main.add(processesContainer);
	    
	    // actions for getdata button
		HorizontalPanel actions = new HorizontalPanel();
		actions.setHeight(Sizes.toString(Sizes.SEARCHER_WIDGET_HEIGHT));
		
		getButton = new Button("Refresh", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getButton.setFocus(true);
				// do!
				getSystemActivity();
				
			}
		});
		getButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		actions.add(getButton);
		
		main.add(actions);

	    // main
	    add(main);

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
	 * @param focus
	 */
	public void setFocus(boolean focus){
		getButton.setFocus(focus);
		if (focus){
			getSystemActivity();
		}
	}

	
	/**
	 * @param jobs collections of jobs to cancel
	 */
	public void getSystemActivity(){
		if (!getButton.isEnabled()) {
			return;
		}
		Loading.startProcessing();
		getButton.setEnabled(false);
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.getJobSystemActivity(getJob(), new GetJobSystemActivityAsyncCallback());
			}
	    });

	}
	
	private class GetJobSystemActivityAsyncCallback extends ServiceAsyncCallback<JobSystemActivity> {
		@Override
		public void onJemSuccess(JobSystemActivity result) {
			if (result.isActive()){
				
				String display = NumberFormat.getFormat("###,###,##0 ms").format(result.getCpu());
				layoutCpu.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_2, display);

				display = NumberFormat.getFormat("##0.00").format(result.getCpuPerc()*100) + " %";
				layoutCpu.setHTML(RowIndex.ROW_2, ColumnIndex.COLUMN_2, display);
				
				display = NumberFormat.getFormat("###,##0 MB").format((double)result.getMemory()/(double)MemorySize.MB);
				layoutMem.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_2, display);

				layoutProc.removeAllRows();
			    layoutProc.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_1, "Command");
			    layoutProc.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_2, "PID");
			    layoutProc.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_3, "Cpu");
			    layoutProc.setHTML(RowIndex.ROW_1, ColumnIndex.COLUMN_4, "Memory");
				
				createProcessesList(result.getProcess(), 0);
			    UITools.setFlexTableRowStyles(layoutProc, 
		    	    		Styles.INSTANCE.inspector().rowDark(), 
		    	    		Styles.INSTANCE.inspector().rowLight());
			    
			    UITools.setHeaderStyle(layoutProc, Styles.INSTANCE.common().bold());
		    
			    
			} else {
				new Toast(MessageLevel.WARNING, "Job '"+getJob().getName()+"' is no longer running!", "GetJobSystemActivty command error!").show();
			}
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "GetJobSystemActivty command error!").show();
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			getButton.setEnabled(true);
        }		
	}
	
	private void createProcessesList(OSProcess process, int level){
		int row = layoutProc.getRowCount();
	
		StringBuilder indent = new StringBuilder();
		for (int i=0; i< level; i++){
			indent.append("&nbsp;&nbsp;&nbsp;");
		}
		
	    layoutProc.setHTML(row, ColumnIndex.COLUMN_1, indent.toString()+process.getCommand());
	    layoutProc.setHTML(row, ColumnIndex.COLUMN_2, String.valueOf(process.getPid()));
	    layoutProc.setHTML(row, ColumnIndex.COLUMN_3, NumberFormat.getFormat("###,###,##0 ms").format(process.getCpu()));
	    layoutProc.setHTML(row, ColumnIndex.COLUMN_4, NumberFormat.getFormat("###,##0 MB").format((double)process.getMemory()/(double)MemorySize.MB));
	    
	    for (OSProcess p : process.getChildren()){
	    	createProcessesList(p, level+1);
	    }
	    
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
	    // calculate width and height
	    int width = availableWidth;
	    // spacing horizontal
	    width -= SPACING * 2;

	    int height = availableHeight - (SPACING * 4) - Sizes.SEARCHER_WIDGET_HEIGHT - FLEXTABLE_HEIGHT;
	    
	    processesContainer.setSize(Sizes.toString(width), Sizes.toString(height));

    }
}
