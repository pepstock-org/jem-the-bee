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
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.DataSet;
import org.pepstock.jem.util.CharSet;

/**
 * Creates all data set implementation called by DataDescriptionManager.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DataSetManager {

	/**
	 * Constant for INLINE file name prefix, used when a dataset contains text
	 * defined inside JCL.
	 */
	public static final String INLINE_FILE_NAME_PREFIX = "step-inline";

	/**
	 * To avoid any instantiation
	 */
	private DataSetManager() {
		
	}

	/**
	 * Gets the dataset implementation and loads it with all information for
	 * dataset implementation (necessary for GRS), depending on different type
	 * of dataset.<br>
	 * Checks also if the definitions are correct (see wrong disposition for
	 * specific dataset type).
	 * 
	 * @param ddImpl data description implementation
	 * @param ds dataset instance
	 * @throws SpringBatchException occurs if errors
	 * @throws IOException occurs if errors
	 */
	public static void createDataSetImpl(DataDescriptionImpl ddImpl, DataSet ds) throws SpringBatchException, IOException {
		DataSetImpl dataset = ds.getDataSetImpl();

		if (ds.isTemporary()) {
			// if temporary dataset, disposition MUST be "NEW"), otherwise
			// exception
			if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.NEW)){
				throw new SpringBatchException(SpringBatchMessage.JEMS007E, ddImpl.getName(), ddImpl.getDisposition(), ds.toString());
			}
			createDataSetImpl(ds);
		} else if (ds.isInline()) {
			// is inline dataset, no actions, loads it
			createDataSetImpl(ds);
		} else if (ds.isGdg()) {
			if (ds.getName() == null){
				throw new SpringBatchException(SpringBatchMessage.JEMS008E, ddImpl.getName(), ddImpl.getDisposition());
			}
			// is gdg dataset, no actions, loads it
			createDataSetImpl(ds);
		} else if (ds.isDefinedDatasource()) {
			if (ds.getName() == null){
				throw new SpringBatchException(SpringBatchMessage.JEMS008E, ddImpl.getName(), ddImpl.getDisposition());
			}

			// is datasource dataset, no actions, loads it
			createDataSetImpl(ds);
		} else {
			if (ds.getName() == null){
				throw new SpringBatchException(SpringBatchMessage.JEMS008E, ddImpl.getName(), ddImpl.getDisposition());
			}

			// is normal dataset, load datasetimpl checks if file exists
			createDataSetImpl(ds);

			// gets file from dataset
			File file = dataset.getFile();

			if (ddImpl.getDisposition().equalsIgnoreCase(Disposition.NEW)) {
				// if the file is defined as NEW, it don't must exist, otherwise
				// exception
				if (file.exists()){
					throw new SpringBatchException(SpringBatchMessage.JEMS009E, ddImpl.getName(), ddImpl.getDisposition(), ds.toString());
				}
				
				// file name could have a new dirctories. Create them here
				File parent = file.getParentFile();
				if (!parent.exists()){
					boolean isCreated = parent.mkdirs();
					if (!isCreated){
						throw new SpringBatchException(NodeMessage.JEMC153E, parent.getAbsolutePath());
					}
				}
			} else {
				// if the file is not defined as NEW, it must exist, otherwise
				// exception
				if (!file.exists()){
					throw new SpringBatchException(SpringBatchMessage.JEMS009E, ddImpl.getName(), ddImpl.getDisposition());
				}
			}
		}
		// adds dataset to data description
		ddImpl.addDataSet(dataset);
	}

	/**
	 * Gets the dataset implementation and loads it with all information for
	 * dataset implementation (necessary for GRS), depending on different type
	 * of dataset.<br>
	 * Checks also if the definitions are correct (see wrong disposition for
	 * specific dataset type).<br>
	 * 
	 * @param ds dataset instance
	 * @throws IOException occurs if errors
	 * @throws SpringBatchException occurs if errors
	 * 
	 */
	public static void createDataSetImpl(DataSet ds) throws SpringBatchException, IOException {
		// get dataset impl setting name
		DataSetImpl dataset = ds.getDataSetImpl();
		
		if (ds.isTemporary()) {
		
			// create temporary file using part of dataset name and suffix tmp,
			// sets delete on exit of JVM
			File file = File.createTempFile(ds.getName().substring(2), ".tmp");
			file.deleteOnExit();

			// set file and type into dataset
			dataset.setFile(file);
			dataset.setType(DataSetType.TEMPORARY);
			dataset.setName(ds.getName());

		} else if (ds.isInline()) {
			// is a inline dataset, that represents a temporary file with
			// constant prefix and tmp suffix
			File file = File.createTempFile(INLINE_FILE_NAME_PREFIX, ".tmp");
			file.deleteOnExit();

			// prints the content defined inside JCL into temporary file, to
			// pass to user program
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.write(ds.getText(), fos, CharSet.DEFAULT);
			fos.flush();
			fos.close();

			// set file and type into dataset
			dataset.setFile(file);
			dataset.setType(DataSetType.INLINE);
			dataset.setName(ds.getName());

		} else if (ds.isGdg()) {
			// is a gdg
			// create file object
			FileWrapper fileWrapper = getFile(ds);
			File file = fileWrapper.getFile();


			dataset.setName(fileWrapper.getDataSetName());
			// set file and type into dataset
			dataset.setType(DataSetType.GDG);
			dataset.setFile(file);

			// sets the offset of GDGD, already calculated when sets the name
			// into Dataset object
			dataset.setOffset(ds.getOffset());

		} else if (ds.isDefinedDatasource()) {
			// set file and type into dataset
			dataset.setType(DataSetType.RESOURCE);
			dataset.setDataSource(ds.getDatasource());
			dataset.setName(ds.getName());

		} else {
			// is a normal file
			// create file object
			FileWrapper fileWrapper = getFile(ds);
			File file = fileWrapper.getFile();

			dataset.setName(fileWrapper.getDataSetName());
			// set file and type into dataset
			dataset.setType(DataSetType.FILE);
			dataset.setFile(file);
		}

	}
	
	/**
	 * 
	 * @param ddImpl
	 * @param ds
	 * @return
	 * @throws BuildException
	 */
	private static FileWrapper getFile(DataSet ds) throws SpringBatchException{
		// gets the data path from the environment
		// variables
		// if data path is null, exception
		String dataPath = System.getProperty(ConfigKeys.JEM_DATA_PATH_NAME);
		if (dataPath == null){
			throw new SpringBatchException(SpringBatchMessage.JEMS001E);
		}
		
		dataPath = FilenameUtils.normalize(dataPath, true);
		//checks if the Dsname is a absolute file name
		// if absolute path is equals return the file 
		// otherwise checks datapath
		
		File file = null;
		String fileName = null;
		
		if (ds.getName().startsWith(dataPath)){
			file =  new File(ds.getName());
			fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
			dataPath = FilenameUtils.normalize(dataPath, true);
			fileName = StringUtils.substringAfter(fileName, dataPath);
			fileName = fileName.substring(1);
		} else {
			file = new File(ds.getName());
			if (FilenameUtils.normalize(file.getAbsolutePath(), true).equalsIgnoreCase(ds.getName())){
				fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
			} else {
				file = new File(dataPath, ds.getName());
				fileName = FilenameUtils.normalize(ds.getName(), true);
			}
		}
		return  new FileWrapper(file, fileName);
	}

}

final class FileWrapper{

	private File file = null;
	
	private String dataSetName = null;
	
	/**
	 * @param file
	 * @param dataSetName
	 */
	public FileWrapper(File file, String dataSetName) {
		super();
		this.file = file;
		this.dataSetName = dataSetName;
	}


	/**
	 * @return the dataSetName
	 */
	public String getDataSetName() {
		return dataSetName;
	}

	/**
	 * @param dataSetName the dataSetName to set
	 */
	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileWrapper [file=" + file + ", dataSetName=" + dataSetName + "]";
	}
	
}