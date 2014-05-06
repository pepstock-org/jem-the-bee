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
package org.pepstock.jem.junit.test.jppf;

import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.JemTestManager;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class Create extends TestCase{
	
	
	/**
	 * Clean dataset created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCreateDataset() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_JPPF_FILES_CREATE.xml"), "ant", true,
						false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	
	
	/**
	 * Clean roles created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCreateJDBC() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_JPPF_CREATE_JEM_JDBC_DATA_SOURCE.xml"), "ant", true,
						false);
		future.get();
	}
	
	
	
	/**
	 * Clean resources created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCreateResource() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_JPPF_RESOURCE_CREATE.xml"), "ant", true,
						false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}
	
	private String getJcl(String name) {
		return this.getClass().getResource("jcls/" + name).toString();
	}

}
