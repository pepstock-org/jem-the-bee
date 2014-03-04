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

import junit.framework.TestCase;

import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.submitter.JemTestManager;

/**
 * 
 * @author Simone "Busy" Businaro
 *
 */
public class GDGTask extends TestCase {

	/**
	 * Define a gdg
	 * 
	 * @throws Exception
	 */
	public void testGdgDefine() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_ANTUTILS_GDG_DEFINE.xml"), "ant",
						true, false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	/**
	 * delete a gdg
	 * 
	 * @throws Exception
	 */
	public void testGdgDelete() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_ANTUTILS_GDG_DELETE.xml"), "ant",
						true, false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	/**
	 * Rebuild a gdg
	 * 
	 * @throws Exception
	 */
	public void testGdgRebuild() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_ANTUTILS_GDG_REBUILD.xml"), "ant",
						true, false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testGdgRename() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_ANTUTILS_GDG_RENAME.xml"), "ant",
						true, false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testGdgCopy() throws Exception {
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_ANTUTILS_GDG_COPY.xml"), "ant",
						true, false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private String getJcl(String name) {
		return this.getClass().getResource("jcls/gdg/"+name).toString();
	}
}
