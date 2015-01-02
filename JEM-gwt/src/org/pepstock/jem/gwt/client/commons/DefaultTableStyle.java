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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.CellTable;

/**
 * Style interface for {@link CellTable.Resources} 
 * @author Marco "Fuzzo" Cuccato
 */
public interface DefaultTableStyle extends CellTable.Resources {

	@Override
	@Source({CellTable.Style.DEFAULT_CSS, "../resources/css/DefaultTable.css"})
	TableStyle cellTableStyle();
	
	/**
	 * Needed nested interface
	 */
	interface TableStyle extends CellTable.Style {
	}
	
    @Source("../resources/images/defaulttable/sortAscending.png")
    @ImageOptions(flipRtl = true)
    ImageResource cellTableSortAscending();

    @Source("../resources/images/defaulttable/sortDescending.png")
    @ImageOptions(flipRtl = true)
    ImageResource cellTableSortDescending();
	
}