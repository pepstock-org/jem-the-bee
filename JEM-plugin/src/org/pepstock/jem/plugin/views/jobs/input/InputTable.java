/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.input;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.TabFolder;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.commons.JemTableColumn;
import org.pepstock.jem.plugin.views.jobs.JobColumnSorter;
import org.pepstock.jem.plugin.views.jobs.JobsTableContainer;
import org.pepstock.jem.rest.entities.Jobs;

/**
 * Table container of jobs in INPUT queue of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class InputTable extends JobsTableContainer {

	/**
	 * Readable name of queue
	 */
	public static final String NAME = "Input";
	
	// list of columns of the table to be showed
	private static final Collection<JemTableColumn> COLUMNS = Collections.unmodifiableCollection(Arrays.asList(new JemTableColumn[]{ 
			new JemTableColumn("Name"),
			new JemTableColumn("Type"),
			new JemTableColumn("User"),
			new JemTableColumn("Environment"),
			new JemTableColumn("Domain"),
			new JemTableColumn("Affinity"),
			new JemTableColumn("Submitted Time"),
			new JemTableColumn("Priority"),
			new JemTableColumn("Memory (MB)"),
			new JemTableColumn("Hold")
	}));

	// labels and sorter providers
	private static final InputLabelProvider LABEL_PROVIDER = new InputLabelProvider();
	private static final InputColumnSorter COLUMN_SORTER = new InputColumnSorter();

	/**
	 * Constructor of object with folder and style 
	 * @param parent tabbed panel, container of this
	 * @param style style for composites
	 */
    public InputTable(TabFolder parent, int style) {
	    super(parent, style, Queues.INPUT_QUEUE);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.tabbedQueues.QueueTab#getName()  
	 */
    @Override
    public String getName() {
	    return NAME;
    }

    /* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.JobsTablePanel#getColumns()
	 */
    @Override
	public Collection<JemTableColumn> getColumns(){
		return COLUMNS;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.JobsTableContainer#getLabelProvider()
	 */
    @Override
    public ILabelProvider getLabelProvider() {
    	return LABEL_PROVIDER;    
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.JobsTableContainer#getColumnSorter()
	 */
    @Override
    public JobColumnSorter getColumnSorter() {
    	return COLUMN_SORTER;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.tabbedQueues.QueueTab#loadData(java.lang.String)
	 */
    @Override
    public Jobs loadData(String filter) throws JemException {
    	return Client.getInstance().refreshInput(filter);
    }
}
