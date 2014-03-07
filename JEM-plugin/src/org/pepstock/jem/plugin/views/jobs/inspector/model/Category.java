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
package org.pepstock.jem.plugin.views.jobs.inspector.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/**
 * Entity which represents a folder inside of output directory of a job
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Category {
	
	private String name;
	
	private Image image = null;
	
	private List<ProducedOutput> producedOutputs = new ArrayList<ProducedOutput>();
	
	private List<Category> subCategories = new ArrayList<Category>();
	

	/**
	 * Creates object using the name of folder
	 * @param name the name of folder
	 */
    public Category(String name) {
	    super();
	    this.name = name;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
	 * Returns the list of output files
	 * @return the list of output files
	 */
	public List<ProducedOutput> getProducedOutputs() {
		return producedOutputs;
	}

	/**
	 * Returns the list of sub-categories
	 * @return the list of sub-categories
	 */
	public List<Category> getSubCategories() {
		return subCategories;
	}
	
}
