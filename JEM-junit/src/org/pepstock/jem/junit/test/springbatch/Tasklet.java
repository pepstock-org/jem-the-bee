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
package org.pepstock.jem.junit.test.springbatch;


/**
 * 
 * @author Simone "Busy" Businaro
 *
 */
public class Tasklet extends SpringBatchTestCase {
	
	/**
	 * Copy a dataset
	 * 
	 * @throws Exception
	 */
	public void testDataSourceConnection() throws Exception {
		assertEquals(submit("tasklet/TEST_SPRINGBATCH_USE_DATASOURCE.xml"), 0);
	}
	
	/**
	 * Test launcher with main class
	 * @throws Exception
	 */
	public void testLauncherMain() throws Exception {
		assertEquals(submit("tasklet/TEST_SPRINGBATCH_LAUNCHER_MAIN.xml"), 0);
	}
	
	/**
	 * Test launcher with main class
	 * @throws Exception
	 */
	public void testLauncherMainAndContext() throws Exception {
		assertEquals(submit("tasklet/TEST_SPRINGBATCH_LAUNCHER_MAIN_CONTEXT.xml"), 0);
	}
	
	/**
	 * Launcher with Tasklet
	 * @throws Exception
	 */
	public void testLauncherTasklet() throws Exception {
		assertEquals(submit("tasklet/TEST_SPRINGBATCH_LAUNCHER_TASKLET.xml"), 0);
	}
	
	/**
	 * Test security by launcher
	 * @throws Exception
	 */
	public void testLauncherTaskletSecurity() throws Exception {
		assertEquals(submit("tasklet/TEST_SPRINGBATCH_LAUNCHER_TASKLET_SECURITY.xml"), 0);
	}
	
	/**
	 * Test security by launcher
	 * @throws Exception
	 */
	public void testLauncherTaskletAbend() throws Exception {
		assertEquals(submit("tasklet/TEST_SPRINGBATCH_LAUNCHER_TASKLET_ABEND.xml"), 1);
	}
}
