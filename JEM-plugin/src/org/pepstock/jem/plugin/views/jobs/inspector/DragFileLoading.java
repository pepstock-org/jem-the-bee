/*******************************************************************************
 * Copyright (c) 2012-2014 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.inspector;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.views.jobs.inspector.model.Category;
import org.pepstock.jem.plugin.views.jobs.inspector.model.ProducedOutput;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class DragFileLoading extends FileLoading {
	
	private DragSourceEvent event = null;

	/**
	 * @param shell 
	 * @param event 
	 * @param category
	 */
	public DragFileLoading(Shell shell, DragSourceEvent event, Category category) {
		super(shell, category);
		this.event = event;
	}

	/**
	 * @param shell 
	 * @param event 
	 * @param output
	 */
	public DragFileLoading(Shell shell, DragSourceEvent event, ProducedOutput output) {
		super(shell, output);
		this.event = event;
	}

	/**
	 * @return the event
	 */
	public DragSourceEvent getEvent() {
		return event;
	}
}
