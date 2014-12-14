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
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.executors.gfs.GetFilesList;
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
 * @version 1.4
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
	
	// list all all columns (name and dimension) of the table
	private final Collection<JemTableColumn> columns = Collections.unmodifiableCollection(Arrays.asList(new JemTableColumn[]{ 
			new JemTableColumn("Name", 50),
			new JemTableColumn("Size (bytes)", 15),
			new JemTableColumn("Last modified", 20),
			new JemTableColumn("Path name", 15),
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
		
		// adds drop listener of DND
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { FileTransfer.getInstance() };
		viewer.addDragSupport(operations, transferTypes, dragListener);
		dragListener.setTableViewer(viewer);
		dragListener.setType(type);
		
		// adds drop listener of DND
		 viewer.addDropSupport(operations, transferTypes, new FilesUploadDropListener(viewer, searcher, type));


		// total amount of items
		Composite compositeTot = new Composite(this.composite, SWT.NONE);
		compositeTot.setLayout(new GridLayout(2, false));
		compositeTot.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		// adds label with total items
		Label numberLabel = new Label(compositeTot, SWT.NONE);
		numberLabel.setText("&Total Files: ");
		numberLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		// label which shows the amount of displayed files
		number = new Label(compositeTot, SWT.NONE);
		number.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		number.setText(String.valueOf(0));
		number.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), viewer.getClass().getName());
	}
	
	/**
	 * returns the name of GFS type
	 * @return the name
	 */
	public String getName() {
		return GfsFileType.getName(type);
	}
	
	/**
	 * Returns the GFS type
	 * @see GfsFileType
	 * @return the type
	 */
	public int getType() {
		return type;
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
					// reset the table data
					getViewer().setInput(data);
					// and sets the file number
					number.setText(String.valueOf(data.size()));
				}
			});
		}
	}
	
	/**
	 * Creates and fill table viewer
	 */
	public void createViewer() {
		// creates the content provider
		viewer.setContentProvider(new JemContentProvider<GfsFile>());
		// and the sorter
		viewer.setSorter(new ExplorerColumnSorter());
		// creates all columns
		createColumns();
		// sets teh label provider
		viewer.setLabelProvider(new GfsLabelProvider());
		// sets the data
		viewer.setInput(data);
		// changes the table 
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
						refresh(file.getLongName(), file.getDataPathName());
					} else {
						//only DATA and SOURCE can be downloaded
						if ((type == GfsFileType.DATA) || (type == GfsFileType.SOURCE)){
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
    
    /**
     * Method to refresh table with result
     */
    public void refresh(){
    	// class refresh passing the text of searcher
    	refresh(searcher.getText());
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.Refresher#refresh(java.lang.String)
	 */
    @Override
    public void refresh(String filter) {
    	refresh(filter, null);
    }
    
    /**
     * Method to refresh table with result, using filter and data path name
	 * @param filter the folder path to search in JEM.
	 * @param pathName data path name
     */
    public void refresh(String filter, String pathName){
		FilesListLoading loading = new GFSFilesListLoading(filter, pathName);
		loading.run();	
    }

	/**
	 * Creates all columns of explorer table
	 */
	private void createColumns() {
		// needs to set the column sorter listener
		int count=0;
		// scans all columns
		for (JemTableColumn column : getColumns()) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(getViewer(), SWT.NONE);
			TableColumn tblColumn = tableViewerColumn.getColumn();
			//Specify width using weights
			// setting the minimum width
			layout.setColumnData(tblColumn, new ColumnWeightData(column.getWeight(), ColumnWeightData.MINIMUM_WIDTH, true));
			tblColumn.setText(column.getName());
			// adds sorter using the COUNT
			tblColumn.addSelectionListener(new JemColumnSortListener(count, viewer));
			count++;
		}
	}
	
	/**
	 * Extends the file loading, dedicated for all actions to GFS
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	private class GFSLoading extends FileLoading{

		/**
		 * Creates the loading file using the shell container, type of GFS to use
		 * and file 
		 * @see GfsFileType
		 * @param shellContainer ALWAYS the explorer container
		 * @param type type of GFS file system
		 * @param file file to download
		 */
        public GFSLoading(ShellContainer shellContainer, int type, GfsFile file) {
	        super(shellContainer.getShell(), type, file);
        }
        
		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        protected void execute() throws JemException {
			try {
				String content = null;
				// if source of data, you can download
				if (type == GfsFileType.DATA || type == GfsFileType.SOURCE){
					content = Client.getInstance().getGfsFile(type, getFile().getLongName(), getFile().getDataPathName());
				} else { 
					// otherwise you can't download
					return;
				}
				// activate the editor
				// going in editor with the content of the file
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new StringEditorInput(content, getFile().getName()), "org.eclipse.ui.DefaultTextEditor");		
			} catch (PartInitException e) {
				// if any errors from editing 
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to open the editor!", 
						"Error occurred during opening of editor: "+e.getMessage(), MessageLevel.ERROR);
			} catch (JemException e) {
				// if any errors to download the file
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to get "+getFile().getName()+"!", 
						"Error occurred during retrieving the file  '"+getFile().getName()+"': "+e.getMessage(), MessageLevel.ERROR);
			}
		}
	}
	
	/**
	 * File loading used when you search the content of a path
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	private class GFSFilesListLoading extends FilesListLoading{

		/**
		 * Creates object with the folder path to search in JEM.
		 * @param filter the folder path to search in JEM.
		 * @param pathName data path name
		 */
        public GFSFilesListLoading(String filter, String pathName) {
	        super(filter, pathName);
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
		public void execute() throws JemException {
			try {
				String filter = null;
				// the text on searcher is empty, uses a root path
				if (getFilter().trim().length() == 0){
					filter = GetFilesList.ROOT_PATH;
				} else {
					// the text on searcher is star, uses a root path otherwise
					// the content of teh text box
					filter = "*".equalsIgnoreCase(getFilter()) ? GetFilesList.ROOT_PATH : getFilter();
				}
				// gets  data
				data = DataLoader.loadData(type, filter, getPathName());
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						// loads tables
						getViewer().setInput(data);
						if (data != null){
							// sets amount
							number.setText(String.valueOf(data.size()));
						} else {
							// resets the amount of files
							number.setText(String.valueOf(0));
						}
						// sets complete folder name to text
						searcher.setText(getFilter());
					}
				});
			} catch (JemException e) {
				// if any errors from REST APi
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(getShell(), "Unable to load data", e.getMessage(), MessageLevel.ERROR);
			}
		}
        
		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#getDisplay()
		 */
        @Override
        public Display getDisplay() {
            return getShell().getDisplay();
        }
	}
}
