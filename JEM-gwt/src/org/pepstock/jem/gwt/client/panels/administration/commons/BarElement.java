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
package org.pepstock.jem.gwt.client.panels.administration.commons;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class BarElement {

	/**
	 * Default bar element, used for the "back" button 
	 */
	public static final BarElement BACK = new BarElement("< Back", -1);
	
	private String label = null;
	
	private int value = 0;

	/**
	 * @param label
	 * @param value
	 */
    public BarElement(String label, int value) {
	    this.label = label;
	    this.value = value;
    }

	/**
	 * 
	 */
    public BarElement() {
    }

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "BarElement [label=" + label + ", value=" + value + "]";
    }

}