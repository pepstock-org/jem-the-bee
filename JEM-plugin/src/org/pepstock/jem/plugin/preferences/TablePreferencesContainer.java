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
package org.pepstock.jem.plugin.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Activator;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.commons.JemColumnSortListener;
import org.pepstock.jem.plugin.commons.JemContentProvider;
import org.pepstock.jem.plugin.commons.JemTableColumn;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.event.PreferencesEnvironmentEventListener;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.util.Numbers;

/**
 * Container of table preferences, which contains all JEM environments coordinates.
 * This is called by Preferences in the Eclipse Menu.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class TablePreferencesContainer extends PreferencePage implements IWorkbenchPreferencePage, ShellContainer {

	private static final String TITLE = "Loading preferences error!";
	
	private static final String MSG = "Error while loading preferences, your preferences may not be loaded: ";
	
	private TablePreferences tablePreferences = new TablePreferences();
	
	private TableViewer tableViewer;

	private Button edit;

	private Button remove;
	
	private Label message;
	
	private Map<String, UpdateEvent> events = new HashMap<String, UpdateEvent>();
	
	private Map<String,Coordinate> cloneEnvironments = null;

	/**
	 * Constructs the table with all coordinates saved as preferences.
	 * Sets the preferences store.
	 */
	public TablePreferencesContainer() {
		super();
		this.noDefaultAndApplyButton();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#getShell()
	 */
    @Override
    public Shell getShell() {
    	return tableViewer.getControl().getShell();
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
    @Override
    public void init(IWorkbench arg0) {
		// loads preferences only if not loaded
		if (PreferencesManager.ENVIRONMENTS.isEmpty()){
			try {
				// loads all preferences
				PreferencesManager.load();
			} catch (StorageException e) {
				// if any errors related to Eclipse way to manage preferences
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(this, TITLE, MSG + e.getMessage(), MessageLevel.ERROR);
			} catch (Exception e) {
				// if any other errors, mainly de-serialization from XML 
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(this, TITLE, MSG + e.getMessage(), MessageLevel.ERROR);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    protected Control createContents(Composite parent) {
    	// clones environments
    	// necessary to be able to cancel wrong updates 
		cloneEnvironments = new ConcurrentHashMap<String,Coordinate>(PreferencesManager.ENVIRONMENTS);
		
		// Main container
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(Numbers.N_2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// list of environments label
		Label listLabel = new Label(composite, SWT.NONE);
		listLabel.setText("List of JEM environments:");
		new Label(composite, SWT.NONE);
		
		// Initializes TableViewer
		tableViewer = new TableViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		// loads all columns
		int count=0;
		for (JemTableColumn column : tablePreferences.getColumns()) {
			TableColumn tbColumn = new TableColumn(table, SWT.LEFT);
			tbColumn.setText(column.getName());
			tbColumn.setWidth(column.getWeight());
			tbColumn.addSelectionListener(new JemColumnSortListener(count, tableViewer));
			count++;
		}
		
		// sets providers 
		tableViewer.setContentProvider(new JemContentProvider<Coordinate>());
		tableViewer.setSorter(tablePreferences.getColumnSorter());
		tableViewer.setLabelProvider(tablePreferences.getLabelProvider());		
		// loads values
		tableViewer.setInput(cloneEnvironments.values());

		// Initializes Buttons
		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayout(new GridLayout(Numbers.N_1, false));
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = Numbers.N_80;
		buttons.setLayoutData(gd);

		// ADD button
		Button add = new Button(buttons, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// adds selection to ADD action
		add.addSelectionListener(new Add());

		// EDIT button
		edit = new Button(buttons, SWT.PUSH);
		edit.setText("Edit");
		edit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edit.setEnabled(false);
		// adds selection to EDIT action
		edit.addSelectionListener(new Edit());

		// REMOVE button
		remove = new Button(buttons, SWT.PUSH);
		remove.setText("Remove");
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.setEnabled(false);
		// adds selection to REMOVE action
		remove.addSelectionListener(new Remove());

		// adds selection listener
		tableViewer.addSelectionChangedListener(new TableChanged());

		// By default, double click goes in EDIT mode
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				edit.setSelection(true);
			}
		});

		// ERROR MESSAGE panel
		message = new Label(composite, SWT.NONE);
		message.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		// empty area
		new Label(composite, SWT.NONE);
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
    @Override
    public boolean performOk() {
		try {
			// clears and loads environments
			PreferencesManager.ENVIRONMENTS.clear();
			PreferencesManager.ENVIRONMENTS.putAll(cloneEnvironments);
			// stores preferences
			PreferencesManager.store();
			// fires all saved events
			for (UpdateEvent event : events.values()){
				for (PreferencesEnvironmentEventListener listener : PreferencesManager.getEnvlisteners()) {
					switch(event.getType()){
					case UpdateEvent.ADD:
						// fires the ADD new environment
						listener.environmentAdded(new EnvironmentEvent(this, event.getCoordinate()));
						break;
					case UpdateEvent.REMOVE: 
						// fires the REMOVE new environment
						listener.environmentRemoved(new EnvironmentEvent(this, event.getCoordinate()));
						break;
					case UpdateEvent.UPDATE: 
						// fires the UPDATE new environment
						listener.environmentUpdated(new EnvironmentEvent(this, event.getCoordinate()));
						break;
					default:
						// default is ADD
						listener.environmentAdded(new EnvironmentEvent(this, event.getCoordinate()));
						break;
					}
				}
			}
			// clears events
			events.clear();
		} catch (StorageException e) {
			// if any errors related to Eclipse way to manage preferences
			LogAppl.getInstance().ignore(e.getMessage(), e);
			Notifier.showMessage(this, "Saving preferences error!", "Error while saving preferences, your preferences may not be saved: " + e.getMessage(), MessageLevel.ERROR);
		} catch (Exception e) {
			// if any other errors, mainly de-serialization from XML 
			LogAppl.getInstance().ignore(e.getMessage(), e);
			Notifier.showMessage(this, "Saving preferences error!", "Error while saving preferences, your preferences may not be saved: " + e.getMessage(), MessageLevel.ERROR);
		}
		return true;
	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performCancel()
	 */
    @Override
    public boolean performCancel() {
    	// clears everything
    	// both events and clone environment 
		events.clear();
		cloneEnvironments.clear();
		try {
			// reload preferences
			PreferencesManager.load();
		} catch (StorageException e) {
			// if any errors related to Eclipse way to manage preferences
			LogAppl.getInstance().ignore(e.getMessage(), e);
			Notifier.showMessage(this, "Loading preferences error!", "Error while saving preferences, your preferences may not be loaed: " + e.getMessage(), MessageLevel.ERROR);
		} catch (Exception e) {
			// if any other errors, mainly de-serialization from XML 
			LogAppl.getInstance().ignore(e.getMessage(), e);
			Notifier.showMessage(this, "Loading preferences error!", "Error while saving preferences, your preferences may not be loaed: " + e.getMessage(), MessageLevel.ERROR);
		}
		return true;
	}

    /**
     * Checks if updated coordinate (ADD or EDIT) has default set. If yes, checks if 
     * all other defined coordinates there is any coordinate with default if yes,
     * it must create a new event with the update of default environment (disables it)
     * and put in the list
     * @param updatedCoordinate coordinate to check if has default
     */
    private void checkDefault(Coordinate updatedCoordinate) {
    	// if default has been checked
    	if (updatedCoordinate.isDefault()){
    		// scans all coordinates to check if another one was the default
    		for (Coordinate coordinate : cloneEnvironments.values()){
    			// checks only if coordinate has different name
    			// and was the default
    			if (!updatedCoordinate.getName().equalsIgnoreCase(coordinate.getName()) && coordinate.isDefault()){
    				// changes the default
    				coordinate.setDefault(false);
    				// creates an update event
    				UpdateEvent event = null;
    				// if coordinate is already in events to change
    				if (events.containsKey(coordinate.getName())){
    					// gets the existing event
    					UpdateEvent savedEvent = events.get(coordinate.getName());
    					// if saved event was in ADD, leaves ADD
    					// otherwise ALWAYS update
    					if (savedEvent.getType() == UpdateEvent.ADD){
    						event =  new UpdateEvent(coordinate, UpdateEvent.ADD);
    					} else {
    						event =  new UpdateEvent(coordinate, UpdateEvent.UPDATE);
    					}
    				} else {
    					// if not present
    					// creates a new event
    					event =  new UpdateEvent(coordinate, UpdateEvent.UPDATE);
    				}
    				// puts new ccordinate on the clone environment
    				cloneEnvironments.put(coordinate.getName(), coordinate);
    				// fires event
    				events.put(coordinate.getName(), event);
    			}
    		}
    	}
    }
    
    /**
     * Adds a new coordinates, showing the dialog to create a new coordinate
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class Add extends SelectionAdapter{
    	
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
        @Override
        public void widgetSelected(SelectionEvent evt) {
			// creates dialog
			CoordinateDialog dialog = new CoordinateDialog(getShell(), cloneEnvironments);
			// if OK, go ahead
			if (dialog.open() == Dialog.OK) {
				// gets new coordinate and check default
				Coordinate inserted = dialog.getCoordinate();
				checkDefault(inserted);
				// check if environment name is already in events map
				if (events.containsKey(inserted.getName())){
					// if is in the table can be ONLY if before there is a remove
					UpdateEvent event = events.get(inserted.getName());
					if (event.getType() == UpdateEvent.REMOVE){
						// tests if is a old preferences
						// if yes, means that it removes and re-added
						if (PreferencesManager.ENVIRONMENTS.containsKey(inserted.getName())){
							// exists in preferences, decides for update or add
							events.put(inserted.getName(), new UpdateEvent(inserted, UpdateEvent.UPDATE));
						} else {
							events.put(inserted.getName(), new UpdateEvent(inserted, UpdateEvent.ADD));
						}
					} else {
						// impossible to be here
						// it's not possible to have event in Update and add
					}
				} else {
					events.put(inserted.getName(), new UpdateEvent(inserted, UpdateEvent.ADD));
				}
				// adds in clone envs map and refeshes the table
				cloneEnvironments.put(inserted.getName(), inserted);
				tableViewer.refresh();
			}
		}
    }
    
    /**
     * Edit an existing coordinate, opening a coordinate dialog
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class Edit extends SelectionAdapter{

    	/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
        @Override
        public void widgetSelected(SelectionEvent evt) {
			// gets the selected coordinate and checks is the same of client
			// that means is in use
			IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
			Coordinate template = (Coordinate) selection.getFirstElement();
			if (Client.getInstance().isLogged() && template.getName().equalsIgnoreCase(Client.getInstance().getCurrent().getName())) {
				Notifier.showMessage(TablePreferencesContainer.this, "Unable to remove " + template.getName(), template.getName() + " is currently in use and then is not possible to update. Please disconnect before updating", MessageLevel.ERROR);
				return;
			}
			// creates the dialog to update
			CoordinateDialog dialog = new CoordinateDialog(getShell(), template, cloneEnvironments);
			if (dialog.open() == Dialog.OK) {
				// gets coordinates and checks default
				Coordinate updated = dialog.getCoordinate();
				checkDefault(updated);
				UpdateEvent event = null;
				// check if environment name is already in events map
				if (events.containsKey(updated.getName())) {
					UpdateEvent savedEvent = events.get(updated.getName());
					// event was in add, leaves ADD action, changing coordinate
					if (savedEvent.getType() == UpdateEvent.ADD) {
						event = new UpdateEvent(updated, UpdateEvent.ADD);
					} else {
						// event was in update, leaves UPDATE action, changing coordinate
						event = new UpdateEvent(updated, UpdateEvent.UPDATE);
					}
				} else {
					// otherwise ALWAYS UPDATE
					event = new UpdateEvent(updated, UpdateEvent.UPDATE);
				}
				// adds in clone envs map and refeshes the table
				cloneEnvironments.put(updated.getName(), updated);
				events.put(updated.getName(), event);
				tableViewer.refresh();
			}
		}

    }
    
    /**
     * Removes a coordinate from the list of saved coordinates
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class Remove extends SelectionAdapter{
    	/* (non-Javadoc)
    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
    	 */
    	@Override
    	public void widgetSelected(SelectionEvent evt) {
    		// gets the selected coordinate and checks is the same of client
    		// that means is in use
    		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
    		Coordinate toBeRemoved = (Coordinate) selection.getFirstElement();
    		if (Client.getInstance().isLogged() && toBeRemoved.getName().equalsIgnoreCase(Client.getInstance().getCurrent().getName())) {
    			Notifier.showMessage(TablePreferencesContainer.this, "Unable to remove " + toBeRemoved.getName(), toBeRemoved.getName() + " is currently in use and then is not possible to remove. Please disconnect before removing", MessageLevel.ERROR);
    			return;
    		}
    		// creates event to remove and refreshes the table
    		Coordinate removed = cloneEnvironments.remove(toBeRemoved.getName());
    		events.put(removed.getName(), new UpdateEvent(removed, UpdateEvent.REMOVE));
    		tableViewer.refresh();
    	}
    }
    
    /**
     * Is the listener on the table, because some logics are a little complex.
     * It enables and disables buttons when an item of the table has been selected.
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class TableChanged implements ISelectionChangedListener{
    	@Override
		public void selectionChanged(SelectionChangedEvent event) {
			// if no select, no Remove, no Edit
			if (tableViewer.getTable().getSelectionCount() == 0) {
				edit.setEnabled(false);
				remove.setEnabled(false);
			} else {
				// gets the selected coordinate and checks is the same of client
				// that means is in use
				// if in use set message and disable buttons
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Coordinate toBeRemoved = (Coordinate) selection.getFirstElement();
				// if logged and the selected coordinate is used
				// it disables edit and remove
				// you must disconnect before to change it
				if (Client.getInstance().isLogged() && toBeRemoved.getName().equalsIgnoreCase(Client.getInstance().getCurrent().getName())) {
					edit.setEnabled(false);
					remove.setEnabled(false);
					message.setText("Unable to update the configuration of '"+toBeRemoved.getName()+"' because is currently in use");
					message.pack(true);
					return;
				}
				// enables buttons
				edit.setEnabled(true);
				remove.setEnabled(true);
				message.setText("");
				message.pack(true);
			}
		}
    }
}