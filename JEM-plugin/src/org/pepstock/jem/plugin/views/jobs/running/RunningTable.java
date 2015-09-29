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
package org.pepstock.jem.plugin.views.jobs.running;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.TabFolder;
import org.pepstock.jem.plugin.commons.JemTableColumn;
import org.pepstock.jem.plugin.views.jobs.JobColumnSorter;
import org.pepstock.jem.plugin.views.jobs.JobsTableContainer;
import org.pepstock.jem.rest.entities.JobQueue;

/**
 * Table container of jobs in RUNNING queue of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class RunningTable extends JobsTableContainer {
	
	/**
	 * Readable name of HC queue
	 */
	public static final String NAME = "Running";
	
	// list of columns of the table to be showed
	private static final Collection<JemTableColumn> COLUMNS = Collections.unmodifiableCollection(Arrays.asList(new JemTableColumn[]{ 
			new JemTableColumn("Name"),
			new JemTableColumn("Type"),
			new JemTableColumn("User"),
			new JemTableColumn("Step"),
			new JemTableColumn("Domain"),
			new JemTableColumn("Affinity"),
			new JemTableColumn("Running Time"),
			new JemTableColumn("Memory (MB)"),
			new JemTableColumn("Member")

	}));
	
	// labels and sorter providers
	private static final RunningLabelProvider LABEL_PROVIDER = new RunningLabelProvider();
	private static final RunningColumnSorter COLUMN_SORTER = new RunningColumnSorter();
	
	/**
	 * Constructor of object with folder and style 
	 * @param parent tabbed panel, container of this
	 * @param style style for composites
	 */
    public RunningTable(TabFolder parent, int style) {
	    super(parent, style, JobQueue.RUNNING);
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
	 * @see jemplugin.views.JobViewPart#getLabelProvider()
	 */
    @Override
    public ILabelProvider getLabelProvider() {
    	return LABEL_PROVIDER;
    }
	/* (non-Javadoc)
	 * @see jemplugin.views.JobViewPart#getColumnSorter()
	 */
    @Override
    public JobColumnSorter getColumnSorter() {
	    return COLUMN_SORTER;
    }
}
