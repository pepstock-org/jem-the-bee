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
package org.pepstock.jem.plugin.views.explorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.pepstock.jem.GfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.commons.JemColumnSortListener;
import org.pepstock.jem.plugin.commons.JemContentProvider;
import org.pepstock.jem.plugin.commons.JemTableColumn;
import org.pepstock.jem.plugin.commons.StringEditorInput;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.plugin.views.Searcher;
import org.pepstock.jem.plugin.views.jobs.Refresher;

/**
 * Table container of explorer of GFS. It contains a table for each type of data in GFS.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ExplorerTableContainer implements ShellContainer, Refresher{

	// internal composite, necessary to avoid null pointer during initialization
	private Composite composite;
	
	// table
	private TableViewer viewer;
	
	private final FileDragListener dragListener = new FileDragListener();
	
	private Searcher searcher = null;
	
	private Label number;
	
	private TableColumnLayout layout = new TableColumnLayout();

	private Collection<GfsFile> data = new ArrayList<GfsFile>();
	
	private int type = -1;
	
	private final Collection<JemTableColumn> columns = Collections.unmodifiableCollection(Arrays.asList(new JemTableColumn[]{ 
			new JemTableColumn("Name", 50),
			new JemTableColumn("Size (bytes)", 25),
			new JemTableColumn("Last modified", 25)
	}));

	/**
	 * Constructor of object with folder and type 
	 * @param parent tabbed panel, container of this
	 * @param style style for composites
	 * @param type data type (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 */
	public ExplorerTableContainer(TabFolder parent, int style, int type) {
		this.type = type;
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
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { FileTransfer.getInstance() };
		viewer.addDragSupport(operations, transferTypes, dragListener);
		dragListener.setTableViewer(viewer);
		dragListener.setType(type);

		// total amount of items
		Composite compositeTot = new Composite(this.composite, SWT.NONE);
		compositeTot.setLayout(new GridLayout(2, false));
		compositeTot.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		// adds label with total items
		Label numberLabel = new Label(compositeTot, SWT.NONE);
		numberLabel.setText("&Total Files: ");
		numberLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		number = new Label(compositeTot, SWT.NONE);
		number.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		number.setText(String.valueOf(0));
		number.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), viewer.getClass().getName());
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return PathName.getPathName(type);
	}
	
	/**
	 * Returns the collection of files 
	 * @return the collection of files
	 */
	public Collection<GfsFile> getData(){
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
	 * Returns lists of table columns
	 * @return collection with columns of table
	 */
	public Collection<JemTableColumn> getColumns(){
		return columns;
	}

	/**
	 * Sets all components enabled or not
	 * @param enabled if <code>true</code>, enables the components, otherwise disabled them.
	 */
	public void setEnabled(boolean enabled){
		searcher.setEnabled(enabled);
		viewer.getTable().setEnabled(enabled);
		if (!enabled){
			// clear data and
			// reset table
			data.clear();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getViewer().setInput(data);
					number.setText(String.valueOf(data.size()));
				}
			});
		}
	}
	
	/**
	 * Creates and fill table viewer
	 */
	public void createViewer() {
		viewer.setContentProvider(new JemContentProvider<GfsFile>());
		viewer.setSorter(new ExplorerColumnSorter());
		//
		createColumns();
		
		viewer.setLabelProvider(new GfsLabelProvider());
		viewer.setInput(data);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		// add Double Click listener
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// gets GFS file
				ISelection selection = viewer.getSelection();
				GfsFile file = (GfsFile)((IStructuredSelection) selection).getFirstElement();
				if (file != null){
					// DRILL down only for directory
					if (file.isDirectory()){
						refresh(file.getLongName());
					} else {
						//only DATA and SOURCE can be downloaded
						if ((type == GfsFile.DATA) || (type == GfsFile.SOURCE)){
							// load file 
							FileLoading loading = new GFSLoading(ExplorerTableContainer.this, type, file);
							loading.run();
						}
					}
				}
			}
		});
	}
	
	
	
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
		FilesListLoading loading = new GFSFilesListLoading(filter);
		loading.run();
    }

	/**
	 * Creates all columns of explorer table
	 */
	private void createColumns() {
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
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	private class GFSLoading extends FileLoading{

		/**
		 * @param shellContainer
		 * @param type
		 * @param file
		 */
        public GFSLoading(ShellContainer shellContainer, int type, GfsFile file) {
	        super(shellContainer.getShell(), type, file);
        }

		@Override
		public void execute() throws JemException {
			try {
				String content = null;
				if (type == GfsFile.DATA){
					content = Client.getInstance().getGfsFileData(getFile().getLongName());
				} else if (type == GfsFile.SOURCE){
					content = Client.getInstance().getGfsFileSource(getFile().getLongName());
				} else { 
					return;
				}
				// activate the editor
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new StringEditorInput(content, getFile().getName()), "org.eclipse.ui.DefaultTextEditor");		
			} catch (PartInitException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to open the editor!", 
						"Error occurred during opening of editor: "+e.getMessage(), MessageLevel.ERROR);
			} catch (JemException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to get "+getFile().getName()+"!", 
						"Error occurred during retrieving the file  '"+getFile().getName()+"': "+e.getMessage(), MessageLevel.ERROR);
			}
		}
	
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	private class GFSFilesListLoading extends FilesListLoading{
		/**
		 * @param filter
		 */
        public GFSFilesListLoading(String filter) {
	        super(filter);
        }

		@Override
		public void execute() throws JemException {
			try {
				// gets  data
				data = DataLoader.loadData(type, getFilter());
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						// loads tables
						getViewer().setInput(data);
						// sets amount
						number.setText(String.valueOf(data.size()));
						// sets complete folder name to text
						searcher.setText(getFilter());
					}
				});
			} catch (JemException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(getShell(), "Unable to load data", e.getMessage(), MessageLevel.ERROR);
			}
		}

		@Override
        public Display getDisplay() {
            return getShell().getDisplay();
        }
	}
}
