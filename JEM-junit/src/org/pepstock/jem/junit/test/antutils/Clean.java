/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.junit.test.antutils;

import java.util.concurrent.Future;

import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.submitter.JemTestManager;

import junit.framework.TestCase;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class Clean extends TestCase{

	/**
	 * Clean dataset created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanDataset() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("stepjava/TEST_ANTUTILS_STEPJAVA_DELETE_DATA.xml"), "ant", true,
						false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	/**
	 * Clean roles created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanRoles() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("role/TEST_ANTUTILS_REMOVE_ROLE.xml"), "ant", true,
						false);
		future.get();
	}
	
	/**
	 * Clean resources created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanResources() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("resource/TEST_ANTUTILS_RESOURCES_REMOVE.xml"), "ant", true,
						false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}
	
	/**
	 * Clean jobs created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanJobs() throws Exception {
		Future<SubmitResult>future = JemTestManager.getSharedInstance()
				.submit(getJcl("archive/TEST_ANTUTILS_ARCHIVE.xml"), "ant", true,
						false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}
		
	private String getJcl(String name) {
		return this.getClass().getResource("jcls/" + name).toString();
	}
}
