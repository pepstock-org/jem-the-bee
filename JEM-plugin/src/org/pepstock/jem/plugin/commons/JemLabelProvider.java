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
package org.pepstock.jem.plugin.commons;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider of table viewer, both for texts and for images.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * @param <T> 
 */
public abstract class JemLabelProvider<T> extends LabelProvider implements ITableLabelProvider {

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object object, int index) {
		@SuppressWarnings("unchecked")
		T element = (T) object;
		return getImageColumn(element, index);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
    @Override
    public String getColumnText(Object object, int index) {
		@SuppressWarnings("unchecked")
		T element = (T) object;
		return getTextColumn(element, index);
    }

    /**
     * Returns the label text, by object (for row) and column index
     * @param element object which is represented by row
     * @param index index of column
     * @return the label text
     */
	public abstract String getTextColumn(T element, int index);

	/**
     * Returns the image, by object (for row) and column index
     * @param element object which is represented by row
     * @param index index of column
     * @return the image
	 */
	public abstract Image getImageColumn(T element, int index);
	
}