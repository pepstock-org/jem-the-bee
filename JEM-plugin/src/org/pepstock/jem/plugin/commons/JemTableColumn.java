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
package org.pepstock.jem.plugin.commons;

/**
 * Common table column representation, with name and weight for width.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JemTableColumn {
	
	/**
	 * Default of column weight
	 */
	public static final int DEFAULT_WEIGHT = 100;
	
	private int weight = DEFAULT_WEIGHT;
	
	private String name = null;
	
	/**
	 * Constructs the object using column name
	 * @param name column name
	 */
    public JemTableColumn(String name) {
	    this(name, DEFAULT_WEIGHT);
    }
    

	/**
	 * Constructs the object using column name and width weight
	 * @param name column name
	 * @param weight width weight
	 */
    public JemTableColumn(String name, int weight) {
	    this.weight = weight;
	    this.name = name;
    }
    
	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "JemTableColumn [weight=" + weight + ", name=" + name + "]";
    }

}