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
package org.pepstock.jem.junit.test.ftp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class StartFtpD extends TestCase{
	
	
	/**
	 * Clean dataset created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testStartFtpD() throws Exception {
		FtpThread td = new FtpThread();
		td.setDaemon(false);
		td.start();
		
//		FTPClient ftp =  new FTPClient();
//		ftp.connect("127.0.0.1", 2121);
//		ftp.login("admin", "admin");
//		FTPFile[] files = ftp.listFiles();
//		
//		for (int i=0; i<files.length;i++){
//			System.err.println(files[i]);
//		}
//		System.err.println(files.length);
//		
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		
//		ftp.retrieveFile("sample.data", os);
//		
//		System.err.println("zio cane  "+os.size());
//		
//		ftp.logout();
//		ftp.disconnect();
		
		
//		InputStream is = ftp.retrieveFileStream("sample.data");
		
//		IOUtils.copy(is, System.out);
		
//		IOUtils.closeQuietly(is);
		
	}
	
	
	class FtpThread extends Thread {
		
		private static final String CONFIG_FILE = "./ftp/config/users.properties";
		
		private static final String LIB_FOLDER = "./ftp/lib";
		

		/**
		 * 
		 */
		public FtpThread() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
			try {
//				String[] args = new String[]{"--default"};
	            Collection<File> files = FileUtils.listFiles(new File(LIB_FOLDER), new String[]{"jar"}, false);
	            
	            URL[] urls = new URL[files.size()];

	            int index = 0;
	            for (File lib : files){
	            	urls[index] = lib.toURL();
	            	index++;
	            }
	            
	            URLClassLoader u = new URLClassLoader(urls);
	            Class c = u.loadClass("org.apache.ftpserver.FtpServerFactory");
	            FtpServerFactory serverFactory = (FtpServerFactory)c.newInstance();
	            
	            ListenerFactory factory = new ListenerFactory();
	            
	            // set the port of the listener
	            factory.setPort(2121);

	            // replace the default listener
	            serverFactory.addListener("default", factory.createListener());
	            
	            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
	            userManagerFactory.setFile(new File(CONFIG_FILE));
	            
	            serverFactory.setUserManager(userManagerFactory.createUserManager());
	            
	            // start the server
	            FtpServer server = serverFactory.createServer(); 
	            
	            server.start();
	            
            } catch (MalformedURLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (SecurityException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (IllegalArgumentException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (ClassNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
            } catch (IllegalAccessException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
//            } catch (InvocationTargetException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
            } catch (InstantiationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (FtpException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
	}
}
