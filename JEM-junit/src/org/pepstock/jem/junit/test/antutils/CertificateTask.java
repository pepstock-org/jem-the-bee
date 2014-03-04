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
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class CertificateTask extends TestCase {

	/**
	 * Submit JCL one for import a certificate to the JEM user keystore and
	 * another one to remove it.
	 * 
	 * @throws Exception
	 */
	public void testCertificate() throws Exception {
		// import certificate
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_ANTUTILS_IMPORT_CERTIFICATE.xml"), "ant",
						true, false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
		// delete certificate
		future = JemTestManager.getSharedInstance().submit(
				getJcl("TEST_ANTUTILS_DELETE_ALIAS.xml"), "ant", true, false);
		sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	private String getJcl(String name) {
		return this.getClass().getResource("jcls/certificate/" + name)
				.toString();
	}
}
