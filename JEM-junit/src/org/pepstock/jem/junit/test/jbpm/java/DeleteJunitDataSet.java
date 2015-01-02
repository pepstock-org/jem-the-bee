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
package org.pepstock.jem.junit.test.jbpm.java;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.DataPathsContainer;

/**
 * This class is a spring batch tasklet thath will delete all the dataset create
 * during the junit test relative to spring batch.
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class DeleteJunitDataSet {
	/**
	 * name of the dataset that will contain the JDBC resource information
	 */
	private static final String DATA_SET_JUNIT_FOLDER = "test_jbpm";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
			for (String path : DataPathsContainer.getInstance().getDataPaths()){
				File dirToDelete = new File(path, DATA_SET_JUNIT_FOLDER);

				System.out.println("Deliting folder:" + dirToDelete);
				if (dirToDelete.exists()) {
					FileUtils.deleteDirectory(dirToDelete);
					System.out.println("folder:" + dirToDelete + " deleted");
				} else {
					System.out.println("folder:" + dirToDelete
							+ " does not exists");
				}
			}
	}

}
