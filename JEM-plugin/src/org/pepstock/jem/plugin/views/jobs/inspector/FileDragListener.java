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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.Job;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.FilesUtil;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.plugin.views.jobs.inspector.model.Category;
import org.pepstock.jem.plugin.views.jobs.inspector.model.CategoryFactory;
import org.pepstock.jem.plugin.views.jobs.inspector.model.ProducedOutput;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.JobQueue;

/**
 * File drag listener utility, enables to open files of output directory of a job., using DND.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class FileDragListener implements DragSourceListener, ShellContainer {
	
	private Job job = null;
	
	private JobQueue queueName = null;

	private TreeViewer treeViewer = null;

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

	/**
	 * @return the treeViewer
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * @param treeViewer the treeViewer to set
	 */
	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		// nop
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragSetData(final DragSourceEvent event) {
		if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			// Here you do the convertion to the type which is expected.
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			Object selectedNode = selection.getFirstElement();
			if (selectedNode instanceof Category) {
				// gets selected category
				Category category = (Category) selectedNode;
				/*-------------------------
				 * JCL
				 --------------------------*/
				if (CategoryFactory.JOB_JCL_NAME.equals(category.getName())) {
					FileLoading loading = new GetJcl(getShell(), event, category);
					loading.run();

				} else if (!CategoryFactory.JOB_INFORMATION_NAME.equals(category.getName()) &&
						!CategoryFactory.JOB_OUTPUT_NAME.equals(category.getName())) {
					/*-------------------------
					 * SUB-CATEGORY
					 --------------------------*/
					FileLoading loading = new GetProducedOutputs(getShell(), event, category);
					loading.run();
				}
			} else if (selectedNode instanceof ProducedOutput) {
				/*-------------------------
				 * PRODUCED-OUTPUT
				 --------------------------*/
				ProducedOutput output = (ProducedOutput) selectedNode;
				FileLoading loading = new GetProducedOutput(getShell(), event, output);
				loading.run();
			}
		}
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		Object selectedNode = selection.getFirstElement();
		// gets category
		if (selectedNode instanceof Category) {
			Category category = (Category) selectedNode;
			// if is Job general info or output name, is not enable to drag
			if (CategoryFactory.JOB_INFORMATION_NAME.equals(category.getName()) ||
					CategoryFactory.JOB_OUTPUT_NAME.equals(category.getName())) {
				event.doit = false;
			}
		} 
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.ShellContainer#getShell()
	 */
    @Override
    public Shell getShell() {
	    return treeViewer.getControl().getShell();
    }
    
    /**
     * It retrieves output file content and writes on temporary file 
     * @param out file reference to write
     * @return temporary file with output file content
     * @throws JemException 
     * @throws IOException 
     * @throws Exception if any error occurs
     */
    private File dragProducedOutput(ProducedOutput out) throws RestException, IOException{
    	OutputListItem item = out.getOutItem();
		// rest call
		String content = Client.getInstance().getOutputFileContent(job, queueName, out.getOutItem());
		String fileName = FilenameUtils.getName(item.getFileRelativePath());
		return FilesUtil.writeToTempFile(fileName, content);
    }

    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 1.4
     */
    private class GetJcl extends DragFileLoading{
		/**
		 * @param shell 
		 * @param event 
		 * @param category
		 */
        public GetJcl(Shell shell, DragSourceEvent event, Category category) {
	        super(shell, event, category);
        }

		@Override
		public void execute() throws JemException {
			try {
				if (job.getJcl().getContent() == null){
					// REST call to download JCL
					job.getJcl().setContent(Client.getInstance().getJcl(job, getQueueName()));
				}
				// writes JCL
				File file = FilesUtil.writeJcl(job);
				if (FileTransfer.getInstance().isSupportedType(getEvent().dataType)) {
					getEvent().data = new String[] { file.getAbsolutePath() };
				}
			} catch (IOException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(FileDragListener.this, "Unable to get JCL!", "Error while getting JCL: " + e.getMessage(), MessageLevel.ERROR);
			}
		}
    }
    
    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 1.4
     */
    private class GetProducedOutputs extends DragFileLoading{
    	
		/**
		 * @param shell
		 * @param event
		 * @param category
		 */
        public GetProducedOutputs(Shell shell, DragSourceEvent event, Category category) {
	        super(shell, event, category);
        }

		@Override
        public void execute() throws JemException {
			// downloading a complete sub-category
            List<String> listFiles = new LinkedList<String>();
            for (ProducedOutput output : getCategory().getProducedOutputs()){
            	try {
            		// creates files 
            		File file = dragProducedOutput(output);
            		// adds to a list
            		listFiles.add(file.getAbsolutePath());
                } catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					Notifier.showMessage(FileDragListener.this, "Unable to get Output!", "Error while getting output file: " + e.getMessage(), MessageLevel.ERROR);
                } catch (RestException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					Notifier.showMessage(FileDragListener.this, "Unable to get Output!", "Error while getting output file: " + e.getMessage(), MessageLevel.ERROR);
                }
            }
            // if it's created files
            // it inform DND of them
            if (!listFiles.isEmpty()){
            	String[] filesArray = new String[listFiles.size()];
            	filesArray = listFiles.toArray(filesArray);
            	getEvent().data = filesArray;	
            }
        }
    }
    
    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 1.4
     */
    private class GetProducedOutput extends DragFileLoading{
		/**
		 * @param shell
		 * @param event
		 * @param output
		 */
        public GetProducedOutput(Shell shell, DragSourceEvent event, ProducedOutput output) {
	        super(shell, event, output);
        }

		@Override
		public void execute() throws JemException {
			try {
				// download a single files
				File file = dragProducedOutput(getOutput());
				getEvent().data = new String[] { file.getAbsolutePath() };
			} catch (RestException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(FileDragListener.this, "Unable to get Output!", "Error while getting output file: " + e.getMessage(), MessageLevel.ERROR);
			} catch (IOException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(FileDragListener.this, "Unable to get Output!", "Error while getting output file: " + e.getMessage(), MessageLevel.ERROR);
            }
		}    	
    }
    
}