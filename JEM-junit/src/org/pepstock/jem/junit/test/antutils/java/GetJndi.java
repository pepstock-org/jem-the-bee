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
package org.pepstock.jem.junit.test.antutils.java;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.StringRefAddr;

import org.pepstock.jem.annotations.AssignDataSource;
import org.pepstock.jem.jppf.DataStreamNameClassPair;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;

/**
 * This class will show an example of how to use a JEM datasource
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class GetJndi {

	
	@AssignDataSource("JUNIT-JNDI-RESOURCE")
	private static InitialContext context = null;

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.err.println("*** JNDI");
		NamingEnumeration<NameClassPair> list = context.list("");
		while(list.hasMore()){
			NameClassPair pair = list.next();
			// checks if is datastream
			// only datastreams are changed
			if (pair instanceof DataStreamNameClassPair){
				DataStreamNameClassPair dsPair = (DataStreamNameClassPair) pair;
				DataStreamReference prevReference = (DataStreamReference)dsPair.getObject();
				// gets data description XML defintion
				// adding it to a new reference, for remote access
				StringRefAddr sra = (StringRefAddr) prevReference.get(StringRefAddrKeys.DATASTREAMS_KEY);
				System.err.println(sra.getContent());
			}
		}
		System.err.println(context.getEnvironment());
		if (args != null && args.length > 0){
			context.unbind("test");
		}
	}
}
