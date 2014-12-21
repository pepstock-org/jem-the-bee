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
package org.pepstock.jem.node.executors.certificates;

import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import org.pepstock.jem.io.BytesArray;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.security.keystore.CertificatesUtil;

/**
 * Adds a new user certificate into JEM. The certificate is necessary when JEM is configured with 
 * socket interceptor and all submitter must use a certificate to authenticate themselves to JEM.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class AddCertificate extends DefaultExecutor<Boolean> {

	private static final long serialVersionUID = 1L;

	private String userid = null;
	
	private BytesArray certificate = null;

	/**
	 * Constructs using certificate (in byte format) and 
	 * user id (is consider the alias in keystore)
	 * 
	 * @param userid user id  
	 * @param certificate certificate in byte format 
	 */
	public AddCertificate(BytesArray certificate, String userid) {
		this.userid = userid;
		this.certificate = certificate;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		try {
			// adds certificate, relating to user id
			CertificatesUtil.addCertificate(certificate.toByteArray(), userid);
			// returns always TRUE
			return Boolean.TRUE;
		} catch (CertificateException e) {
			throw new ExecutorException(NodeMessage.JEMC239E, e);
		} catch (KeyStoreException e) {
			throw new ExecutorException(NodeMessage.JEMC239E, e);
		}
	}
}
