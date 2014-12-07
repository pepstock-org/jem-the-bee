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
package org.pepstock.jem.junit.test.antutils;


/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class Clean extends AntTestCase{

	/**
	 * Clean dataset created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanDataset() throws Exception {
		assertEquals(submit("stepjava/TEST_ANTUTILS_STEPJAVA_DELETE_DATA.xml"), 0);
	}

	/**
	 * Clean roles created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanRoles() throws Exception {
		assertEquals(submit("role/TEST_ANTUTILS_REMOVE_ROLE.xml"), 0);
	}
	
	/**
	 * Clean resources created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanResources() throws Exception {
		assertEquals(submit("resource/TEST_ANTUTILS_RESOURCES_REMOVE.xml"), 0);
	}
	
	/**
	 * Clean jobs created by the last junit run
	 * 
	 * @throws Exception
	 */
	public void testCleanJobs() throws Exception {
		assertEquals(submit("archive/TEST_ANTUTILS_ARCHIVE.xml"), 0);
	}
}
