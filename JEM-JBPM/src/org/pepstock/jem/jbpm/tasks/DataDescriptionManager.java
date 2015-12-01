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
package org.pepstock.jem.jbpm.tasks;

import java.io.File;
import java.io.IOException;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.jbpm.JBpmException;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.Task;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * Creates data description implementation from a data description.<br>
 * Creates all data set implementation calling DataSetManager.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class DataDescriptionManager {
	
	/**
	 * To avoid any instantiation
	 */
	private DataDescriptionManager() {
		
	}

	/**
	 * Creates a data description implementation from a data description, loaded
	 * by JBpm.<br>
	 * Checks different kinds of data description that you could have, so
	 * sysout, single dataset or multi dataset.<br>
	 * 
	 * @see org.pepstock.catalog.DataDescriptionImpl
	 * @param dd data description instance
	 * @param item JBpm task instance, caller
	 * @return data description implementation
	 * @throws JBpmException if any error occurs
	 * @throws IOException if I/O error occurs
	 */
	public static DataDescriptionImpl createDataDescriptionImpl(DataDescription dd, Task item) throws JBpmException, IOException {
		if (dd.getName() == null){
			throw new JBpmException(JBpmMessage.JEMM005E);
		}
		
		// creates a data description impl to return
		DataDescriptionImpl ddImpl = new DataDescriptionImpl();

		// sets name and disposition
		ddImpl.setName(dd.getName());
		ddImpl.setDisposition(dd.getDisposition());

		// checks type of data description
		if (dd.isSysout()) {
			// if sysout creates a sysout
			loadSysout(ddImpl, item);
		} else if (dd.isSingleDataset()) {
			// if single dataset creates load a dataset
			loadSingleDataset(ddImpl, dd, item);
		} else if (dd.isMultiDataset()) {
			// if multi datasets load multi datasets
			loadMultiDataset(ddImpl, dd, item);
		}
		return ddImpl;
	}

	/**
	 * Loads all info to have a SYSOUT data description.<br>
	 * Uses the <code>output</code> path to create a folder with target name
	 * and, for each task, a file with naem
	 * <code>[task-ID]-[data-description-name]</code><br>
	 * Is always defined in output mode.
	 * 
	 * @param ddImpl data description impl instance
	 * @param item JBpm task instance, caller
	 * @throws JBpmException if output path is null
	 */
	private static void loadSysout(DataDescriptionImpl ddImpl, Task item) throws JBpmException {
		// set flag to true
		ddImpl.setSysout(true);

		// extract the real path, using a env variable to save the sysout file.
		// If null, exception occurs
		String path = System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME);
		if (path == null) {
			throw new JBpmException(JBpmMessage.JEMM007E);
		}

		// create the directory object using the target name, if doesn't exist
		File dir = new File(path, item.getName()+item.getId());
		if (!dir.exists()) {
			boolean isCreated = dir.mkdir();
			if (!isCreated){
				throw new JBpmException(NodeMessage.JEMC153E, dir.getAbsolutePath());
			}
		}

		// the name of sysout file is taskname and data description name. Suffix
		// is .log
		File file = new File(dir, item.getId() + "-" + ddImpl.getName() + ".log");

		// create the dataset object with the name taskname and data description
		// name
		DataSet ds = new DataSet();
		ds.setName(item.getId() + "-" + ddImpl.getName());

		// creates a new data set impl
		// load with all information
		DataSetImpl dataset = new DataSetImpl();
		// the dataset name is file name
		dataset.setName(file.getName());
		dataset.setType(DataSetType.SYSOUT);
		// set file. The real name is the same
		dataset.setFile(file);
		// dataset is loaded to data description impl
		ddImpl.addDataSet(dataset);

		// add dataset description to container for further referback
		ImplementationsContainer.getInstance().addDataDescription(item, ddImpl);
	}

	/**
	 * Creates a data description implementation with a single dataset.
	 * 
	 * @param ddImpl data description implementation
	 * @param dd data description instance
	 * @param item JBPM task, caller
	 * @throws JBpmException if errors occurs
	 * @throws IOException if I/O occurs
	 */
	private static void loadSingleDataset(DataDescriptionImpl ddImpl, DataDescription dd, Task item) throws JBpmException, IOException {
		// get the first from the list. being a single dataset, the size of list
		// must be 1
		DataSet ds = dd.getDatasets().get(0);

		// checks type
		if (ds.isReference()) {
			// if reference creates a reference dataset
			DataSetManager.loadReferences(ddImpl, ds);
		} else {
			// creates a dataset
			DataSetManager.createDataSetImpl(ddImpl, ds);
		}

		// add dataset description to container for further referback
		ImplementationsContainer.getInstance().addDataDescription(item, ddImpl);

	}
	
	/**
	 * Creates a data description implementation with many dataset.<br>
	 * Works only if the data description is defined in SHR mode (for reading),
	 * otherwise a exception occurs.<br>
	 * 
	 * @param ddImpl data description implementation
	 * @param dd data description instance
	 * @param item JBPM task, caller
	 * @throws JBpmException if errors occurs
	 * @throws IOException if I/O occurs
	 */
	private static void loadMultiDataset(DataDescriptionImpl ddImpl, DataDescription dd, Task item) throws JBpmException, IOException {
		// being many datasets, you can only use to read them, not to write
		if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR)){
			throw new JBpmException(JBpmMessage.JEMM008E, dd.getName(), dd.getDisposition());
		}

		// creates all datasets
		for (DataSet ds : dd.getDatasets()) {
			// checks type
			if (ds.isReference()) {
				// if reference creates a reference dataset
				DataSetManager.loadReferences(ddImpl, ds);
			} else {
				// creates a dataset
				DataSetManager.createDataSetImpl(ddImpl, ds);
			}
		}
		// add dataset description to container for further referback
		ImplementationsContainer.getInstance().addDataDescription(item, ddImpl);
	}
}