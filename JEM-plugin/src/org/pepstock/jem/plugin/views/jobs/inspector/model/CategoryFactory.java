/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Enrico Frigo - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.inspector.model;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.plugin.util.Images;
/**
 * Creates the complete tree of output directory of a job.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class CategoryFactory {

	/**
	 * Job general information label
	 */
	public static final String JOB_INFORMATION_NAME = "Job information";
	
	/**
	 * JCL label
	 */
	public static final String JOB_JCL_NAME = "JCL";
	
	/**
	 * Output folder label
	 */
	public static final String JOB_OUTPUT_NAME = "Output";

	/**
	 * Private constructor to avoid new instantiations 
	 */
	private CategoryFactory() {
	}

    /**
     * Starting from output tree object, is able to creates a tree with all necessary object to represent output directory by a tree.
     * 
     * @param outputTree tree of output directory
     * @return a list of categories
     */
	public static List<Category> getCategories(OutputTree outputTree) {
		List<Category> categories = new LinkedList<Category>();
		
		// general job info
		Category category = new Category(JOB_INFORMATION_NAME);
		category.setImage(Images.GENERAL);
		categories.add(category);

		// JCL
		category = new Category(JOB_JCL_NAME);
		category.setImage(Images.JCL);
		categories.add(category);
		
		// for ROUTING and INPUT, the levelItems are empty 
		if (!outputTree.getFirstLevelItems().isEmpty()){
			category = new Category(JOB_OUTPUT_NAME);
			category.setImage(Images.DIRECTORY);
			categories.add(category);
			// scans first level
			for(int i=0;outputTree.getFirstLevelItems()!=null && i<outputTree.getFirstLevelItems().size();i++){
				ProducedOutput out = new ProducedOutput(outputTree.getFirstLevelItems().get(i).getLabel(),outputTree.getFirstLevelItems().get(i).getFileRelativePath());
				out.setOutItem(outputTree.getFirstLevelItems().get(i));
				out.setImage(Images.FILE);
				category.getProducedOutputs().add(out);
			}
			// scans second level
			
			for (List<OutputListItem> items : outputTree.getSecondLevelItems()){
				if (!items.isEmpty()){
					String key = items.get(0).getParent();
					Category subCategory = new Category(key);
					subCategory.setImage(Images.DIRECTORY);
					for (OutputListItem item : items) {
						ProducedOutput out = new ProducedOutput(item.getLabel(), item.getFileRelativePath());
						out.setOutItem(item);
						out.setImage(Images.FILE);
						subCategory.getProducedOutputs().add(out);
					}
					category.getSubCategories().add(subCategory);
				}
			}
		}
		return categories;
	}

}
