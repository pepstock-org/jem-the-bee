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
package org.pepstock.jem.gwt.server.listeners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.pepstock.jem.Service;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.configuration.ConfigurationException;
import org.pepstock.jem.gwt.server.connector.ConnectorServiceFactory;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.net.InterfacesUtils;

import com.hazelcast.config.InMemoryXmlConfig;
import com.hazelcast.config.Interfaces;
import com.hazelcast.config.NetworkConfig;

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
	
	private static final String MANIFEST_FILE = "/META-INF/MANIFEST.MF";

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
		// gets servlet context and saves it
		SharedObjects.getInstance().setContext(context);
		// reads Hazecast init parameter
		String hazelcastFile = context.getInitParameter(ConfigKeys.HAZELCAST_CONFIG);
		// set the check version flag retreive by web.xml
		Boolean checkVersion = Boolean.valueOf(context.getInitParameter(ConfigKeys.JEM_CHECK_VERSION));
		SharedObjects.getInstance().setCheckVersion(checkVersion);
		setJemVersion(context);
		if (hazelcastFile != null) {
			try {
				// starts hazelcast
				startHazelcastClient(context, hazelcastFile);
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
	private void startHazelcastClient(ServletContext context, String hazelcastFile) throws ConfigurationException {
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG006I);
		InputStream input = context.getResourceAsStream(hazelcastFile);
		StringWriter sw = new StringWriter();
		// loads configuration file from XML file
		InMemoryXmlConfig config;
		try {
			IOUtils.copy(input, sw, CharSet.DEFAULT);
			config = new InMemoryXmlConfig(sw.toString());
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
		// start connector service
		try {
			SharedObjects.getInstance().setHazelcastConfig(config);
			SharedObjects.getInstance().setNetworkInterface(InterfacesUtils.getInterface(config));
			// Overrides the 
			NetworkConfig network = config.getNetworkConfig();
			Interfaces interfaces = network.getInterfaces();
			// overrides network only if is not set
			if (interfaces == null){
				network.getInterfaces().setEnabled(true).addInterface(Main.getNetworkInterface().getAddress().getHostAddress());
			}

			LogAppl.getInstance().emit(NodeMessage.JEMC273I, SharedObjects.getInstance().getNetworkInterface());
			Service connectorService = ConnectorServiceFactory.getConnectorService();
			SharedObjects.getInstance().setConnectorService(connectorService);
			SharedObjects.getInstance().getConnectorService().start();
        } catch (MessageException e) {
        	throw new ConfigurationException(e);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Set the jem Version
	 * 
	 * @param contextPath
	 */
	private void setJemVersion(ServletContext context) {
		// reads manifest file for searching version of JEM
		InputStream fis = null;
		try {
			fis = context.getResourceAsStream(MANIFEST_FILE);
			if (fis == null){
				throw new FileNotFoundException(MANIFEST_FILE);
			}
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