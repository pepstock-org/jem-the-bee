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
package org.pepstock.jem.junit.test.jms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.junit.test.rest.RestManager;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class LoadLibraries extends TestCase {

	/**
	 * Load libraries, to the GFS classpath folder, needed by the test suite
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		UploadedGfsFile uploaFile = new UploadedGfsFile();
		File fileToUpload = new File("jms/lib/ffmq3-core.jar");
		byte[] bytes = new byte[(int) fileToUpload.length()];
		InputStream is = new FileInputStream(fileToUpload);
		is.read(bytes);
		is.close();
		uploaFile.setUploadedFile(fileToUpload);
		uploaFile.setType(GfsFileType.CLASS);
		uploaFile.setGfsPath("jms/");
		RestManager.getSharedInstance().getGfsManager().putFile(uploaFile);
	}
}
