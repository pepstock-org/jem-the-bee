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
package org.pepstock.jem.junit.test.ftp.java;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.Registry;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class UseFTP {
	
//	@AssignDataSource("localhost")
//	private static Ftp INSTANCE = null;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NamingException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws IOException, NamingException, NotBoundException {
		if (args != null){
			if (args.length > 0){
				for (int i=0; i<args.length; i++){
					System.err.println("ARG["+i+"]="+args[i]);
				}
			}
		}
		
//		InitialContext ic = ContextUtils.getContext();
//		Ftp ftp = (Ftp)ic.lookup("localhost");
//		ftp.retrieveFile("Action.java", System.out);
//		ftp.disconnect();
//		
//		INSTANCE.retrieveFile("Action.java", System.out);
//		INSTANCE.disconnect();
		
		InitialContext ic = ContextUtils.getContext();
		Registry reg = (Registry)ic.lookup("localhost");
		
		System.err.println(reg.lookup("jem"));
		
	}

}
