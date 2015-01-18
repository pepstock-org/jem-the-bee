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
package org.pepstock.jem.junit.init;

import java.util.List;

/**
 * 
 * @author Simone "Busy" Businaro
 *
 */
public class Submitter {

	private String name;

	@SuppressWarnings("rawtypes")
	private Class referenceClass;

	private Boolean selected;
	
	private Boolean embedded = Boolean.TRUE;
	
	private Boolean nodeJs;

	private List<Param> params;

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

	/**
	 * @return the embedded
	 */
	public Boolean getEmbedded() {
		return embedded;
	}

	/**
	 * @param embedded the embedded to set
	 */
	public void setEmbedded(Boolean embedded) {
		this.embedded = embedded;
	}

	/**
	 * @return the clazz
	 */
	@SuppressWarnings("rawtypes")
	public Class getReferenceClass() {
		return referenceClass;
	}

	/**
	 * @param clazz the clazz to set
	 */
	@SuppressWarnings("rawtypes")
	public void setReferenceClass(Class clazz) {
		this.referenceClass = clazz;
	}

	/**
	 * @return the selected
	 */
	public Boolean getSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the nodeJs
	 */
	public Boolean getNodeJs() {
		return nodeJs;
	}

	/**
	 * @param nodeJs the nodeJs to set
	 */
	public void setNodeJs(Boolean nodeJs) {
		this.nodeJs = nodeJs;
	}

	/**
	 * @return the params
	 */
	public List<Param> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(List<Param> params) {
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "Submitter [name=" + name + ", referenceClass=" + referenceClass + ", selected=" + selected + ", embedded=" + embedded + ", params=" + params + "]";
    }
}
