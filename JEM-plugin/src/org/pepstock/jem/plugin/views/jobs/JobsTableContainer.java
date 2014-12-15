/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.commons.JemColumnSortListener;
import org.pepstock.jem.plugin.commons.JemContentProvider;
import org.pepstock.jem.plugin.commons.JemTableColumn;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.plugin.views.Searcher;
import org.pepstock.jem.plugin.views.jobs.inspector.InspectorViewPart;
import org.pepstock.jem.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.rest.entities.Jobs;

/**
 * Table container of jobs of JEM. It contains a table for each type of jobs queue of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public abstract class JobsTableContainer implements ShellContainer, Refresher {

	// internal composite, necessary to avoid null pointer during initialization
	private Composite composite;
	
	// table
	private TableViewer viewer;
	
	private Searcher searcher = null;
	
	private Label number;
	
	private TableColumnLayout layout = new TableColumnLayout();
	
	private String queueName = null;

	private Collection<Job> data = new ArrayList<Job>();
	
	/**
	 * Constructor of object with folder and job queue name 
	 * @param parent tabbed panel, container of this
	 * @param style style for composites
	 * @param queueName job queue name
	 */
	public JobsTableContainer(TabFolder parent, int style, String queueName) {
		this.queueName = queueName;
		composite = new Composite(parent, style); // style
		composite.setLayout(new GridLayout(1, false));
		
		// adds searcher
		searcher = new Searcher(this);
		
		//Instantiate TableViewer
		//Create the composite
		Composite compositeTb = new Composite(composite, SWT.NONE);
		compositeTb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		compositeTb.setLayout(layout);
		viewer = new TableViewer(compositeTb, style | SWT.BORDER | SWT.FULL_SELECTION);
		
		// adds drop listener
		int operations = DND.DROP_COPY |  DND.DROP_MOVE |DND.DROP_DEFAULT;
		Transfer[] transferTypes = new Transfer[]{FileTransfer.getInstance()};
		viewer.addDropSupport(operations, transferTypes, new SubmitDropListener(viewer));

		// total amount of items
		Composite compositeTot = new Composite(this.composite, SWT.NONE);
		compositeTot.setLayout(new GridLayout(2, false));
		compositeTot.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		// adds label with total items
		Label numberLabel = new Label(compositeTot, SWT.NONE);
		numberLabel.setText("&Total Jobs: ");
		numberLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		number = new Label(compositeTot, SWT.NONE);
		number.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		number.setText(String.valueOf(0));
		number.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), viewer.getClass().getName());
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}
	
	/**
	 * Returns the collection of jobs 
	 * @return the collection of jobs
	 */
	public Collection<Job> getData(){
		return data;
	}
	
	/**
	 * Returns the table viewer 
	 * @return the table viewer
	 */
	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Returns the shell, because is a ShellConatiner.
	 * @return the shell
	 */
	public Shell getShell() {
		return getViewer().getControl().getShell();
	}
	
	/**
	 * Sets all components enabled or not
	 * @param enabled if <code>true</code>, enables the components, otherwise disabled them.
	 */
	public void setEnabled(boolean enabled){
		// sets components enabled or not 
		searcher.setEnabled(enabled);
		viewer.getTable().setEnabled(enabled);
		if (!enabled){
			data.clear();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					// reset the enabled attribute of the viewer
					// and label with amount of jobs
					getViewer().setInput(data);
					number.setText(String.valueOf(data.size()));
				}
			});
		}
	}
	
	/**
	 * Returns lists of table columns
	 * @return array with columns of table
	 */
	public abstract Collection<JemTableColumn> getColumns();

	/**
	 * Loads a collection of jobs, using filter
	 * 
	 * @param filter searching filter
	 * @return a collection of jobs
	 * @throws JemException if any error occurs
	 */
	public abstract Jobs loadData(String filter) throws JemException;
	
	/**
	 * Returns the name of queue name, readable
	 * @return the name of queue name
	 */
	public abstract String getName();
	
	/**
	 * Returns the label provider of jobs in table
	 * @return the label provider of jobs in table
	 */
	public abstract ILabelProvider getLabelProvider();

	/**
	 * Returns the job sorter for table
	 * @return the job sorter for table
	 */
	public abstract JobColumnSorter getColumnSorter();
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.Refresher#getComposite()
	 */
    @Override
    public Composite getComposite() {
	    return composite;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.Refresher#refresh(java.lang.String)
	 */
    @Override
    public void refresh(String filter) {
		// loading of jobs
		JobSearchLoading loading = new JobSearch(getShell(), filter);
		loading.run();
    }
	
	/**
	 * Creates and fill table viewer
	 */
	public void createViewer() {
		// sets the content and sort provider
		viewer.setContentProvider(new JemContentProvider<Job>());
		viewer.setSorter(getColumnSorter());
		// create columns
		createColumns();
		// sets labels provider
		viewer.setLabelProvider(getLabelProvider());
		// loads data
		viewer.setInput(data);
		// creates teh table to show jobs
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		// add DC listener
		// to inspect the selected job
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = viewer.getSelection();
				// gets job selected
				Job job = (Job)((IStructuredSelection) selection).getFirstElement();
				if (job!=null){
					// loads output tree to inspect
					TreeListLoading loading = new TreeList(getShell(), job);
					loading.run();
				}
			}
		});
	}

	/**
	 * Creates all columns of job table
	 */
	private void createColumns() {
		// counter used for sorting
		int count=0;
		for (JemTableColumn column : getColumns()) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(getViewer(), SWT.NONE);
			TableColumn tblColumn = tableViewerColumn.getColumn();
			//Specify width using weights
			layout.setColumnData(tblColumn, new ColumnWeightData(column.getWeight(), ColumnWeightData.MINIMUM_WIDTH, true));
			tblColumn.setText(column.getName());
			tblColumn.addSelectionListener(new JemColumnSortListener(count, viewer));
			count++;
		}
	}
	
	/**
	 * Loading components, showed when the search starts
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	private class JobSearch extends JobSearchLoading{
		
		/**
		 * Shell container necessary to show the progress bar and search filter
		 * @param shell Shell container necessary to show the progress
		 * @param filter search filter
		 */
        public JobSearch(Shell shell, String filter) {
	        super(shell, filter);
        }
        
		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        protected void execute() throws JemException {
			Jobs jobs = null;
			try {
				// loads data, requsting to the server by REST
				jobs = loadData(getFilter());
				// if data is not provided, creates a empty collections
				data = (jobs != null && jobs.getJobs() != null) ? jobs.getJobs() : new ArrayList<Job>();
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						// sets data to table
						getViewer().setInput(data);
						// and the amount fo jobs
						number.setText(String.valueOf(data.size()));
					}
				});
			} catch (JemException e) {
				// if any error occurs on REST calls
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(JobsTableContainer.this, "Unable to load data", e.getMessage(), MessageLevel.ERROR);
			}
		}
	}
	
	/**
	 * Loading component called when you go inspecting a job
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	private class TreeList extends TreeListLoading{
		
		/**
		 * Shell of graphic container and the job instance to show
		 * 
		 * @param shell Shell of graphic container
		 * @param job the job instance to show
		 */
        public TreeList(Shell shell, Job job) {
	        super(shell, job);
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        protected void execute() throws JemException {
			try {
				// loads the output tree
				JobOutputTreeContent currentData = Client.getInstance().getOutputTree(getJob(), getQueueName());
				// gets inspector view part to open
				InspectorViewPart inspector = (InspectorViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(InspectorViewPart.ID);
				// sets all necessary data
				inspector.setData(currentData);
				inspector.setJob(getJob());
				inspector.setQueueName(getQueueName());
				inspector.setFocus();
			} catch (JemException e) {
				// if any error occurs on REST calls
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to load job "+getJob().getName()+" !", 
						"Unable to load the job '"+getJob().getName()+"' information. Please have a look to following exception message: "+e.getMessage(), 
						MessageLevel.ERROR);
			} catch (PartInitException e) {
				// if any error occurs on teh editor of Eclipse
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to load job "+getJob().getName()+" !", 
						"Unable to load the job '"+getJob().getName()+"' information. Please have a look to following exception message: "+e.getMessage(), 
						MessageLevel.ERROR);
            }
		}
	}
}
