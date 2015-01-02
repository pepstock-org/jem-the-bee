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
package org.pepstock.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract container of fields, addressable by name and with the disposition how
 * to access on datasets.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 * @param <T> Type of datasets
 * 
 */
public abstract class AbstractDataDescription<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;

	private String disposition = Disposition.SHR;

	private boolean sysout = false;

	private List<T> datasets = new ArrayList<T>();

	/**
	 * Empty constructor
	 */
	public AbstractDataDescription() {

	}
	
	/**
	 * Sets a list of dataset definition.
	 * 
	 * @see DataSet
	 * @param datasets list of datasets
	 */
	public void setDatasets(List<T> datasets) {
		this.datasets = datasets;
	}

	/**
	 * Returns the list of datasets.
	 * 
	 * @return the list of datasets
	 */
	public List<T> getDatasets() {
		return datasets;
	}

	/**
	 * Returns the name of data description. This is mandatory value because is
	 * used to access to resources by name.
	 * 
	 * @return the name of data description
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of data description. This is mandatory value because is
	 * used to access to resources by name.
	 * 
	 * @param name the name of data description
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the disposition (how to access to datasets)
	 * 
	 * @see Disposition
	 * @return the disposition (how to access to datasets)
	 */
	public final String getDisposition() {
		return disposition;
	}

	/**
	 * Sets the disposition (how to access to datasets)
	 * 
	 * @see Disposition
	 * @param disposition the disposition (how to access to datasets)
	 */
	public final void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	/**
	 * Return <code>true</code> if the data description is defined with sysout
	 * attribute
	 * 
	 * @return <code>true</code> if the data description is defined with sysout
	 *         attribute
	 */
	public final boolean isSysout() {
		return sysout;
	}

	/**
	 * Return <code>true</code> if the data description is defined with sysout
	 * attribute
	 * 
	 * @param sysout <code>true</code> if the data description is defined with
	 *            sysout attribute
	 */
	public final void setSysout(boolean sysout) {
		this.sysout = sysout;
	}

	/**
	 * Return <code>true</code> if the size of datasets list is equals to 1.
	 * 
	 * @return <code>true</code> if the size of datasets list is equals to 1.
	 */
	public final boolean isSingleDataset() {
		return datasets.size() == 1;
	}

	/**
	 * Return <code>true</code> if the size of datasets list is greater to 1.
	 * 
	 * @return <code>true</code> if the size of datasets list is greater to 1.
	 */
	public final boolean isMultiDataset() {
		return datasets.size() > 1;
	}
	
	/**
	 * Return string representation of a DataDescriptor
	 */
	@Override
	public String toString() {
		if (isSingleDataset()) {
			T ds = getDatasets().get(0);
			return "[dd=" + getName() + ", disp=" + getDisposition() + ", " + ds.toString() + "]";
		} else if (!getDatasets().isEmpty()) {
			StringBuilder sb = new StringBuilder("[dd=" + getName() + ", disp=" + getDisposition() + ",\n");
			for (T ds : getDatasets()) {
				sb.append("    ").append(ds.toString()).append(",").append('\n');
			}
			sb.append("]");
			return sb.toString();
		} else if (isSysout()) {
			return "[dd=" + getName() + ", disp=" + getDisposition() + ", SYSOUT]";

		}
		return super.toString();
	}
}