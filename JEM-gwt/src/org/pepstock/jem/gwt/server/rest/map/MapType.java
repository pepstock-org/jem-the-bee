/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.server.rest.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a {@link MapEntryType} list. To serialize a MAP by REST, a list is necessary,
 * and item of list represent a entry and value of a map
 * 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class MapType {

	/**
	 * To serialize a MAP by REST, a list is necessary,
	 */
	private List<MapEntryType> entry = new ArrayList<MapEntryType>();

	/**
	 * @return a {@link MapEntryType} list
	 */
	public List<MapEntryType> getEntry() {
		return entry;
	}

	/**
	 * Sets the {@link MapEntryType} list
	 * @param entry
	 */
	public void setEntry(List<MapEntryType> entry) {
		this.entry = entry;
	}

}