/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.explorer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.pepstock.jem.GfsFile;
import org.pepstock.jem.plugin.util.Images;
import org.pepstock.jem.plugin.util.TimeDisplayUtils;

/**
 * Provides the labels to use inside the explorer table for each file.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class GfsLabelProvider extends LabelProvider implements ITableLabelProvider {

	private SimpleDateFormat dateFormatter;
	
	private DecimalFormat sizeFormat;

	/**
	 * Sets date and number formatters  
	 */
	public GfsLabelProvider() {
		dateFormatter = new SimpleDateFormat(TimeDisplayUtils.TIMESTAMP_FORMAT);
		sizeFormat = new DecimalFormat("###,###,###,###,###,##0");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
    @Override
    public Image getColumnImage(Object element, int index) {
		if (index == 0) {
			GfsFile file = (GfsFile) element;
			if (file.isDirectory()) {
				return Images.DIRECTORY;
			} else {
				return Images.FILE;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
    @Override
    public String getColumnText(Object element, int index) {
		GfsFile file = (GfsFile) element;
		switch (index) {
		case 0:
			// file name
			return file.getName();
		case 1:
			// no size for directory
			return getTextForSize(file);
		case 2:
			// no modified for directory
			return getTextForLastModified(file);
		case 3:
			// no modified for directory
			return file.getDataPathName();			
		default:
			return null;
		}
    }
    
    /**
     * Returns the size in formatted string
     * @param file file to show
     * @return the size in formatted string
     */
    private String getTextForSize(GfsFile file){
		// no size for directory
		if (file.isDirectory()){
			return null;
		} else {
			// file size
			return sizeFormat.format(file.getLength());
		}
    }
    
    /**
     * Returns the last modified date in formatted string
     * @param file file to show
     * @return the last modified date in formatted string
     */
    private String getTextForLastModified(GfsFile file){
		// no modified for directory
		if (file.isDirectory()){
			return null;
		} else {
			// last modified file
			return dateFormatter.format(file.getLastModified());
		}
    }

}