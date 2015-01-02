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
package org.pepstock.jem.gwt.client.panels.gfs.inspector;

import org.pepstock.jem.gwt.client.commons.AbstractInspector;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;

/**
 * Component which shows content of a file. Can be called to see a file.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class FileInspector extends AbstractInspector {
	
	private String fileName = null;
	
	private File file = null;
	
	/**
	 * Construct the UI with content file
	 * 
	 * @param fileName file name
	 * @param content of file
	 */
	public FileInspector(String fileName, String content) {
		this.fileName = fileName;
		this.file = new File(content);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
	    return new  FileHeader(fileName, this);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getCenter()
	 */
    @Override
    public Panel getContent() {
	    return file;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getActions()
	 */
    @Override
    public Panel getActions() {
	    return null;
    }

    @Override
    public void show() {
    	super.show();
    	file.startEditor();
    }
	
	@Override
	public void hide(){
		// overrides method to destroy editor
		super.hide();
		if (file != null){
			file.destroyEditor();
		}
	}
	
	@Override
	public void hide(boolean autoClose){
		// overrides method to destroy editor
		super.hide(autoClose);
		if (file != null){
			file.destroyEditor();
		}
	}

}