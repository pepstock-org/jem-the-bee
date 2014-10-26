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
package org.pepstock.jem.junit.test.jbpm;

import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.JemTestManager;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JBpmTestCase extends TestCase {

	/**
	 * 
	 */
	public JBpmTestCase() {
	}

	/**
	 * @param name
	 */
	public JBpmTestCase(String name) {
		super(name);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public final int submit(String name) throws Exception{
		Future<SubmitResult> future = JemTestManager.getSharedInstance().submit(getJcl(name), "jbpm", true,	false);
		SubmitResult sr = future.get();
		return sr.getRc();
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public final String getJcl(String name) {
		String ss = "jcls/" + name;
		System.err.println(ss);
		
		System.err.println(this.getClass().getResource("jcls"));
		
		
		return this.getClass().getResource("jcls/" + name).toString();
	}
}
