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
package org.pepstock.jem.jbpm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Is the collections of all inforation passsed in JBPM file related to IO specification of BPMN2 language.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class IoSpecification {
	
	private final Map<String, DataInputAssociation> associations = new HashMap<String, DataInputAssociation>();
	
	private final List<DataInput> dataInputs= new ArrayList<DataInput>();

	/**
	 * @return the dataInputs
	 */
	public List<DataInput> getDataInputs() {
		return dataInputs;
	}

	/**
	 * @return the associations
	 */
	public Map<String, DataInputAssociation> getAssociations() {
		return associations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "IoSpecification [associations=" + associations + ", dataInputs=" + dataInputs + "]";
    }
}