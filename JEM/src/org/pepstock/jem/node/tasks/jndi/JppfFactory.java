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
package org.pepstock.jem.node.tasks.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.jppf.utils.TypedProperties;
import org.pepstock.jem.jppf.JPPFMessage;
import org.pepstock.jem.jppf.JPPFMessageException;
import org.pepstock.jem.jppf.JPPFUtil;
import org.pepstock.jem.jppf.Keys;
import org.pepstock.jem.node.resources.JppfResource;

/**
 * JNDI factory to create typedProperties to lod on JPPFConfiguration
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class JppfFactory implements ObjectFactory {

	
	
	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context nameCtx, Hashtable<?, ?> environment) throws JNDIException {
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}

		String addresses = null;
		Reference ref = (Reference) object;
		RefAddr ra = ref.get(JppfResource.ADDRESSES);
		if (ra != null) {
			addresses = ra.getContent().toString().trim();
		} else {
			throw new JNDIException(JPPFMessage.JEMJ009E, JppfResource.ADDRESSES);
		}
		return createTypeProperties(addresses);
	}

	/**
	 * Creates a typed properties parsing the addresses, comma separated
	 * 
	 * @param addressParm list addresses comma separated
	 * @return typed properties for JPPF connection
	 * @throws JNDIException if an error occurs 
	 */
	public static Object createTypeProperties(String addressParm) throws JNDIException {
		TypedProperties props = new TypedProperties();
		
		/*----------------------+
		 | Load JPPF properties | 
		 +----------------------*/
		props.setProperty(Keys.JEM_JPPF_DISCOVERY_ENABLED, Boolean.FALSE.toString());
		
		if (addressParm != null){
			try {
				JPPFUtil.loadTypedProperties(props, addressParm);
			} catch (JPPFMessageException e) {
				throw new JNDIException(JPPFMessage.JEMJ008E, e, addressParm);
			}
		}
		return props;
	}
}