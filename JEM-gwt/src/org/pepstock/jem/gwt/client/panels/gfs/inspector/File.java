/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.editor.SyntaxHighlighter;
import org.pepstock.jem.gwt.client.editor.viewers.TextViewer;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component that shows the JCL, using ACE
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class File extends VerticalPanel implements ResizeCapable, SyntaxHighlighter{
	
	private static final String EMPTY = "[empty]";
	
	private TextViewer viewOutput = new TextViewer();

	/**
	 * Browse file content
	 * 
	 * @param contentParm FILE content
	 * 
	 */
	public File(String contentParm) {
		String content = contentParm;
		setSpacing(0);
		if (content == null || content.trim().length() == 0){
			content = EMPTY;
		}
		add(viewOutput);
		viewOutput.setContent(content);
	}
	
	/**
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	viewOutput.onResize(availableWidth, availableHeight);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#startEditor()
	 */
    @Override
    public void startEditor() {
	    viewOutput.startEditor();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#destroyEditor()
	 */
    @Override
    public void destroyEditor() {
	    viewOutput.destroyEditor();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#getContent()
	 */
    @Override
    public String getContent() {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#setContent(java.lang.String)
	 */
    @Override
    public void setContent(String content) {
	  // ignore
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#isChanged()
	 */
    @Override
    public boolean isChanged() {
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#setChanged(boolean)
	 */
    @Override
    public void setChanged(boolean changed) {
    	// ignore
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#isEditorStarted()
	 */
    @Override
    public boolean isEditorStarted() {
	    return false;
    }
}