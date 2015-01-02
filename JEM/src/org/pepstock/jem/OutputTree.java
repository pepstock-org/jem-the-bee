/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the tree of job output. It has 2 level:<br>
 * <ul>
 * <li>first level contains <code>job.log</code> and <code>message.log</code>, always the same for all jobs</li>
 * <li>second level contains a arraylist of folders and their contents which are created for every step which creates a sysout in a datadescriptor</li>
 * </ul>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
@XmlRootElement
public class OutputTree implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String jclContent = null;

	private List<OutputListItem> firstLevelItems = new LinkedList<OutputListItem>();

	private List<List<OutputListItem>> secondLevelItems = new LinkedList<List<OutputListItem>>();
	
	/**
	 * Empty constructor
	 */
	public OutputTree() {
	}
	
	/**
	 * Returns JCL content
	 * @return the jclContent
	 */
	public String getJclContent() {
		return jclContent;
	}

	/**
	 * Sets JCL content
	 * 
	 * @param jclContent the jclContent to set
	 */
	public void setJclContent(String jclContent) {
		this.jclContent = jclContent;
	}

	/**
	 * Returns the first level file names
	 * 
	 * @return the firstLevelItems
	 */
	public List<OutputListItem> getFirstLevelItems() {
		return firstLevelItems;
	}
	/**
	 * Sets the first level file names
	 * 
	 * @param firstLevelItems the firstLevelItems to set
	 */
	public void setFirstLevelItems(List<OutputListItem> firstLevelItems) {
		this.firstLevelItems = firstLevelItems;
	}
	/**
	 * Returns the second level file names
	 * 
	 * @return the secondLevelItems
	 */
	public List<List<OutputListItem>> getSecondLevelItems() {
		return secondLevelItems;
	}
	/**
	 * Sets the second level file names
	 * 
	 * @param secondLevelItems the secondLevelItems to set
	 */
	public void setSecondLevelItems(List<List<OutputListItem>> secondLevelItems) {
		this.secondLevelItems = secondLevelItems;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OutputTree [firstLevelItems=" + firstLevelItems + ", secondLevelItems=" + secondLevelItems + "]";
	}
	
}