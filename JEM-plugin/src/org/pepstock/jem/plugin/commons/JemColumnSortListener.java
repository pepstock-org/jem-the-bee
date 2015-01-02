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
package org.pepstock.jem.plugin.commons;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * Common listener to add to all columns of table, to catch sort and call JemTableSorter to 
 * compare rows.
 *  
 * @author Andrea "Stock" Stocchero
 *
 */
public class JemColumnSortListener implements SelectionListener {

	private int index;

	private TableViewer viewer;

	/**
	 * Constructs the object with column index and table viewer.
	 * 
	 * @param column column index
	 * @param viewer table viewer 
	 */
	public JemColumnSortListener(int column, TableViewer viewer){
		this.viewer = viewer;
		index = column;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
    @Override
    public void widgetSelected(SelectionEvent event) {
    	// gets sorter
		JemColumnSorter<?> sorter = (JemColumnSorter<?>)viewer.getSorter();
		
		// if is asking sort for the same column, then change online the ascending mode
		if (sorter.getIndex() == index){
			boolean order = (sorter.isAscending()) ? false : true;
			sorter.setAscending(order);
		} else{
			sorter.setIndex(index);	
			sorter.setAscending(true);				
		}
		// refreshes table
		viewer.refresh();
	}

    
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    	// do nothing
    }
}
