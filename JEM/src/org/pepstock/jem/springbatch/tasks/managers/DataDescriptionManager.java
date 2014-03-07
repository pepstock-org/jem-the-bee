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
package org.pepstock.jem.springbatch.tasks.managers;

import java.io.File;
import java.io.IOException;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSet;

/**
 * Creates data description implementation from a data description.<br>
 * Creates all data set implementation calling DataSetManager.
 * 
 * @author Andrea "Stock" Stocchero
 * @see DataSetManager
 */
public class DataDescriptionManager {

	/**
	 * To avoid any instantiation
	 */
	private DataDescriptionManager() {
		
	}

	/**
	 * Creates a data description implementation from a data description, loaded
	 * by SpringBatch.<br>
	 * Checks different kinds of data description that you could have, so
	 * sysout, single dataset or multi datasets.<br>
	 * 
	 * @param dd data description instance, created by SpringBatch reading JCL
	 * @param stepName step name
	 * @return data set implementation, to use for GRS
	 * @throws SpringBatchException if errors occur
	 */
	public static DataDescriptionImpl createDataDescriptionImpl(DataDescription dd, String stepName) throws SpringBatchException {
		if (dd.getName() == null){
			throw new SpringBatchException(SpringBatchMessage.JEMS003E);
		}
		
		// creates a dataDescription implementation
		DataDescriptionImpl ddImpl = new DataDescriptionImpl();
		ddImpl.setName(dd.getName());
		ddImpl.setDisposition(dd.getDisposition());

		try {
			// check if is a sysout
			if (dd.isSysout()) {
				loadSysout(ddImpl, stepName);
			} else if (dd.isSingleDataset()) {
				// check if is a single data-set representation
				loadSingleDataset(ddImpl, dd, stepName);
			} else if (dd.isMultiDataset()) {
				// check if is a multi data-sets representation
				loadMultiDataset(ddImpl, dd, stepName);
			}
			return ddImpl;
		} catch (IOException e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS038E, e);
		}
	}

	/**
	 * Creates a sysout data description. A sysout is a folder inside of folder
	 * for the job, folder uses to save standard output and error, jcl and
	 * commomn report of steps execution.<br>
	 * 
	 * @param dd data description instance, created by SpringBatch reading JCL
	 * @param stepName step name
	 * @throws SpringBatchException if errors occur
	 */
	private static void loadSysout(DataDescriptionImpl ddImpl, String stepName) throws SpringBatchException {
		// set flag to true
		ddImpl.setSysout(true);

		// extract the real path, using a env variable to save the sysout file.
		// If null, exception occurs
		String path = System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME);
		if (path == null) {
			throw new SpringBatchException(SpringBatchMessage.JEMS005E);
		}

		// create the directory object, if doesn't exist
		File dir = new File(path, stepName);
		if (!dir.exists()) {
			boolean isCreated = dir.mkdir();
			if (!isCreated){
				throw new SpringBatchException(NodeMessage.JEMC153E, dir.getAbsolutePath());
			}
		}
		// the name of sysout file is stepname and data description name. Suffix
		// is .log
		File file = new File(dir, stepName + "-" + ddImpl.getName() + ".log");

		// create the dataset object with the name stepname and data description
		// name
		DataSet ds = new DataSet();
		ds.setName(stepName + "-" + ddImpl.getName());

		// get data set impl already created in all Dataset and load with all
		// information
		DataSetImpl dataset = ds.getDataSetImpl();
		// the dataset name is file name
		dataset.setName(file.getName());
		dataset.setType(DataSetType.SYSOUT);
		// set file. The real name is the same
		dataset.setFile(file);

		// dataset is loaded to data description impl
		ddImpl.addDataSet(dataset);
		
		ImplementationsContainer.getInstance().addDataDescription(stepName, ddImpl);
	}

	/**
	 * Creates a data description implementation with a single dataset.
	 * 
	 * @param ddImpl data description implementation
	 * @param dd data description instance, created by SpringBatch reading JCL
	 * @param stepName step name
	 * @throws IOException if errors occur
	 * @throws SpringBatchException if errors occur
	 */
	private static void loadSingleDataset(DataDescriptionImpl ddImpl, DataDescription dd, String stepName) throws SpringBatchException, IOException {
		// get the first from the list. being a single dataset, the size of list
		// must be 1
		DataSet ds = dd.getDatasets().get(0);
		// create dataset using datasetmanager
		DataSetManager.createDataSetImpl(ddImpl, ds);
		
		ImplementationsContainer.getInstance().addDataDescription(stepName, ddImpl);
	}

	/**
	 * Creates a data description implementation with many dataset.<br>
	 * Works only if the data description is defined in SHR mode (for reading),
	 * otherwise a exception occurs.<br>
	 * 
	 * @param ddImpl data description implementation
	 * @param dd data description instance, created by SpringBatch reading JCL
	 * @param stepName step name
	 * @throws IOException  if errors occur
	 * @throws SpringBatchException if errors occur
	 */
	private static void loadMultiDataset(DataDescriptionImpl ddImpl, DataDescription dd, String stepName) throws SpringBatchException, IOException {
		// being many datasets, you can only use to read them, not to write
		if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR)){
			throw new SpringBatchException(SpringBatchMessage.JEMS005E, ddImpl.getName(),ddImpl.getDisposition());
		}

		// creates all datasets
		for (DataSet ds : dd.getDatasets()) {
			DataSetManager.createDataSetImpl(ddImpl, ds);
		}
		
		ImplementationsContainer.getInstance().addDataDescription(stepName, ddImpl);
	}

}