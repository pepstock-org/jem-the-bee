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
package org.pepstock.jem.node.executors.jobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.OutputSystem;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Returns the tree of job log folder.<br>
 * The tree will use on UI to read output
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GetOutputTree extends DefaultExecutor<OutputTree> {

	private static final long serialVersionUID = 1L;
	
	private static final String JEM_LOG_LABEL = "JEM log";

	private static final String JOB_LOG_LABEL = "JOB log";
	
	private static final OutputTreeComparator COMPARATOR = new OutputTreeComparator();
	
	private Job job = null;

	/**
	 * Constructs saving job instance
	 * @param job job instance
	 */
	public GetOutputTree(Job job) {
		this.job = job;
	}

	/**
	 * Reads file system and returns the tree of job log folder.
	 * 
	 * @return output tree
	 * @throws if I/O error occurs 
	 */
	@Override
	public OutputTree execute() throws ExecutorException {
		// gets the jcl file to extract the directory
		File jclFile;
		try {
			jclFile = Main.getOutputSystem().getJclFile(job);
		} catch (IOException e) {
			// messo il job ma il node file va messo
			throw new ExecutorException(NodeMessage.JEMC242E, e, job);
		}
		// checks if file exists otherwise exception occurs
		if (!jclFile.exists()){
			throw new ExecutorException(NodeMessage.JEMC242E, jclFile);
		}
		
		// creates the empty result
		OutputTree tree = new OutputTree();
		
		// get the folder of JCL. is folder used for job output
		File jobOutputFolder = jclFile.getParentFile(); 
		// lists all files 
		File[] files = jobOutputFolder.listFiles();

		Arrays.sort(files, COMPARATOR);
		
		// scans all files of folder and load the result tree
		for (int i=0; i<files.length; i++){
			File file = files[i];
			// ignores JCL file because is already inside the job and
			// due to you need this data in UI, doesn't make sense to download it
			if (!file.getName().equalsIgnoreCase(OutputSystem.JCL_FILE) && !file.getName().equalsIgnoreCase(OutputSystem.JOB_FILE)){
				// if is not the directory (means not syslog of step)
				// this is the first level of folder
				if (!file.isDirectory()){
					if (file.getName().equalsIgnoreCase(OutputSystem.JOBLOG_FILE) || file.getName().equalsIgnoreCase(OutputSystem.MESSAGESLOG_FILE)){
						// creates the items and sets label (to show on UI) and relative path of file
						OutputListItem item = new OutputListItem();
						String label = (file.getName().equalsIgnoreCase(OutputSystem.JOBLOG_FILE)) ? JEM_LOG_LABEL : JOB_LOG_LABEL; 
						item.setLabel(label);
						String fileRelativePath = StringUtils.remove(file.getAbsolutePath(), Main.getOutputSystem().getOutputPath().getAbsolutePath());
						item.setFileRelativePath(FilenameUtils.normalize(fileRelativePath, true));
						// adds to first level
						tree.getFirstLevelItems().add(item);
					}
				} else  {
					// checks the folder which represents the folder for all syslog of step
					// loads all files in second level
					List<OutputListItem> secondLevelItems = new ArrayList<OutputListItem>();
					// gets all files of folder, scanning them
					File[] stepFiles = file.listFiles();
					
					for (int k=0; k<stepFiles.length; k++){
						File stepFile = stepFiles[k];
						// ignores directory. it shouldn't be present directory here
						if (!stepFile.isDirectory()){
							// creates the items and sets label (to show on UI) and relative path of file							
							OutputListItem item = new OutputListItem();
							item.setLabel(stepFile.getName());
							item.setParent(file.getName());
							String fileRelativePath = StringUtils.remove(stepFile.getAbsolutePath(), Main.getOutputSystem().getOutputPath().getAbsolutePath());
							item.setFileRelativePath(FilenameUtils.normalize(fileRelativePath, true));
							secondLevelItems.add(item);
						}
					}
					// adds to second level
					tree.getSecondLevelItems().add(secondLevelItems);
				}
			}
		}
		return tree;
	}
}