/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Enrico Frigo - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.inspector;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.pepstock.jem.Job;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.commons.StringEditorInput;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.util.FilesUtil;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.views.JemViewPart;
import org.pepstock.jem.plugin.views.jobs.inspector.model.Category;
import org.pepstock.jem.plugin.views.jobs.inspector.model.CategoryFactory;
import org.pepstock.jem.plugin.views.jobs.inspector.model.ProducedOutput;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.JobQueue;

/**
 *  Is a viewPart, activated to show the details of a job. It has got the job header to provide short info about job anme and JEM environment.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class InspectorViewPart extends JemViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = InspectorViewPart.class.getName();
	
	private static final InspectorLabelProvider LABEL_PROVIDER = new InspectorLabelProvider();
	
	private static final InspectorContentProvider CONTENT_PROVIDER = new InspectorContentProvider();
	
	private final FileDragListener dragListener = new FileDragListener();
	
	private TreeViewer treeViewer;
	
	private JobHeader jobHeader;
	
	private OutputTree data = null;
	
	private Job job = null;
	
	private JobQueue queueName = null;

	/**
	 * @return the data
	 */
	public OutputTree getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(OutputTree data) {
		this.data = data;
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
	 * @return the queueName
	 */
	public JobQueue getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName the queueName to set
	 */
	public void setQueueName(JobQueue queueName) {
		this.queueName = queueName;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.JemViewPart#setFocus()
	 */
    @Override
    public void setFocus() {
    	super.setFocus();
    	updateName();
    	// Be aware about this assignment
    	// very important
    	treeViewer.setInput(data);
    	jobHeader.setJob(getJob());
    	dragListener.setJob(getJob());
    	dragListener.setQueueName(getQueueName());
    }
  
	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.JemViewPart#getSelectedObjectName()
	 */
    @Override
    public String getSelectedObjectName() {
	    return (job != null) ? job.getName() : null;
    }

    
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		parent.setLayout(layout);
		
		// job header
		jobHeader = new JobHeader(parent);
		
		//Instantiate TableViewer
		//Create the composite
		Composite compositeTree = new Composite(parent, SWT.NONE);
		compositeTree.setLayout(new GridLayout(1, true));
		compositeTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		// Creates tree
		treeViewer = new TreeViewer(compositeTree, SWT.NONE);
		treeViewer.setContentProvider(CONTENT_PROVIDER);
		treeViewer.setLabelProvider(LABEL_PROVIDER);
		treeViewer.setInput(data);
		// expands all to solve the pack view
		treeViewer.setAutoExpandLevel(10);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		
		// DND
		dragListener.setTreeViewer(treeViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { FileTransfer.getInstance() };
		treeViewer.addDragSupport(operations, transferTypes, dragListener);
		treeViewer.addDoubleClickListener(new TreeSelect());
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentConnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentConnected(EnvironmentEvent event) {
    	super.environmentConnected(event);
    	jobHeader.environmentConnected(event);
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#
	 * environmentDisconnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
	@Override
	public void environmentDisconnected(EnvironmentEvent event) {
		super.environmentDisconnected(event);
		treeViewer.setInput(null);
		setJob(null);
		setData(null);
		updateName();
		jobHeader.environmentDisconnected(event);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.ShellContainer#getShell()
	 */
    @Override
    public Shell getShell() {
	    return treeViewer.getControl().getShell();
    }

    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 1.4
     */
    private class GetOutputFileContent extends FileLoading{
		/**
		 * @param shell 
		 * @param output
		 */
        public GetOutputFileContent(Shell shell, ProducedOutput output) {
	        super(shell, output);
        }
		
		@Override
		public void execute() throws JemException {
			try {
				// loads file content
				String content = Client.getInstance().getOutputFileContent(job, queueName, getOutput().getOutItem());
				// open text plain editor
				getSite().getWorkbenchWindow().getActivePage().openEditor(new StringEditorInput(content, getOutput().getName()), 
						"org.eclipse.ui.DefaultTextEditor");
			} catch (RestException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(getSite().getShell(), "Unable to get Log!", "Error while getting log file: " + e.getMessage(), MessageLevel.ERROR);
            } catch (PartInitException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(getSite().getShell(), "Unable to get Log!", "Error while getting log file: " + e.getMessage(), MessageLevel.ERROR);
            }
		}
    }
    
    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class TreeSelect implements IDoubleClickListener{
    	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
		 */
        @Override
        public void doubleClick(DoubleClickEvent event) {
			TreeViewer viewer = (TreeViewer) event.getViewer();
			IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
			Object selectedNode = thisSelection.getFirstElement();
			
			/*------------------------
			 * ProducedOutput
			 *------------------------*/
			if (selectedNode instanceof ProducedOutput) {
				ProducedOutput out = (ProducedOutput) selectedNode;
				FileLoading loading = new GetOutputFileContent(getShell(), out);
				loading.run();
			} else if (selectedNode instanceof Category){
				/*------------------------
				 * Category
				 *------------------------*/
				Category category = (Category) selectedNode;
				if (CategoryFactory.JOB_INFORMATION_NAME.equals(category.getName())) {
					/*------------------------
					 * GENERAL INFO
					 *------------------------*/
					// is general job info then starts popup
					JobPropertiesDialog jp = new JobPropertiesDialog(getSite().getShell(), job);
					jp.open();
				} else if (CategoryFactory.JOB_JCL_NAME.equals(category.getName())) {
					/*------------------------
					 * JCL
					 *------------------------*/
					try {
						// loads JCL.
						// remember that JCL content is transient and not
						// serialized inside of JCL object
						if (job.getJcl().getContent() == null){
							// REST call to download JCL
							job.getJcl().setContent(Client.getInstance().getJcl(job, queueName));
						}
						// writes JCL on temp file
						File file = FilesUtil.writeJcl(job);
						// activates EDITOR multi purposes 
						IPath ipath = new Path(file.getAbsolutePath());
						IFileStore fileLocation = EFS.getLocalFileSystem().getStore(ipath);
						IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
						getSite().getWorkbenchWindow().getActivePage().openEditor(new FileStoreEditorInput(fileLocation),desc.getId());
					} catch (PartInitException e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
						Notifier.showMessage(getSite().getShell(), "Unable to load JCL Log!", "Error while loading JCL: " + e.getMessage(), MessageLevel.ERROR);
					} catch (Exception e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
						Notifier.showMessage(getSite().getShell(), "Unable to load JCL Log!", "Error while loading JCL: " + e.getMessage(), MessageLevel.ERROR);
					}
				}
			} else {
				viewer.setExpandedState(selectedNode, !viewer.getExpandedState(selectedNode));
			}
		}

    }
}
