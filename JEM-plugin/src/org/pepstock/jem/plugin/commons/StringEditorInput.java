/*******************************************************************************
 * Copyright (c) 2012-2013 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.commons;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.input.ReaderInputStream;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
/**
 * Is Eclipse EDITOR based on string (file content) and filename.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class StringEditorInput implements IStorageEditorInput {
	
	private static final String DEFAULT_TOOLTIP_TEXT = "";

	private String inputString;
	
	private String fileName;

	/**
	 * Constructs object saving content and file name instances
	 * @param inputString content of file
	 * @param fileName file name
	 */
	public StringEditorInput(String inputString, String fileName) {
		this.inputString = inputString;
		this.fileName = fileName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
    @Override
    public boolean exists() {
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
    @Override
    public ImageDescriptor getImageDescriptor() {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
    @Override
    public String getName() {
	    return fileName;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
    @Override
    public IPersistableElement getPersistable() {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
    @Override
    public String getToolTipText() {
	    return DEFAULT_TOOLTIP_TEXT;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class arg0) {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStorageEditorInput#getStorage()
	 */
    @Override
    public IStorage getStorage() throws CoreException {
    	return new MyStorage();
    }

    /**
     * Eclipse storage which represents file content
     * 
     * @author Andrea "Stock" Stocchero
     * @version 1.4
     */
	private final class MyStorage implements IStorage {

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
        @SuppressWarnings("rawtypes")
        @Override
        public Object getAdapter(Class arg0) {
	        return null;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IStorage#getContents()
		 */
        @Override
        public InputStream getContents() throws CoreException {
        	return new ReaderInputStream(new StringReader(inputString));
        }

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IStorage#getFullPath()
		 */
        @Override
        public IPath getFullPath() {
	        return null;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IStorage#getName()
		 */
        @Override
        public String getName() {
	        return StringEditorInput.this.getName();
        }

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IStorage#isReadOnly()
		 */
        @Override
        public boolean isReadOnly() {
	        return false;
        }

	}
}
