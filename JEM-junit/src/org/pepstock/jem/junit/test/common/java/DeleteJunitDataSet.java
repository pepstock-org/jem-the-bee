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
package org.pepstock.jem.junit.test.common.java;

import java.io.File;

import javax.naming.InitialContext;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.ant.AntKeys;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * This class will delete all the dataset create during the junit test relative
 * to folder passed as parameter.
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class DeleteJunitDataSet {

	/**
	 * 
	 * @param args[0] is the folder on the jem.dataPaths to delete
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if(args==null || args[0].length() == 0){
			throw new Exception("Need argument for the folder to delete !");
		}
		String folder=args[0];
		InitialContext ic = ContextUtils.getContext();
		// loads datapath container
		DataPathsContainer dc = (DataPathsContainer)ic.lookup(AntKeys.ANT_DATAPATHS_BIND_NAME);
		for (String path : dc.getDataPaths()){
			File dirToDelete = new File(path, folder);
			System.out.println("Deliting folder:" + dirToDelete);
			if (dirToDelete.exists()) {
				FileUtils.deleteDirectory(dirToDelete);
				System.out.println("folder:" + dirToDelete + " deleted");
			} else {
				System.out.println("folder:" + dirToDelete + " does not exists");
			}
		}
	}
}
