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
package org.pepstock.jem.util.migrate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.Configuration;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.configuration.Database;
import org.pepstock.jem.node.persistence.CommonResourcesDBManager;
import org.pepstock.jem.node.persistence.DBPoolManager;
import org.pepstock.jem.node.persistence.InputDBManager;
import org.pepstock.jem.node.persistence.JobDBManager;
import org.pepstock.jem.node.persistence.OutputDBManager;
import org.pepstock.jem.node.persistence.RoutingDBManager;
import org.pepstock.jem.node.persistence.RunningDBManager;
import org.pepstock.jem.node.persistence.SQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.DB2SQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.DefaultSQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.MySqlSQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.OracleSQLContainerFactory;
import org.pepstock.jem.util.CharSet;

/**
 * Utility to migrate the jobs and resources (changed on own XML format)
 * from version 2.1 and 2.2.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class DBMaint {
	
	private static final String CONFIGURATION = "configuration";

	/**
	 * To avoind any instantiation
	 */
	private DBMaint() {
	}

	/**
	 * Main program which accept an argument with the file where JEM-ENV configuration
	 * file is stored.
	 * <br>
	 * It uses to read the database information and to initialize the database managers.
	 * 
	 * @param args list of arguments: it accepts a argument with the file of JEM-ENV configuration file, by tag 
	 * @throws ConfigurationException if there is any error reading the JEM configuration
	 * @throws SQLException if there is any error updating the JEM tables on the database
	 * @throws ParseException if there is an error on the arguments list
	 */
	public static void main(String[] args) throws ConfigurationException, SQLException {
		// reads arguments
		// -configuration mandatory
		@SuppressWarnings("static-access")
		Option propFile =  OptionBuilder.withArgName(CONFIGURATION).hasArg().withDescription("The file of JEM environment configuration.").create(CONFIGURATION);
		// the argument is mandatory
		propFile.setRequired(true);

		// parses all arguments
		ArgumentsParser parser = new ArgumentsParser(DBMaint.class.getName());
		// adds to the map of all args
		parser.getOptions().add(propFile);
		// gets the properties with all
		// arguments
		Properties properties;
		try {
			properties = parser.parseArg(args);
		} catch (ParseException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			return;
		}

		// gets the configuration file
		String configFile = properties.getProperty(CONFIGURATION);
		File fileConfig = new File(configFile);
	
		// reads the JEM ENV configuration file
		String xmlConfig = null;
		try {
			xmlConfig = FileUtils.readFileToString(fileConfig, CharSet.DEFAULT_CHARSET_NAME);
		} catch (IOException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC006E);
			throw new ConfigurationException(NodeMessage.JEMC006E.toMessage().getMessage(), e);
		}
		
		// parses the configuration object
		Configuration config = Configuration.unmarshall(xmlConfig);
		LogAppl.getInstance().emit(NodeMessage.JEMC008I, configFile);
		
		// gets ONLY the database config
		Database database = config.getDatabase();
		// if database is null, EXCEPTION
		if (database == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.DATABASE_ELEMENT);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.DATABASE_ELEMENT));
		}
		// gets URL
		String url = database.getUrl();
		// shows which database is used
		LogAppl.getInstance().emit(NodeMessage.JEMC193I, url);
		// parses the URL to get which database has been used
		String dbType = null;
		try {
			// parses URL
			URI url1 = new URI(database.getUrl());
			// gets Scheme Specific part
			URI myURL = new URI(url1.getSchemeSpecificPart());
			// and gets database type
			dbType = myURL.getScheme();
		} catch (URISyntaxException e2) {
			LogAppl.getInstance().emit(NodeMessage.JEMC166E, e2, database.getUrl());
			throw new ConfigurationException(NodeMessage.JEMC166E.toMessage().getFormattedMessage(database.getUrl()));
		}

		// defines the SQL container
		// using the database type
		SQLContainerFactory engine = null;
		if (dbType.equals(MySqlSQLContainerFactory.DATABASE_TYPE)) {
			engine = new MySqlSQLContainerFactory();
		} else if (dbType.equals(OracleSQLContainerFactory.DATABASE_TYPE)) {
			engine = new OracleSQLContainerFactory();
		} else if (dbType.equals(DB2SQLContainerFactory.DATABASE_TYPE)) {
			engine = new DB2SQLContainerFactory();
		} else {
			engine = new DefaultSQLContainerFactory();
		}

		try {
			// creates database pool
			// using the database configuration
			DBPoolManager.getInstance().setDriver(database.getDriver());
			DBPoolManager.getInstance().setUrl(database.getUrl());
			DBPoolManager.getInstance().setUser(database.getUser());
			DBPoolManager.getInstance().setPassword(database.getPassword());
			DBPoolManager.getInstance().setProperties(database.getProperties());
			DBPoolManager.getInstance().setKeepAliveConnectionSQL(engine.getKeepAliveConnectionSQL());
			// init the db pool
			DBPoolManager.getInstance().init();

			// initialize jobs DB managers
			// necessary to migrate the database
			InputDBManager.getInstance().setSqlContainer(engine.getSQLContainerForInputQueue());
			RunningDBManager.getInstance().setSqlContainer(engine.getSQLContainerForRunningQueue());
			OutputDBManager.getInstance().setSqlContainer(engine.getSQLContainerForOutputQueue());
			RoutingDBManager.getInstance().setSqlContainer(engine.getSQLContainerForRoutingQueue());
			// initialize common resources DB manager
			// necessary to migrate the database
			CommonResourcesDBManager.getInstance().setSqlContainer(engine.getSQLContainerForCommonResourcesMap());
		} catch (SQLException e) {
			throw new ConfigurationException(NodeMessage.JEMC165E.toMessage().getFormattedMessage(JobDBManager.class.getName()), e);
		}
		// creates DB migration utility for jobs
		JobDBUpdate job = new JobDBUpdate();
		// start migration
		job.start();
		// creates DB migration utility for resources
		ResourceDBUpdate resource = new ResourceDBUpdate();
		// start migration
		resource.start();
	}
}
