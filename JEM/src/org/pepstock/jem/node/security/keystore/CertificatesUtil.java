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
package org.pepstock.jem.node.security.keystore;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.CertificateEntry;

import com.hazelcast.core.ILock;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class CertificatesUtil {
	
	/**
	 * Certificate type 
	 */
	public static final String X509_CERTIFICATE_TYPE = "X.509";
	
	/**
	 * To avoid any instantiation
	 */
	private CertificatesUtil() {
		
	}

	/**
	 * 
	 * @return the list of certificate entries relative to the user keystore
	 * @throws CertificateException
	 * @throws KeyStoreException 
	 */
	public static List<CertificateEntry> getCertificates() throws CertificateException, KeyStoreException {
		ILock lock = Main.getHazelcast().getLock(Queues.KEYSTORE_LOCK);
		try{
			lock.lock();
			KeyStoreInfo info = KeyStoreUtil.getKeyStoresInfo().getUserKeystoreInfo();
			return getCertificates(info);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 
	 * @param certificate
	 * @param certificateAlias
	 * @throws CertificateException
	 * @throws KeyStoreException 
	 */
	public static void addCertificate(byte[] certificate, String certificateAlias) throws CertificateException, KeyStoreException {
		ILock lock = Main.getHazelcast().getLock(Queues.KEYSTORE_LOCK);
		try{
			lock.lock();
			KeyStoreInfo info = KeyStoreUtil.getKeyStoresInfo().getUserKeystoreInfo();
			KeyStore keystore = KeyStoreUtil.getKeystore(info);
			if (!keystore.containsAlias(certificateAlias)){
				insertCertificate(keystore, info, certificate, certificateAlias);
			} else {
				throw new CertificateException("Alias '"+certificateAlias+"' is already present");
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @param certificate
	 * @param certificateAlias
	 * @throws CertificateException
	 * @throws KeyStoreException 
	 */
	public static void removeCertificate(String certificateAlias) throws CertificateException, KeyStoreException {
		ILock lock = Main.getHazelcast().getLock(Queues.KEYSTORE_LOCK);
		try{
			lock.lock();
			KeyStoreInfo info = KeyStoreUtil.getKeyStoresInfo().getUserKeystoreInfo();
			KeyStore keystore = KeyStoreUtil.getKeystore(info);
			if (keystore.containsAlias(certificateAlias)){
				removeAlias(keystore, info, certificateAlias);
			} else {
				throw new CertificateException("Alias '"+certificateAlias+"' is not present");
			}
		} finally {
			lock.unlock();
		}
	}


	/**
	 * Insert a certificate to the key store and verify if the certificate is an
	 * X.509 or has expired. If so throw an exception
	 * 
	 * @param certificate the certificate in byte array. Remember that must be
	 *            an X.509 certificate
	 * @param certificateAlias the certificate alias
	 * @param keystoreFile
	 * @param keystoreBackupFile 
	 * @param keystorePwd
	 * @throws KeyStoreException 
	 * @throws Exception if any exception occurs
	 */
	private static void insertCertificate(KeyStore keystore, KeyStoreInfo info, byte[] certificate, String certificateAlias) throws CertificateException, KeyStoreException {
		ByteArrayInputStream fis = new ByteArrayInputStream(certificate);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
		Certificate cert = cf.generateCertificate(bis);
		if (!cert.getType().equals(X509_CERTIFICATE_TYPE)) {
			throw new CertificateException(NodeMessage.JEMC200E.toMessage().getFormattedMessage(cert.getType()));
		}
		// verify expiration date
		Date expirationDate = ((X509Certificate) cert).getNotAfter();
		if (expirationDate.before(new Date())) {
			throw new CertificateException(NodeMessage.JEMC201E.toMessage().getFormattedMessage(certificateAlias, expirationDate));
		}
		keystore.setCertificateEntry(certificateAlias, cert);
		KeyStoreUtil.save(keystore, info);
	}

	/**
	 * 
	 * @param keystoreFile
	 * @param keystorePwd
	 * @return the list of X509 Certificate present in the keystore
	 * @throws KeyStoreException 
	 * @throws Exception if any exception occurs
	 */
	private static List<CertificateEntry> getCertificates(KeyStoreInfo info) throws CertificateException, KeyStoreException {
		KeyStore keystore = KeyStoreUtil.getKeystore(info);
		Enumeration<String> aliases = keystore.aliases();
		List<CertificateEntry> listCerts = new ArrayList<CertificateEntry>();
		while (aliases.hasMoreElements()) {
			String currAlias = aliases.nextElement();
			Certificate cert = keystore.getCertificate(currAlias);
			CertificateEntry keystoreEntry = new CertificateEntry();
			keystoreEntry.setAlias(currAlias);
			if (cert != null && cert.getType().equals(X509_CERTIFICATE_TYPE)) {
				X509Certificate certificate = (X509Certificate) cert;
				keystoreEntry.setIssuer(certificate.getIssuerDN().getName());
				keystoreEntry.setSubject(certificate.getSubjectDN().getName());
				keystoreEntry.setNotBefore(certificate.getNotBefore());
				keystoreEntry.setNotAfter(certificate.getNotAfter());
			}
			listCerts.add(keystoreEntry);
		}
		return listCerts;
	}

	/**
	 * Remove alias from key store
	 * 
	 * @param keystoreFile
	 * @param keystoreBackupFile 
	 * @param keystorePwd
	 * @param alias
	 * @throws CertificateException 
	 * @throws KeyStoreException 
	 */
	private static void removeAlias(KeyStore keystore, KeyStoreInfo info, String alias) throws CertificateException, KeyStoreException {
		keystore.deleteEntry(alias);
		KeyStoreUtil.save(keystore, info);
	}
}
