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
package org.pepstock.jem.junit.test.antutils.java;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.Configuration;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.XmlUtil;
import org.pepstock.jem.util.CharSet;

import com.thoughtworks.xstream.XStream;

/**
 * This class will create a dataset containing the definition of the data source
 * containing the information to connect to JEM-DB in read only mode. This
 * dataset will then be used by the junit-test to create the JDBC resource that
 * will be used to make an example of jdbc connection through the use of JEM
 * resource
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class CreateJemJdbcResource {
	/**
	 * config file of the environment
	 */
	public static final String JEM_ENV_CONFIG = "config/jem-env.xml";

	/**
	 * name of the dataset that will contain the JDBC resource information
	 */
	public static final String DATA_SET_NAME = "/test_common/JEM_JDBC_RESOURCE";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String persistencePath = System
				.getProperty(ConfigKeys.JEM_PERSISTENCE_PATH_NAME);
		String dataPath = System.getProperty(ConfigKeys.JEM_DATA_PATH_NAME);
		File file = new File(persistencePath);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		// get first directory that should be one of the JEM environment
		String jemEnv = directories[0];
		File configFile = new File(persistencePath + "/" + jemEnv + "/"
				+ JEM_ENV_CONFIG);
		String xmlConfig = FileUtils.readFileToString(configFile,
				CharSet.DEFAULT_CHARSET_NAME);
		Configuration jemEnvConf = Configuration.unmarshall(xmlConfig);
		// create dataSource with JEM Dabase info read from configuration file
		Resource resource = new Resource();
		resource.setType("jdbc");
		resource.setName("JUNIT_JDBC_JEM");
		resource.setUser("root");
		resource.setLastModified(new Date());
		resource.setProperty("driverClassName", jemEnvConf.getDatabase()
				.getDriver());
		resource.setProperty("url", jemEnvConf.getDatabase().getUrl());
		resource.setProperty("username", jemEnvConf.getDatabase().getUser());
		resource.setProperty("password", jemEnvConf.getDatabase().getPassword());
		resource.setProperty("defaultReadOnly", "true");
		resource.setProperty("defaultAutoCommit", "true");
		XStream xStream = XmlUtil.getXStream();
		// write the resource to a file that will than be use to import the
		// resource on JEM
		String xmlResource = xStream.toXML(resource);
		String dataSet = dataPath + DATA_SET_NAME;
		System.out.println("Generated reousrce:");
		System.out.println(xmlResource);
		System.out.println("");
		System.out.println("Writing resource to dataset:" + dataSet);
		FileUtils.writeStringToFile(new File(dataSet), xmlResource);
	}
}
