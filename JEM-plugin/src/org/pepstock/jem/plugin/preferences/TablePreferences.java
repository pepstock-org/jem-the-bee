/*******************************************************************************
 * Copyright (c) 2012-2014 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.preferences;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.pepstock.jem.plugin.commons.JemColumnSorter;
import org.pepstock.jem.plugin.commons.JemLabelProvider;
import org.pepstock.jem.plugin.commons.JemTableColumn;

/**
 * Table preferences definition, with the list of columns, label provider and column sorter for the JEM coordinates table.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class TablePreferences  {
	
	private static final Collection<JemTableColumn> COLUMNS = Collections.unmodifiableCollection(Arrays.asList(new JemTableColumn[]{ 
			new JemTableColumn("Host", 175),
			new JemTableColumn("REST context", 80),
			new JemTableColumn("Name", 90),
			new JemTableColumn("Userid", 60),
			new JemTableColumn("Password", 90),
			new JemTableColumn("Default", 80)
	}));

	/**
	 * Returns the list of columns
	 * @return the list of columns
	 */
	public Collection<JemTableColumn> getColumns() {
		return COLUMNS;
	}

	/**
	 * Returns the label provider for table.
	 * @return the label provider for table.
	 */
	public JemLabelProvider<Coordinate> getLabelProvider() {
		return new LabelProvider();
	}

	/**
	 * Returns the column sorter of table preferences.
	 * @return the column sorter of table preferences.
	 */
	public JemColumnSorter<Coordinate> getColumnSorter() {
		return new Sorter();
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	private static class LabelProvider extends JemLabelProvider<Coordinate>{
		
		private final Random random = new Random();
		
		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.commons.JemLabelProvider#getTextColumn(java.lang.Object, int)
		 */
        @Override
        public String getTextColumn(Coordinate element, int index) {
			switch (index) {
			case 0:
				// host url
				return element.getHost();
			case 1:
				// REST context
				return element.getRestContext();
			case 2:
				// JEM environment name
				return element.getName();
			case 3:
				// userid to use to connect
				return element.getUserid();
			case 4:
				// crypted password
				return StringUtils.repeat("*", (int) (random.nextInt() * 16));
			case 5:
				// is defaut
				return element.isDefault() ? "√" : "";
			default:
				// nothing
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.commons.JemLabelProvider#getImageColumn(java.lang.Object, int)
		 */
        @Override
        public Image getImageColumn(Coordinate element, int index) {
	        return null;
        }
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	private static class Sorter extends JemColumnSorter<Coordinate>{
		
        private static final long serialVersionUID = 1L;

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
        @Override
        public int compare(Coordinate o1, Coordinate o2) {
			int diff = 0;
			switch(getIndex()){
			case 0:
				// sorts by host
				diff = o1.getHost().compareTo(o2.getHost());
				break;
			case 1:
				// sorts by rest context
				diff = o1.getRestContext().compareTo(o2.getRestContext());
				break;
			case 2: 
				// sorts by name
				diff = o1.getName().compareTo(o2.getName());
				break;
			case 3:
				// sorts by userid
				diff = o1.getUserid().compareTo(o2.getUserid());
				break;
			case 4:
				// sorts by password
				// nop
				break;
			case 5:
				// sorts by default
				diff = (o1.isDefault() ? 1 : 0) - (o2.isDefault() ? 1 : 0);
				break;
			default:
				// sorts by jobname
				diff = o1.getName().compareTo(o2.getName());
				break;
			}
			// checks if Ascending otherwise negative
			return isAscending() ? diff : -diff;
        }

	}
}
