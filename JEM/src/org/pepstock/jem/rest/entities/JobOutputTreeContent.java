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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.rest.maps.OutputListItemMapAdapter;


/**
 * Represents folder content of JOB and JCL content.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement
public class JobOutputTreeContent extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;

	private Job job = null;
	
	private String jclContent = null;

	private List<OutputListItem> firstLevelItems = new LinkedList<OutputListItem>();

	// PAY ATTENTION: HashMap are not supported by REST. For this reason there is a specific adapter
	@XmlJavaTypeAdapter(OutputListItemMapAdapter.class)
	private Map<String, List<OutputListItem>> secondLevelItems = new LinkedHashMap<String, List<OutputListItem>>();

	/**
	 * Empty constructor
	 */
	public JobOutputTreeContent() {
		
	}
	/**
	 * Returns job instance
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * Sets job instance
	 * @param job the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
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
	 * @param jclContent the jclContent to set
	 */
	public void setJclContent(String jclContent) {
		this.jclContent = jclContent;
	}
	/**
	 * Returns list of files of first level
	 * @return the firstLevelItems
	 */
	public List<OutputListItem> getFirstLevelItems() {
		return firstLevelItems;
	}
	/**
	 * Sets list of files of first level
	 * @param firstLevelItems the firstLevelItems to set
	 */
	public void setFirstLevelItems(List<OutputListItem> firstLevelItems) {
		this.firstLevelItems = firstLevelItems;
	}
	
	/**
	 * Sets list of files of second level
	 * @param secondLevelItems the secondLevelItems to set
	 */
	public void setSecondLevelItems(Map<String, List<OutputListItem>> secondLevelItems) {
		this.secondLevelItems = secondLevelItems;
	}
	
	/**
	 * Returns list of files of second level
	 * @return the secondLevelItems
	 */
	public Map<String, List<OutputListItem>> getSecondLevelItems() {
		return secondLevelItems;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "JobOutputTreeContent [job=" + job + ", jclContent=" + jclContent + ", firstLevelItems=" + firstLevelItems + ", secondLevelItems=" + secondLevelItems + "]";
    }

}