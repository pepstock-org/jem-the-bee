/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.resources.custom.engine.xml;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a possible value of a field in the resource templates <code>xml</code> file.
 * @see XStream

 * @author Alessandro Zambrini
 */
public class ValueTemplate implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "value";
	
	/**
	 * Name of the selected attribute. 
	 * @see XStream
	 */
	public static final String SELECTED_ATTRIBUTE = "selected";
	
	/**
	 * The property that indicates whether the value must be already selected or not.
	 */
	private boolean selected = false;

	/**
	 * The effective value of this value.
	 */
	private String content = null;

	/**
	 * Returns the property that indicates whether this value must be already selected.
	 * @return <code>true</code> if this value needs to be already selected, <code>false</code> 
	 * otherwise.
	 */	
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets if this value must be already selected or not.
	 * @param selected <code>true</code> if you want this field to be 
	 * already selected, <code>false</code> otherwise.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Returns the effective value of this value.
	 * @return the effective value of this value.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the effective value of this value.
	 * @param content the effective value of this value.
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
}
