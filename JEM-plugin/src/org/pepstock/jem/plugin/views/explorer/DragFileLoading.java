/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.views.explorer;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.plugin.util.ShellContainer;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public abstract class DragFileLoading extends FileLoading {
	
	private DragSourceEvent event = null;

	/**
	 * @param shell 
	 * @param event 
	 * @param type
	 * @param file
	 */
	public DragFileLoading(Shell shell, DragSourceEvent event, int type, GfsFile file) {
		super(shell, type, file);
		this.event = event;
	}
	
	/**
	 * @param shellContainer 
	 * @param event 
	 * @param type
	 * @param file
	 */
	public DragFileLoading(ShellContainer shellContainer, DragSourceEvent event, int type, GfsFile file) {
		this(shellContainer.getShell(), event, type, file);
	}

	/**
	 * @return the event
	 */
	public DragSourceEvent getEvent() {
		return event;
	}

}
