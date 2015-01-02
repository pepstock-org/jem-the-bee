/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Header;

/**
 * A table {@link Header} with an associated filter name
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <H>
 */
public abstract class FilterableHeader<H> extends Header<H> {

	private String filterName;
	
	/**
	 * Builds the header
	 * @param cell the {@link Cell} used to render the header
	 * @param filterName the associated filter name
	 */
	public FilterableHeader(Cell<H> cell, String filterName) {
		super(cell);
		setFilterName(filterName);
	}

	/**
	 * Returns filter name 
	 * @return the filter name
	 */
	public String getFilterName() {
		return filterName;
	}
	
	/**
	 * Sets filter name
	 * @param filterNameParm the associated filter name
	 */
	protected final void setFilterName(String filterNameParm) {
		String newFilterName = filterNameParm;
		newFilterName = newFilterName.trim();
		if (newFilterName.contains(" ")) {
			throw new IllegalArgumentException("filterName does not support spaces!");
		}
		this.filterName = newFilterName;

	}

}