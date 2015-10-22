/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.gwt.client.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;

/**
 * Is generic table, which uses a inspectListener to notify when row is selected.<br>
 * Has a abstract method that all object panel must implement to provide the columns to show and 
 * the sort by comparator.
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T> entity to show in the tabe
 * 
 */
public abstract class AbstractTable<T> {
	
	static {
		DefaultTablePagerResources.INSTANCE.styles().ensureInjected();
	}
	
	/**
	 * The default table page size
	 */
	public static final int DEFAULT_PAGE_SIZE = 25;
	
	/**
	 * The default check column width
	 */
	public static final int DEFAULT_CHECK_COLUMN_WIDTH = 23;
	
	private final List<T> emptyList = new ArrayList<T>();

	private CellTable<T> table = null;

	private IndexedColumnComparator<T> comparator = null;
	
	private SimplePager pager = null;

	private Collection<T> dataProvider = emptyList;

	private AsyncDataProvider<T> provider = null;

	private InspectListener<T> inspectListener = null;
	
	private boolean hasFilterableHeader = false;
	
	/**
	 * Constructs the default table
	 */
	public AbstractTable() {
		this((Resources) GWT.create(DefaultTableStyle.class), (SimplePager.Resources)GWT.create(DefaultTablePagerResources.Resources.class), DEFAULT_PAGE_SIZE);
	}
	
	/**
	 * Constructs the default table
	 * @param filterableHeaders <code>true</code> if this table will contains {@link FilterableHeader}s
	 */
	public AbstractTable(boolean filterableHeaders) {
		this((Resources) GWT.create(DefaultTableStyle.class), (SimplePager.Resources)GWT.create(DefaultTablePagerResources.Resources.class), DEFAULT_PAGE_SIZE, filterableHeaders);
	}

	
	/**
	 * Construct the UI with custom table and pager styles and default page size
	 * @param resources the table style
	 * @param pagerResources the pager style
	 */
	public AbstractTable(CellTable.Resources resources, SimplePager.Resources pagerResources) {
		this(resources, pagerResources, DEFAULT_PAGE_SIZE);
	}

	/**
	 * Construct the UI with default table and pager styles and custom page size
	 * @param pageSize the page sizes (<code>Integer.MAX_VALUE</code> for maximum page size and hides the pager)
	 */
	public AbstractTable(int pageSize) {
		this((Resources) GWT.create(DefaultTableStyle.class), (SimplePager.Resources)GWT.create(DefaultTablePagerResources.Resources.class), pageSize);
	}

	/**
	 * Create table with custom style and custom page size (intended to be used with <code>Integer.MAX_VALUE</code>
	 * @param resources table style
	 * @param pageSize the table page size
	 */
	public AbstractTable(CellTable.Resources resources, int pageSize) {
		this(resources, (SimplePager.Resources)GWT.create(DefaultTablePagerResources.Resources.class), pageSize);
	}

	/**
	 * Create table with custom styles and custom page size
	 * @param resources table style
	 * @param pagerResources pager style
	 * @param pageSize the table page size
	 */
	public AbstractTable(CellTable.Resources resources, SimplePager.Resources pagerResources, final int pageSize) {
		this(resources, pagerResources, pageSize, false);
	}
	
	/**
	 * Contruct the table
	 * @param resources table style
	 * @param pagerResources pager style
	 * @param pageSize page size
	 * @param filterableHeaders <code>true</code> if contains {@link FilterableHeader}s
	 */
	protected AbstractTable(CellTable.Resources resources, SimplePager.Resources pagerResources, final int pageSize, boolean filterableHeaders) {
		// creates the CellTable (with or without custom resources styles)
		if (resources != null) {
			table = new CellTable<T>(pageSize, resources);
		} else {
			table = new CellTable<T>();
		}
		
		// save the filterableHeader flag
		hasFilterableHeader = filterableHeaders;
		
		// sets keyboard disable so you couldn't select the row without checking the box
		// sets selection, pagesize and dimensions
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		
		table.setPageSize(pageSize);
		table.setWidth(Sizes.HUNDRED_PERCENT);
		table.setHeight(Sizes.HUNDRED_PERCENT);
		
		// Create a Pager to control the table.
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(DefaultTablePagerResources.INSTANCE.styles().background());

		// Add a selection model to handle user selection.
		MultiSelectionModel<T> selectionModel = new MultiSelectionModel<T>();
		table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<T> createCheckboxManager(0));
		
		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		// AsyncDataPRrovider.
		AsyncHandler columnSortHandler = new AsyncHandler(table);
		table.addColumnSortHandler(columnSortHandler);

