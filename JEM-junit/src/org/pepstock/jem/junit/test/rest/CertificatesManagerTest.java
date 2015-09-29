/**
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pepstock.jem.junit.test.rest;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.node.security.CertificateEntry;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CertificatesManagerTest extends TestCase {
	
	private static final String ALIAS = "testAlias";

	public void testGetAllCertificates() throws Exception {
		RestManager.getInstance().getCertificatesManager().getCertificates("*");
	}
	
	public void testAddCertificate() throws Exception {
		File cert = getJcl("certificate.xml");
		byte[] certificate = IOUtils.toByteArray(new FileInputStream(cert));
		boolean added = RestManager.getInstance().getCertificatesManager().addCertificates(certificate, ALIAS);
		assertEquals(added, true);
		Collection<CertificateEntry> all = RestManager.getInstance().getCertificatesManager().getCertificates(ALIAS);
		if (all != null && !all.isEmpty()){
			for (CertificateEntry entry : all){
				if (entry.getAlias().equalsIgnoreCase(ALIAS)){
					return;
				}
			}
		}
		throw new Exception("Unable to add "+ALIAS);
	}			
			
	public void testRemoveCertificate() throws Exception{		
		boolean removed = RestManager.getInstance().getCertificatesManager().removeCertificates(ALIAS);
		assertEquals(removed, true);
		Collection<CertificateEntry> all = RestManager.getInstance().getCertificatesManager().getCertificates(ALIAS);
		if (all != null && !all.isEmpty()){
			for (CertificateEntry entry : all){
				if (entry.getAlias().equalsIgnoreCase(ALIAS)){
					throw new Exception("Unbale to remove "+ALIAS);
				}
			}
		}
	}
	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
