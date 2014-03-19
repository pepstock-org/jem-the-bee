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
package org.pepstock.jem.gwt.server.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.pepstock.jem.Service;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.configuration.ConfigurationException;
import org.pepstock.jem.gwt.server.connector.ConnectorServiceFactory;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;

import com.hazelcast.config.FileSystemXmlConfig;

/**
 * Context listener which starts up JEM web application, initializing Log4j,
 * SharedObject and Hazelcast.<br>
 * Extends SHIRO startup, reading the context attribute
 * <code>hazelcast.config</code> which contains the configuration file to start
 * Hazelcast.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StartUp extends EnvironmentLoaderListener implements ServletContextListener {

	/**
	 * Constraucts initializing log4j and shared objects
	 */
	public StartUp() {
		System.setProperty("hazelcast.logging.type", "log4j");
		// starts log4j
		LogAppl.getInstance();
		// creates shared objects
		SharedObjects.createInstance();
		// sets IPV4
		System.setProperty(ConfigKeys.IPV4, Boolean.TRUE.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// stop connector services
		SharedObjects.getInstance().getConnectorService().shutdown();
		// stop client instance
		SharedObjects.getInstance().shutdownHazelcatClient();
		// calls the super class
		super.contextDestroyed(event);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// starts SHIRO
		super.contextInitialized(event);
		// gets servlet context
		ServletContext context = event.getServletContext();
		// gets servlet real path
		String contextPath = context.getRealPath(".");
		contextPath = contextPath.substring(0, contextPath.length() - 1);
		// reads Hazecast init parameter
		String hazelcastFile = context.getInitParameter(ConfigKeys.HAZELCAST_CONFIG);
		// set the check version flag retreive by web.xml
		Boolean checkVersion = Boolean.valueOf(context.getInitParameter(ConfigKeys.JEM_CHECK_VERSION));
		SharedObjects.getInstance().setCheckVersion(checkVersion);
		setJemVersion(contextPath);
		if (hazelcastFile != null) {
			try {
				// starts hazelcast
				startHazelcastClient(contextPath, hazelcastFile);
			} catch (ConfigurationException e) {
				throw new JemRuntimeException(e);
			}
		} else {
			throw new MessageRuntimeException(UserInterfaceMessage.JEMG021E);
		}
	}

	/**
	 * Starts Hazelcast client.
	 * 
	 * @param filename
	 *            Hazelast config file
	 * @throws ConfigurationException
	 *             if a config error occurs
	 */
	private void startHazelcastClient(String contextPath, String hazelcastFile) throws ConfigurationException {
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG006I);
		String filename = contextPath + hazelcastFile;
		// loads configuration file from XML file
		FileSystemXmlConfig config;
		try {
			config = new FileSystemXmlConfig(filename);
		} catch (FileNotFoundException e) {
			throw new ConfigurationException(e);
		}
		// start connector service
		try {
			SharedObjects.getInstance().setHazelcastConfig(config);
			Service connectorService = ConnectorServiceFactory.getConnectorServie();
			SharedObjects.getInstance().setConnectorService(connectorService);
			SharedObjects.getInstance().getConnectorService().start();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Set the jem Version
	 * 
	 * @param contextPath
	 */
	private void setJemVersion(String contextPath) {
		// reads manifest file for searching version of JEM
		File file = null;
		FileInputStream fis = null;
		try {
			file = new File(contextPath + "/META-INF/MANIFEST.MF");
			fis = new FileInputStream(file);
			Manifest manifest = new Manifest(fis);
			// gets JEM vrsion
			Attributes at = manifest.getAttributes(ConfigKeys.JEM_MANIFEST_SECTION);
			String jemVersion = at.getValue(ConfigKeys.JEM_MANIFEST_VERSION);
			// saves JEM version
			if (jemVersion != null) {
				SharedObjects.getInstance().setJemVersion(jemVersion);
			}
		} catch (FileNotFoundException e) {
			// to ignore stack trace
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(NodeMessage.JEMC184W);
		} catch (IOException e) {
			// to ignore stack trace
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(NodeMessage.JEMC184W);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}

	}
}