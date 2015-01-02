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
package org.pepstock.jem.node.tasks.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.pepstock.jem.node.NodeMessage;

import com.thoughtworks.xstream.XStream;

/**
 * JNDI object factory. It returns ALWAYS the DataPathContainer
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 * 
 */
public class DataPathsFactory implements ObjectFactory {
	
	private XStream xstream = new XStream();

	/**
	 * Searches inside the reference the data paths container object,
	 * serialized in XML format.<br>
	 * 
	 * @param object - The possibly null object containing location or reference
	 *            information that can be used in creating an object.
	 * @param name The name of this object relative to nameCtx, or null if no
	 *            name is specified.
	 * @param ctx The context relative to which the name parameter is specified,
	 *            or null if name is relative to the default initial context.
	 * @param env The possibly null environment that is used in creating the
	 *            object.
	 * @return a stream to use to write or read
	 * @throws Exception occurs if there is any error
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JNDIException {
		// checks if object passed is a Reference
		if (object instanceof Reference) {
			Reference reference = (Reference) object;

			// get XML string representation of datapaths impl,
			// wrapped by a data stream
			RefAddr datastreamsAddr = reference.get(StringRefAddrKeys.DATAPATHS_KEY);

			// creates data paths container object using XStream (used to
			// serialize)
			return xstream.fromXML((String) datastreamsAddr.getContent());
		}
		// if we arrive here, object is not a reference
		throw new JNDIException(NodeMessage.JEMC133E, object.getClass().getName());
	}

}