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
package org.pepstock.jem.jppf;

import java.io.IOException;
import java.io.InputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.jppf.utils.TypedProperties;
import org.pepstock.jem.log.LogAppl;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public final class JPPFUtil {

	/**
	 * To avoid any instantiation
	 */
	private JPPFUtil() {
	}
	
	/**
	 * Try to load JPPF configuration from data description
	 * @param ic JNDI initial context to get input stream
	 * @return JPPF properties
	 */
	public static TypedProperties loadTypedPropertiesFromDataDescription(InitialContext ic){
		// try to load configuration from data description
		// if data description is allocated
		TypedProperties propsFromDataDescritpion = new TypedProperties();
		// gets input stream
		try {
			Object ddconfig = (Object) ic.lookup(Keys.JPPF_CONFIG_DATADESCRIPTION);
			InputStream configInputStream = (InputStream) ddconfig;
			// loads properties
			propsFromDataDescritpion.load(configInputStream);

		} catch (NamingException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
		} catch (IOException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
		}
		return propsFromDataDescritpion;
	}
	
	/**
	 * Parses address, port and load JPPF properties to connect to JPPF.
	 * @param props JPPF properties to load
	 * @param addressParm address to parse
	 * @throws JPPFMessageException if address is not valid
	 */
	public static void loadTypedProperties(TypedProperties props, String addressParm) throws JPPFMessageException{
		// parses addresses (comma separated)
		String address = StringUtils.remove(addressParm, " ");
		String[] addresses = StringUtils.split(address, ",");
		if (addresses != null && addresses.length > 0){
			// calculate JPPF.DRIVERS properties
			StringBuilder drivers = new StringBuilder();
			for (int i=0; i<addresses.length; i++){
				// parses address. FORMAT: [host|ipaddress]:[port]
				if (addresses[i].contains(":")){
					String host = StringUtils.substringBefore(addresses[i], ":");
					String port = StringUtils.substringAfter(addresses[i], ":");
					
					// drivers is named with counter
					String driver = Keys.JEM_JPPF_DRIVER_PREFIX+i;

					props.setProperty(driver+Keys.JEM_JPPF_PORT_SUFFIX, port);
					props.setProperty(driver+Keys.JEM_JPPF_SERVER_SUFFIX, host);
					// drivers are defined blank separated
					drivers.append(driver).append(" ");
				} else {
					throw new JPPFMessageException(JPPFMessage.JEMJ008E, addresses[i]);				
				}
			}
			// sets drivers
			props.setProperty(Keys.JEM_JPPF_DRIVERS, drivers.toString());
		}
	}
}
