/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.ant.tasks.managers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.ant.DataDescriptionStep;

/**
 * Is singleton object. Only one instance of this container must instantiated.<br>
 * Contains all data description defined with the goal to implement the dataset
 * referback feature.
 * 
 * @see org.pepstock.jem.ant.tasks.DataSet#isReference()
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public final class ImplementationsContainer {

	private static final String LEVEL_SEPARATOR = ".";
	
	private static final int LEVEL_WITH_ID = 5;
	
	private static final int LEVEL_WITHOUT_ID = 4;
	
	// format to check the reference: target, task, id and data description
	private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("*.{0}.{1}.{2}.{3}");

	// format used for searching by target, task  and id
	private static final MessageFormat MESSAGE_FORMAT_SEARCH = new MessageFormat("*.{0}.{1}.{2}.");

	private static ImplementationsContainer CONTAINER = null;

	private Map<String, DataDescriptionImpl> mapDataDescription = null;

	/**
	 * Private constructor which screates the map to save the references of data
	 * description
	 */
	private ImplementationsContainer() {
		mapDataDescription = new HashMap<String, DataDescriptionImpl>();
	}
	
	/**
	 * Is singleton static method to retrieve the object. Only one instance of
	 * this container must instantiated.<br>
	 * If static instance is null, creates a new one.
	 * 
	 * @return container instance
	 */
	public static ImplementationsContainer getInstance() {
		if (CONTAINER == null) {
			CONTAINER = new ImplementationsContainer();
		}
		return CONTAINER;
	}

	/**
	 * Returns <code>true</code> if container contains the reference, otherwise
	 * <code>false</code>.
	 * 
	 * @see org.pepstock.jem.ant.tasks.DataSet#isReference()
	 * @param reference string reference representation
	 * @return <code>true</code> if container contains the reference
	 */
	public boolean hasDataDescription(String reference) {
		String ref = normalizeReference(reference);
		return (ref != null) ? mapDataDescription.containsKey(ref.toLowerCase()) : false;	
	}

	/**
	 * Returns data description found by reference, otherwise <code>null</code>.
	 * 
	 * @param reference reference string representation
	 * @return data description instance
	 */
	public DataDescriptionImpl getDataDescription(String reference) {
		String ref = normalizeReference(reference);
		return (ref != null) ? mapDataDescription.get(ref.toLowerCase()) : null;	
	}

	/**
	 * Returns the list of data description instances defined for passed Task.
	 * 
	 * @param item ANT task instance
	 * @return list of data description instances
	 */
	public List<DataDescriptionImpl> getDataDescriptionsByItem(DataDescriptionStep item) {
		// creates a new list
		List<DataDescriptionImpl> result = new ArrayList<DataDescriptionImpl>();
		// creates a key usinf format for searching (without data description
		// name)
		String keyPattern = createKey(item);
		// scans all keys of map
		for (String key : mapDataDescription.keySet()) {
			// when the key starts with key pattern, matches!!
			// So adds to list to return
			if (key.startsWith(keyPattern)) {
				result.add(mapDataDescription.get(key));
			}

		}
		return result;
	}

	/**
	 * Adds new data description implementation, defined for passed target and
	 * task.
	 * 
	 * @param item task in executing
	 * @param task task name
	 * @param dd data description implementation
	 */
	public void addDataDescription(DataDescriptionStep item, DataDescriptionImpl dd) {
		// create a key using message format defined for reference
		String key = createKey(item, dd.getName());
		mapDataDescription.put(key, dd);
	}

	/**
	 * Creates a key using the format defined.
	 * 
	 * @see ImplementationsContainer#MESSAGE_FORMAT
	 * @param item task executing
	 * @param ddname data description name
	 * @return the key of map (always lower-case)
	 */
	private String createKey(DataDescriptionStep item, String ddname) {
		String key = MESSAGE_FORMAT.format(new Object[] { item.getTargetName(), item.getTaskName(), item.getId(), ddname }, new StringBuffer(), null).toString();
		return key.toLowerCase();
	}

	/**
	 * Creates a key using the format defined for searching, without data
	 * description name.
	 * 
	 * @see ImplementationsContainer#MESSAGE_FORMAT
	 * @param target target name
	 * @param task task name
	 * @return the key of map (always lower-case)
	 */
	private String createKey(DataDescriptionStep item) {
		String key = MESSAGE_FORMAT_SEARCH.format(new Object[] { item.getTargetName(), item.getTaskName(), item.getId() }, new StringBuffer(), null).toString();
		return key.toLowerCase();
	}
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	private String normalizeReference(String reference){
		String[] levels = StringUtils.split(reference, LEVEL_SEPARATOR);
		if (levels != null){
			if (levels.length == LEVEL_WITH_ID){
				return reference;
			} else if (levels.length == LEVEL_WITHOUT_ID){
				String referenceNew = levels[0] + LEVEL_SEPARATOR + // *
						levels[1] + LEVEL_SEPARATOR + // target
						levels[2] + LEVEL_SEPARATOR + // task
						DataDescriptionStep.DEFAULT_ID + LEVEL_SEPARATOR + // id
						levels[3]; //dd
				return referenceNew;		
			}
		}
		return null;
	}

	/**
	 * Returns the string representation of data description container (uses
	 * HaspMap to string method).
	 * 
	 * @see java.util.HashMap#toString()
	 * @return the string representation of data description container
	 */
	@Override
	public String toString() {
		return mapDataDescription.toString();
	}

}