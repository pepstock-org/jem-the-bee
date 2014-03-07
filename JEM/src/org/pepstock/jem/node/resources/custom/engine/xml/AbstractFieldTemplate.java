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

import org.pepstock.jem.node.resources.custom.AbstractField;
import org.pepstock.jem.util.Parser;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a generic custom resource field in the resource templates <code>xml</code> file. <br>
 * It contains all of the properties common to all types of fields.
 * @see XStream
 *  
 * @author Alessandro Zambrini
 */
public class AbstractFieldTemplate extends AbstractField  {
	
	/**
	 * Name of the key attribute. 
	 * @see XStream
	 */
	public static final String KEY_ATTRIBUTE = "key";
	
	/**
	 * Name of the label attribute. 
	 * @see XStream
	 */
	public static final String LABEL_ATTRIBUTE = "label";
	
	/**
	 * Name of the description attribute. 
	 * @see XStream
	 */
	public static final String DESCRIPTION_ATTRIBUTE = "description";
	
	/**
	 * Name of the mandatory attribute. 
	 * @see XStream
	 */
	public static final String MANDATORY_ATTRIBUTE = "mandatory";

	/**
	 * Name of the override attribute. 
	 * @see XStream
	 */
	public static final String OVERRIDE_ATTRIBUTE = "override";
	
	/**
	 * Name of the visible attribute. 
	 * @see XStream
	 */
	public static final String VISIBLE_ATTRIBUTE = "visible";
	
	/**
	 * The property that indicates whether the field is visible.
	 */
	private String visible = "true";
	
	/**
	 * The property that indicates whether the field is overridable.
	 */
	private String override = "true";
	
	/**
	 * Returns the property that indicates whether the field is visible.
	 * @return <code>true</code> if this field is visible in the summary, 
	 * <code>false</code> otherwise.
	 */
	public boolean isVisible() {
		return Parser.parseBoolean(this.visible, true);
	}

	/**
	 * Sets if this field is visible or not.
	 * @param visible <code>true</code> if you want this field to be 
	 * visible, <code>false</code> otherwise.
	 */
	public void setVisible(boolean visible) {
		this.override = Boolean.toString(visible);
	}
	
	/**
	 * Returns the property that indicates whether the field is override.
	 * @return <code>true</code> if this field is override, <code>false</code> 
	 * otherwise.
	 */
	public boolean isOverride() {
		return Parser.parseBoolean(this.override, true);
	}

	/**
	 * Sets if this field is override or not.
	 * @param override <code>true</code> if you want this field to be 
	 * override, <code>false</code> otherwise.
	 */
	public void setOverride(boolean override) {
		this.override = Boolean.toString(override);
	}

	/**
	 * 
	 * @param o
	 */
	public void override(String o){
		this.override = o;
	}
}
