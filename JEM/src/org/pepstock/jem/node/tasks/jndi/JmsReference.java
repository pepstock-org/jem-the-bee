/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.node.tasks.jndi;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.Reference;

/**
 * Sets the constants for <code>JNDI</code> needed by <code>JMS</code> datasources object. <br>
 * It uses <code>javax.jms</code> classes.
 * It uses {@link JmsFactory} to create a <code>JMS</code>
 * datasource to be used inside the java programs. <br>
 * The <code>JMS</code> datasource object allows to connect to a <code>JMS</code> provider, and write or read 
 * <code>Queues</code> or <code>Topics</code>.
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public class JmsReference extends Reference{

	private static final long serialVersionUID = 1L;

	/**
	 * Declaration of the factory to be used (when requested) to create the <code>JMS JNDI</code> datasource object:<br>
	 * <dd><b>{@link ConnectionFactory}</b>, the <code>JMS</code> datasource object.
	 * 
	 * @see #JNDI_OBJECT
	 */
	public static final String JNDI_FACTORY = JmsFactory.class.getName();
	
	/**
	 * Is the <code>JNDI</code> datasource object created when requested. <br>
	 * This object is useful to access <code>JMS</code> sources, for example
	 * <code>queues</code> and <code>topic</code>, and to write and read them.
	 * 
	 * @see ConnectionFactory
	 */
	public static final String JNDI_OBJECT = Context.class.getName();
	
	/**
	 * Creates a <code>JNDI</code> reference for <code>JMS</code> purposes.
	 */
	public JmsReference() {
		super(JNDI_OBJECT, JNDI_FACTORY, null);
	}
}