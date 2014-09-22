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
package org.pepstock.jem.ant.tasks;

import java.util.Vector;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Upload extends Task {
	
	private Vector<FileSet> filesets = new Vector<FileSet>();

	/**
	 * 
	 */
	public Upload() {
		
	}
	
    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }
    
    public void execute() {
                                                        
        String foundLocation = null;
        for(FileSet fs : filesets ) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            System.err.println("*** "+fs.getDir());
            System.err.println("*** "+fs.getLocation());
            String[] includedFiles = ds.getIncludedFiles();
            for(int i=0; i<includedFiles.length; i++) {
            	System.err.println(includedFiles[i]);
            	
//                String filename = includedFiles[i].replace('\\','/');           
//                filename = filename.substring(filename.lastIndexOf("/")+1);
//                if (foundLocation==null && file.equals(filename)) {
//                    File base  = ds.getBasedir();                               
//                    File found = new File(base, includedFiles[i]);
//                    foundLocation = found.getAbsolutePath();
//                }
            }
        }
    }

}
