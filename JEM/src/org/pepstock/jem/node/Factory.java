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
package org.pepstock.jem.node;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.factories.JclFactory;
import org.pepstock.jem.factories.JclFactoryException;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.log.LogAppl;

/**
 * Creates a new JCL object to load into PreJob object (using static methods)
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Factory {

	/**
	 * To avoid any instantiation
	 */
	private Factory() {
		
	}

	/**
	 * Checks if there's a factory loaded with type, specified submitting the
	 * job. If not, an exception will throw. Then it calls the
	 * <code>createJcl</code> method of JemFactory and load Prejob with new JCL
	 * object.
	 * 
	 * @see org.pepstock.jem.PreJob#getJclType()
	 * @see org.pepstock.jem.factories.JemFactory#createJcl(String)
	 * @param prejob Prejob object instance, used to load JCL object
	 * @throws JclFactoryException if the factory is not found or the factory
	 *             has an exception creating and validating the JCL source
	 */
	public static void loadJob(PreJob prejob) throws JclFactoryException {
		Jcl jcl = null;
		// prejob without type
		if (prejob.getJclType() == null){
			jcl = Factory.scanAllJclFactories(prejob);
		} else if (!Main.FACTORIES_LIST.containsKey(prejob.getJclType().toLowerCase())) {
			// JCL type is normalized using lower case
			throw new JclFactoryException(NodeMessage.JEMC143E.toMessage().getFormattedMessage(prejob.getJclType().toLowerCase()));
		} else {
			// get factory from map, loaded during startup of node
			JemFactory factory = Main.FACTORIES_LIST.get(prejob.getJclType().toLowerCase());
			// creates JCL object using the factory
			jcl = Factory.createJcl(prejob.getJclContent(), factory);
		}
		// sets JCL type
		jcl.setType(prejob.getJclType().toLowerCase());
		// sets JCL to JOB object, inside of PreJob container
		prejob.getJob().setJcl(jcl);
		prejob.getJob().setName(jcl.getJobName());

	}

	/**
	 * Creates a new JCL object by JCL factory, previously loaded
	 * 
	 * @see org.pepstock.jem.factories.JclFactory#createJcl(String)
	 * @param content JCL source code string
	 * @param factory JCLFactory
	 * @return JCL object
	 * @throws JclFactoryException the factory has an exception creating and
	 *             validating the JCL source
	 */
	private static Jcl createJcl(String content, JclFactory factory) throws JclFactoryException {
		return factory.createJcl(content);
	}
	
	/**
	 * Scans all factories because the PreJob ahs got JclType set to null.
	 * @param prejob prejob to check
	 * @return JCL instance
	 * @throws JclFactoryException if no factory is able to create the JCL
	 */
	private static Jcl scanAllJclFactories(PreJob prejob) throws JclFactoryException{
		for (JemFactory factory : Main.FACTORIES_LIST.values()){
			try {
				Jcl jcl =  Factory.createJcl(prejob.getJclContent(), factory);
				prejob.setJclType(factory.getType());
				return jcl;
				// Exception class must be caught 
			} catch (Exception e) {
            	// debug
            	LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
		throw new JclFactoryException(NodeMessage.JEMC143E.toMessage().getFormattedMessage("null"));
	}
}