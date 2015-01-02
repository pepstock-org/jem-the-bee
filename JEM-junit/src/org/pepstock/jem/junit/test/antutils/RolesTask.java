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
public class RolesTask extends AntTestCase {

	/**
	 * Test the creation of a new role, the revokation of a permission from a
	 * role and the removal of a role
	 * 
	 * @throws Exception
	 */
	public void testRole() throws Exception {
		assertEquals(submit("role/TEST_ANTUTILS_CREATE_ROLE.xml"), 0);
		assertEquals(submit("role/TEST_ANTUTILS_REVOKE_PERMISSION.xml"), 0);
	}

}
