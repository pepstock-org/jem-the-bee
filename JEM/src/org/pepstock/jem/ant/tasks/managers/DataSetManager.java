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
package org.pepstock.jem.ant.tasks.managers;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.ant.tasks.DataSet;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;
import org.pepstock.jem.util.CharSet;

/**
 * Creates all data set implementation called by DataDescriptionManager
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
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
	 * Gets the dataset implementation searching inside of data set container.
	 * Checks also if the definitions are correct (see wrong disposition for
	 * specific dataset type).
	 * 
	 * @param ddImpl data description implementation
	 * @param ds dataset instance
	 * @throws BuildException if disposition is not compliant with reference
	 */
	public static void loadReferences(DataDescriptionImpl ddImpl, DataSet ds) throws BuildException {
		// checks if reference
		if (ds.isReference()) {
			// gets container by singleton method
			ImplementationsContainer container = ImplementationsContainer.getInstance();
			// disposition NEW in a referback is not allowed because for sure
			// the files already exist
			if (ddImpl.getDisposition().equalsIgnoreCase(Disposition.NEW)){
				throw new BuildException(AntMessage.JEMA009E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()));
			}
			// container doesn't have the reference, file not previously
			// defined, so exception
			if (!container.hasDataDescription(ds.getName())) {
				throw new BuildException(AntMessage.JEMA010E.toMessage().getFormattedMessage(ddImpl.getName(), ds.getName()));
			}

			// gets data desription impl
			DataDescriptionImpl referback = container.getDataDescription(ds.getName());
			// if data descritpion is multi dataset, only accessible in SHR mod,
			// otherwise exception
			if ((referback.getDatasets().size() > 1) && (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR))){
				throw new BuildException(AntMessage.JEMA009E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()));
			}

			// scans all datasets of referenced data description
			for (DataSetImpl dataset : referback.getDatasets()) {
				// gets file
				File file = (File) dataset.getFile();

				// if file doesn't exist anymore, throws an exception
				if (!file.exists()) {
					throw new BuildException(AntMessage.JEMA011E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()), new FileNotFoundException(file.getAbsolutePath()));
				}
				// adds dataset to new data descritpion impl. created in this
				// task (then step)
				ddImpl.addDataSet(dataset);
			}
			// saves the complete reference anyway
			ddImpl.setDataDescriptionReference(referback);
		} else {
			// calls load reference but the data description is not a reference,
			// so exception
			throw new BuildException(AntMessage.JEMA012E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()));
		}

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
	 * @param item data description item (task)
	 * @throws IOException if I/O error occurs
	 * @throws BuildException configuration not compliant (disposition wrongs or
	 *             invalid call)
	 */
	public static void createDataSetImpl(DataDescriptionImpl ddImpl, DataSet ds, DataDescriptionStep item) throws IOException, BuildException {
		// creates a new instance of dataset implemetation
		DataSetImpl dataset = new DataSetImpl();

		// checks all dataset types
		if (ds.isReference()) {
			// calls load dataset but is a reference, so exception
			throw new BuildException(AntMessage.JEMA012E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()));
		} else if (ds.isTemporary()) {
			// if dispostion is not new for temporary, exception!
			if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.NEW)){
				throw new BuildException(AntMessage.JEMA013E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()));
			}

			if (ds.getName() == null){
				throw new BuildException(AntMessage.JEMA014E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition()));
			}
			
			// set the name
			dataset.setName(ds.getName());
			// create temporary file using part of dataset name and suffix tmp,
			// sets delete on exit of JVM
			File file = File.createTempFile(ds.getName().substring(2), ".tmp");
			file.deleteOnExit();
			// set file and type into dataset
			dataset.setFile(file);
			dataset.setType(DataSetType.TEMPORARY);

		} else if (ds.isInline()) {
			// set the name
			dataset.setName(ds.getName());
			// is a inline dataset, that represents a temporary file with
			// constant prefix and tmp suffix
			File file = File.createTempFile(INLINE_FILE_NAME_PREFIX, ".tmp");
			file.deleteOnExit();

			// prints the content defined inside JCL into temporary file, to
			// pass to user program
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.write(item.getProject().replaceProperties(ds.getText().toString()), fos, CharSet.DEFAULT);
			fos.flush();
			fos.close();

			// set file and type into dataset
			dataset.setFile(file);
			dataset.setType(DataSetType.INLINE);

		} else if (ds.isGdg()) {
			if (ds.getName() == null){
				throw new BuildException(AntMessage.JEMA014E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition()));
			}
			
			// is GDG
			// gets the right file object
			FileWrapper fileWrapper = getFile(ds, ddImpl.getDisposition());
			File file = fileWrapper.getFile();

			// set the name
			dataset.setName(fileWrapper.getDataSetName());

			// set file and type into dataset
			dataset.setType(DataSetType.GDG);
			dataset.setFile(file);

			// sets the offset of GDGD, already calculated when sets the name
			// into Dataset object
			dataset.setOffset(ds.getOffset());

		} else if (ds.isDatasource()) {
			// set file and type into dataset
			dataset.setType(DataSetType.RESOURCE);
			dataset.setDataSource(ds.getDatasource());
			
			// set the name
			dataset.setName(ds.getName());

		} else {
			if (ds.getName() == null){
				throw new BuildException(AntMessage.JEMA014E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition()));
			}
			// is normal file
			// gets the right file object
			FileWrapper fileWrapper = getFile(ds, ddImpl.getDisposition());
			File file = fileWrapper.getFile();
			
			// set the name
			dataset.setName(fileWrapper.getDataSetName());

			// checks disposition. if NEW, the file doesn't must exist,
			// otherwise exception
			if (ddImpl.getDisposition().equalsIgnoreCase(Disposition.NEW)) {
				if (file.exists()){
					throw new BuildException(AntMessage.JEMA015E.toMessage().getFormattedMessage(ddImpl.getName(), ddImpl.getDisposition(), ds.toString()));
				}
				// file name could have a new dirctories. Create them here
				File parent = file.getParentFile();
				if (!parent.exists()){
					boolean isCreated = parent.mkdirs();
					if (!isCreated){
						throw new BuildException(NodeMessage.JEMC153E.toMessage().getFormattedMessage(parent.getAbsolutePath()));
					}
				}
			} else {
				// for all other disposition (not equals to NEW), the file must
				// exist otherwise exception
				if (!file.exists()){
					throw new BuildException(AntMessage.JEMA010E.toMessage().getFormattedMessage(ddImpl.getName(), ds.getName()));
				}
			}
			// set file and type into dataset
			dataset.setType(DataSetType.FILE);
			dataset.setFile(file);
		}
		// adds dataset to data description
		ddImpl.addDataSet(dataset);
	}
	/**
	 * Creates a file wrapper, normalizing the name. That's necessary due to you can write on ANT JCL 
	 * both the relative or the absolute file name.
	 * 
	 * @param ds dataset instance
	 * @return a file wrapper instance
	 * @throws BuildException if dataPath is null, returns ann exception
	 */
	private static FileWrapper getFile(DataSet ds, String disposition) throws BuildException{
		// gets the data path and checks
		// if dataset name starts
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(ds.getName());

		File file = null;
		String fileName = null;

		//checks if the Dsname is a absolute file name
		// if absolute path is equals return the file 
		// otherwise checks datapath
		if (dataPath != null){
			// if name is absolute
			// creates a new FILE object with full pathname 
			file =  new File(ds.getName());
			// normalizes using UNIX rules
			fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
			// extract the short name, taking the string after dataPath
			fileName = StringUtils.substringAfter(fileName, dataPath);
			// removes the first / of the filename
			fileName = fileName.substring(1);
			
			// we must check if full is correct in disposition NEW (only for new allocation)
			if (Disposition.NEW.equalsIgnoreCase(disposition)){
				try {
					// checks all paths
					PathsContainer paths = DataPathsContainer.getInstance().getPaths(fileName);
					// creates a file with dataPath as parent, plus file name  
					file = new File(paths.getCurrent().getContent(), fileName);

				} catch (InvalidDatasetNameException e) {
					throw new BuildException(e);
				}
			}
		} else {
			// should be relative
			file = new File(ds.getName());
			// normalizes the full path and checks again with the name
			// if equals means that is absolute because the previuos checks only if it's using the 
			// data paths. here the absolute path IS NOT a data path
			if (FilenameUtils.normalize(file.getAbsolutePath(), true).equalsIgnoreCase(ds.getName())){
				// normalizes using UNIX rules
				fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
			} else {
				try {
					// checks all paths
					PathsContainer paths = DataPathsContainer.getInstance().getPaths(ds.getName());
					// is relative!
					// creates a file with dataPath as parent, plus file name  
					file = new File(paths.getCurrent().getContent(), ds.getName());
					// if disposition is not in new allocation and the file with current path doesn't exists,
					// try to use the old path is exist
					if (!Disposition.NEW.equalsIgnoreCase(disposition) && !file.exists() && paths.getOld()!=null){
						file = new File(paths.getOld().getContent(), ds.getName());
					}
					// normalizes using UNIX rules
					fileName = FilenameUtils.normalize(ds.getName(), true);
				} catch (InvalidDatasetNameException e) {
					throw new BuildException(e);
				}
			}
		}
		return new FileWrapper(file, fileName);
	}
}

/**
 * Contains information about the file to using, already normalized and
 * with absolute path, based on dataPath.  
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2	
 *
 */
final class FileWrapper{

	private File file = null;
	
	private String dataSetName = null;
	
	/**
	 * Constructor with file object and is absolute name, already normalized, UNIX like
	 *  
	 * @param file file object
	 * @param dataSetName full name
	 */
	public FileWrapper(File file, String dataSetName) {
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
	
}