		// asks to implementor to have the comparator to sort capabilities
		comparator = initCellTable(table);

		// uses Async provider
		provider = new AbstractTableAsyncDataProvider();
		// add table panel to data provider
		provider.addDataDisplay(table);
		provider.updateRowCount(dataProvider.size(), true);

		// sets table and page size to pager
		pager.setDisplay(table);
		pager.setPageSize(pageSize);

		// starts empty
		setRowData(null);
	}
	
	/**
	 * @return the inspectListener
	 */
	public InspectListener<T> getInspectListener() {
		return inspectListener;
	}

	/**
	 * @param listener
	 *            the inspectListener to set
	 */
	public void setInspectListener(InspectListener<T> listener) {
		this.inspectListener = listener;
	}

	/**
	 * @return table of objects
	 */
	public CellTable<T> getTable() {
		return table;
	}

	/**
	 * @return return the pager. This method is used for rendering 
	 */
	public SimplePager getPager() {
		return pager;
	}

	/**
	 * @return <code>true</code> if this table has {@link FilterableHeader}s
	 */
	public boolean hasFilterableHeaders() {
		return hasFilterableHeader;
	}
	
	/**
	 * Sets the collection of objects as data to show in the table
	 * @param collection collections of objects
	 */
	public final void setRowData(Collection<T> collection) {
		if (collection != null) {
			if (!collection.isEmpty()) {
				dataProvider = collection;
			} else {
				dataProvider = emptyList;
			}
		} else {
			dataProvider = emptyList;
		}
		provider.updateRowCount(dataProvider.size(), true);
	}

	/**
	 * Called to have the comparator to sort and to add columns to the table. Is abstract because every view
	 * can have own columns to show. 
	 * 
	 * @param table table UI component
	 * @return comparator to use on sort
	 */
	public abstract IndexedColumnComparator<T> initCellTable(CellTable<T> table);
	
	private class AbstractTableAsyncDataProvider extends AsyncDataProvider<T> {

		/**
		 * Using the ager info, visible range and table size,
		 * decides which rows to show in the table 
		 */
		@Override
		public void onRangeChanged(HasData<T> tb) {
			int pageSize = table.getPageSize();
			int start = 0;
			// pager's gone less than zero, minimum is 0!
			if (pager.getPage() <= 0) {
				start = 0;
			} else {
				// otherwise calculate the first row to show
				start = pager.getPage() * pageSize;
			}
			// calculate the last row to show
			int end = start + pageSize;
			// checks if end is beyond of data size.
			end = end >= dataProvider.size() ? dataProvider.size() : end;

			// sorts the data before to show them
			List<T> list = new ArrayList<T>(dataProvider);
			// gets the sort column list
			ColumnSortList sortList = table.getColumnSortList();
			if (sortList != null) {
				if (sortList.size() > 0) {
					// gets the column to sort
					final ColumnSortInfo info = sortList.get(0);
					// gets index in the table
					@SuppressWarnings("unchecked")
					Column<T, String> c = (Column<T, String>) info.getColumn();
					
                    final int index = table.getColumnIndex((Column<T, String>)c);
					// sets the index to comparator and order mode
					comparator.setIndex(index);
					comparator.setAscending(info.isAscending());
				} else {
					@SuppressWarnings("unchecked")
					Column<T, String> c = (Column<T, String>) table.getColumn(comparator.getIndex());
					ColumnSortInfo sortInfo =  new ColumnSortInfo(c, comparator.isAscending());
					sortList.push(sortInfo);
				}
				// sorts!
				Collections.sort(list, comparator);

			}
			// gets sublist to show (from sorted list)
			List<T> mix = list.subList(start, end);

			// sets data to table and shows them
			table.setRowData(start, mix);
			updateRowData(start, mix);
			table.setVisibleRange(start, pageSize);
			
			// hide the pager if table has max line per page
			if (pageSize == Integer.MAX_VALUE) {
				pager.setVisible(false);
			}
		}

		/**
		 * Sets the visible range and notify the range to onRangeChange method
		 */
		@Override
		public void updateRowCount(int size, boolean exact) {
			super.updateRowCount(size, exact);
			Range range = table.getVisibleRange();
			table.setVisibleRange(0, range.getLength());
			onRangeChanged(table);
		}		
	}
	

}