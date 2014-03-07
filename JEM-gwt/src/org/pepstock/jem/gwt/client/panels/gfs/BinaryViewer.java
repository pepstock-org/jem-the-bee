/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels.gfs;

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.panels.gfs.commons.FileSystemPanel;
import org.pepstock.jem.gwt.client.panels.gfs.commons.FilesComparator;
import org.pepstock.jem.gwt.client.panels.gfs.commons.FilesTable;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class BinaryViewer extends FileSystemPanel{
	
	private BinaryFilesTable table = null;

	/**
	 * 
	 */
	public BinaryViewer() {
		super(PreferencesKeys.EXPLORER_SEARCH_BINARY);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.gfs.commons.FileSystemPanel#getFilesTable()
	 */
    @Override
    public FilesTable getFilesTable() {
    	if (table == null){
    		table = new BinaryFilesTable();
    	}
	    return table;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.gfs.commons.FileSystemPanel#getFilesType()
	 */
    @Override
    public int getFilesType() {
	    return GfsFile.BINARY;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.gfs.commons.FileSystemPanel#isReadOnly()
	 */
    @Override
    public boolean isOverviewOnly() {
	    return true;
    }
    
    static class BinaryFilesTable extends FilesTable{

		/* (non-Javadoc)
		 * @see org.pepstock.jem.gwt.client.panels.gfs.commons.FilesTable#isOverviewOnly()
		 */
        @Override
        public boolean isOverviewOnly() {
	        return true;
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.gwt.client.panels.gfs.commons.FilesTable#getIndexedColumnComparator()
		 */
        @Override
        public IndexedColumnComparator<GfsFile> getIndexedColumnComparator() {
	        return new FilesComparator(0, PreferencesKeys.EXPLORER_SORT_BINARY);
        }
    	
    }

}