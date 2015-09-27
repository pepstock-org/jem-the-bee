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
package org.pepstock.jem.plugin.views.explorer;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.FilesUtil;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.rest.RestException;

/**
 * File drag listener utility, enables only DATA and SOURCE opening.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class FileDragListener implements DragSourceListener, ShellContainer {

	private int type = -1;

	private TableViewer tableViewer = null;

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the tableViewer
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * @param tableViewer
	 *            the tableViewer to set
	 */
	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd
	 * .DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		// nop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd
	 * .DragSourceEvent)
	 */
	@Override
	public void dragSetData(final DragSourceEvent event) {
		// checks if datatype is right
		if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			// gets selected file
			IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
			Object selectedNode = selection.getFirstElement();
			if (selectedNode instanceof GfsFile) {
				GfsFile file = (GfsFile) selectedNode;
				// is not able to download directory. It's too riskly
				// only data or source
				if (!file.isDirectory() && ((type == GfsFileType.DATA) || (type == GfsFileType.SOURCE))) {
					// creates loading...
					FileLoading loading = new GFSFileLoading(getShell(), event, type, file);
					loading.run();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.
	 * DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		// gets selected file
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		Object selectedNode = selection.getFirstElement();
		if (selectedNode instanceof GfsFile) {
			GfsFile file = (GfsFile) selectedNode;
			// is not able to download directory. It's too riskly
			if (file.isDirectory()) {
				event.doit = false;
			} else if ((type != GfsFileType.DATA) && (type != GfsFileType.SOURCE)) {
				event.doit = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.plugin.util.ShellContainer#getShell()
	 */
	@Override
	public Shell getShell() {
		return tableViewer.getControl().getShell();
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	private class GFSFileLoading extends DragFileLoading{

		/**
		 * @param shell
		 * @param event
		 * @param type
		 * @param file
		 */
        public GFSFileLoading(Shell shell, DragSourceEvent event, int type, GfsFile file) {
	        super(shell, event, type, file);
        }

		@Override
		public void execute() throws JemException {
			try {
				// gets content from JEM
				byte[] content = null;
				if (type == GfsFileType.DATA || type == GfsFileType.SOURCE) {
					content = Client.getInstance().getGfsFile(type, getFile().getLongName(), getFile().getDataPathName());
				} else {
					return;
				}
				// writes file on temporary folder
				File file = FilesUtil.writeToTempFile(getFile().getName(), content);
				// sets data for DND
				if (FileTransfer.getInstance().isSupportedType(getEvent().dataType)) {
					getEvent().data = new String[] { file.getAbsolutePath() };
				}
			} catch (RestException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to load file "+getFile().getName()+" !", 
						"Error occurred during retrieving the file '"+getFile().getName()+"': "+e.getMessage(), MessageLevel.ERROR);
			} catch (IOException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to write file "+getFile().getName()+" !", 
						"Error occurred during writing the file '"+getFile().getName()+"': "+e.getMessage(), MessageLevel.ERROR);
            }
		}
	}
}