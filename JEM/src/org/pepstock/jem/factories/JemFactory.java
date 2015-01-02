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
package org.pepstock.jem.factories;

import java.io.Serializable;
import java.util.Properties;

import org.pepstock.jem.log.JemException;

/**
 * Is the merge of JCL and JobTask factories and the main interface to implement
 * if you want to manage a new control job language. The classes which
 * implements this factory must be configured in JEM configuration file inside
 * the <code>&lt;jcl-factories&gt;</code> element and
 * <code>&lt;class-factory&gt;</code> subelement.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface JemFactory extends JclFactory, JobTaskFactory, Serializable {

	/**
	 * Called to initialize the factory. A set of properties are passed, or a
	 * empty collection if the properties are not defined
	 * 
	 * @param properties properties
	 * @throws JemException if some properties are missing of wrong
	 */
	void init(Properties properties) throws JemException;

	/**
	 * Uses to identify the type of job control language. This is the unique key
	 * related to the factory loading it during the startup phase.
	 * 
	 * @see org.pepstock.jem.Jcl#getType()
	 * @return type string representation
	 */
	String getType();
	
	/**
	 * Uses to describe the type of job control language.
	 * 
	 * @see org.pepstock.jem.Jcl#getTypeDescription()
	 * @return description string representation
	 */
	String getTypeDescription();
	
	/**
	 * Returns all properties passed as argument on initialization.
	 * 
	 * @return properties
	 */
	Properties getProperties();

}
