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

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.util.MemorySize;

/**
 * Returns the content of logs of job.<br>
 * Can get both Message log and Job log.<br>
 * Useful by Graphic User Interface
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GetOutputFileContent extends DefaultExecutor<String> {

	private static final long serialVersionUID = 1L;
	
	private static final int MAX_NUMBER_OF_BYTE_READABLE = MemorySize.MB * 5; //5MB

	private OutputListItem item = null;

	/**
	 * Constructs the object passing the job which you need logs of, and relative file by item (relative to <code>ouptut</code> path to download.
	 * 
	 * @param job job instance
	 * @param item item which represents the file to download to UI
	 */
	public GetOutputFileContent(OutputListItem item) {
		this.item = item;
	}

	/**
	 * Checks file you need, reads it from file system and returns it.
	 * 
	 * @return instance with content of log
	 * @throws Exception occurs if errors
	 */
	@Override
	public String execute() throws ExecutorException {
		// gets the file to download
		File file = new File(Main.getOutputSystem().getOutputPath(), item.getFileRelativePath());

		// checks if file exists otherwise exception occurs
		if (!file.exists()){
			throw new ExecutorException(NodeMessage.JEMC242E, file);
		}

		try {
			// loads content file into a buffer
			// must be check the file size... if too big could create problems
			if (file.length() > MAX_NUMBER_OF_BYTE_READABLE){
				return "Output log file too large. Current file size is "+file.length()+" bytes but maximum is "+MAX_NUMBER_OF_BYTE_READABLE+" bytes";
			} else {
				// creates a output container and sets job file content
				return FileUtils.readFileToString(file);
			}			
		} catch (IOException e) {
			throw new ExecutorException(NodeMessage.JEMC242E, e, file);
		}
	}
}