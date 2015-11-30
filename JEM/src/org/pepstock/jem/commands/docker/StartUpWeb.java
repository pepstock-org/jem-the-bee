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
package org.pepstock.jem.commands.docker;

import java.util.Properties;

import org.pepstock.jem.commands.util.NodeProperties;
import org.pepstock.jem.node.configuration.ConfigurationException;


/**
 * Creates a node using the minimum arguments to use into Docker container run.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class StartUpWeb extends StartUp{
	
	private static final String COMMAND = "jem-web.sh";

	/**
	 * Constructs the object saving the command name (necessary on help) and adding arguments definitions.
	 * 
	 * @param commandName command name  (necessary on help)
	 */
	public StartUpWeb() {
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.docker.StartUp#loadProperties(java.util.Properties)
	 */
	@Override
	void loadProperties(Properties props) throws ConfigurationException {
		// sets the mandatory values even if they are not necessary
		props.setProperty(NodeProperties.JEM_DB_DRIVER, this.getClass().getName());
		props.setProperty(NodeProperties.JEM_DB_URL, this.getClass().getName());
	}



	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.docker.StartUp#getCommand()
	 */
	@Override
	String getCommand() {
		return COMMAND;
	}



	/**
	 * Main method! Parses the arguments, creates the client, submits job.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		StartUpWeb create = new StartUpWeb();
		System.exit(create.execute(args));
	}
	
}
