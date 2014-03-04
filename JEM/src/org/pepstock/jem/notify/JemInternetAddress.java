/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.notify;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Jem class for Internet address.
 * It extends {@link InternetAddress}. The only constructor as default
 * uses strict check on the email syntax.
 * 
 * @see InternetAddress
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public class JemInternetAddress extends InternetAddress{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. Builds a <code>JemInternetAddress</code> starting from
	 * an Internet address using strict check on the email syntax.
	 * 
	 * @param address the starting Internet address used to build the <code>JemInternetAddress</code>.
	 * @throws AddressException: exception throwed when the Internet address used to build
	 * <code>JemInternetAddress</code> has an incorrect syntax.
	 */
	public JemInternetAddress(String address) throws AddressException {
		super(address, true);
	}	
}