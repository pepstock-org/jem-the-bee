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
package org.pepstock.jem.junit.test.antutils;


/**
 * 
 * @author Simone "Busy" Businaro
 *
 */
public class GDGTask extends AntTestCase {

	/**
	 * Define a gdg
	 * 
	 * @throws Exception
	 */
	public void testGdgDefine() throws Exception {
		assertEquals(submit("gdg/TEST_ANTUTILS_GDG_DEFINE.xml"), 0);
	}

	/**
	 * delete a gdg
	 * 
	 * @throws Exception
	 */
	public void testGdgDelete() throws Exception {
		assertEquals(submit("gdg/TEST_ANTUTILS_GDG_DELETE.xml"), 0);
	}

	/**
	 * Rebuild a gdg
	 * 
	 * @throws Exception
	 */
	public void testGdgRebuild() throws Exception {
		assertEquals(submit("gdg/TEST_ANTUTILS_GDG_REBUILD.xml"), 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testGdgRename() throws Exception {
		assertEquals(submit("gdg/TEST_ANTUTILS_GDG_RENAME.xml"), 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testGdgCopy() throws Exception {
		assertEquals(submit("gdg/TEST_ANTUTILS_GDG_COPY.xml"), 0);
	}
}
