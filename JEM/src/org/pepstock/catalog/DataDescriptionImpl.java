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
package org.pepstock.catalog;



/**
 * Is a container of datasets, addressable by name and with the disposition how
 * to access on datasets.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataDescriptionImpl extends AbstractDataDescription<DataSetImpl> {

	private static final long serialVersionUID = 1L;

	private DataDescriptionImpl dataDescriptionReference = null;

	/**
	 * Empty constructor
	 */
	public DataDescriptionImpl() {

	}
	
	/**
	 * This adds a dataset definition of super class.
	 * 
	 * @param ds dataset definition
	 */
	public void addDataSet(DataSetImpl ds) {
		getDatasets().add(ds);
	}

	/**
	 * Returns the reference if exists, otherwise <code>null</code>. A reference
	 * occurs when the same data description is referenced in different steps of
	 * a job
	 * 
	 * @return referenced data description if exists, otherwise
	 *         <code>null</code>
	 */
	public DataDescriptionImpl getDataDescriptionReference() {
		return dataDescriptionReference;
	}

	/**
	 * Sets the reference for data description. A reference occurs when the same
	 * data description is referenced in different steps of a job
	 * 
	 * @param dataDescriptionReference reference data description instance
	 */
	public void setDataDescriptionReference(DataDescriptionImpl dataDescriptionReference) {
		this.dataDescriptionReference = dataDescriptionReference;
	}

	/**
	 * Returns the string representation of data description.
	 * 
	 * @return the string representation of data description
	 */
	@Override
	public String toString() {
		if (isSingleDataset()) {
			// if single, gets it and returns name, disposition and dataset
			// string
			DataSetImpl ds = getDatasets().get(0);
			return "[dd=" + getName() + ", disp=" + getDisposition() + ", " + ds.toString() + "]";
		} else if (!getDatasets().isEmpty()) {
			// if multi, gets it and returns name, disposition and all datasets
			// string
			StringBuilder sb = new StringBuilder("[dd=" + getName() + ", disp=" + getDisposition() + ",\n");
			for (DataSetImpl ds : getDatasets()) {
				sb.append("    ").append(ds.toString()).append(",").append('\n');
			}
			sb.append("]");
			return sb.toString();
		} else if (isSysout()) {
			// if sysout, gets it and returns name, disposition and "SYSOUT"
			// string
			return "[dd=" + getName() + ", disp=" + getDisposition() + ", SYSOUT]";
		}
		// otherwise super.toString
		return super.toString();
	}

}