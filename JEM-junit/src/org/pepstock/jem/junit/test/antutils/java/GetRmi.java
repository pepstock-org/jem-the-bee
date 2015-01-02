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

import java.rmi.registry.Registry;

import org.pepstock.jem.annotations.AssignDataSource;

/**
 * This class will show an example of how to use a JEM datasource
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class GetRmi {

	@AssignDataSource("JUNIT-RMI-RESOURCE")
	private static Registry registry = null;
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.err.println();
		System.err.println("*** RMI");
		String[] li = registry.list();
		if (li != null && li.length>0){
			for (int i=0; i<li.length; i++){
				System.err.println(li[i]);
			}
		} else {
			System.err.println("No rmi objects");
		}
		
		if (args != null && args.length > 0){
			registry.unbind("test");
		}
	}
}
