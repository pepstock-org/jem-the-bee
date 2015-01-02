/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.inspector.model;

import org.eclipse.swt.graphics.Image;
import org.pepstock.jem.OutputListItem;

/**
 * Represents a file in output folder of a job.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class ProducedOutput{

	private String name = null;
	
	private String description = null;
	
	private Image image = null;
	
	private OutputListItem outItem;
	
	/**
	 * Creates object using the name and its description
	 * @param name name of file
	 * @param description description of file
	 */
    public ProducedOutput(String name, String description) {
	    super();
	    this.name = name;
	    this.description = description;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the outItem
	 */
	public OutputListItem getOutItem() {
		return outItem;
	}

	/**
	 * @param outItem the outItem to set
	 */
	public void setOutItem(OutputListItem outItem) {
		this.outItem = outItem;
	}


}
