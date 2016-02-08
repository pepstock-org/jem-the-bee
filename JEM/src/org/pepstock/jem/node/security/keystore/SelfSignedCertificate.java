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
package org.pepstock.jem.node.security.keystore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.pepstock.jem.util.TimeUtils;


/**
 * Generate X.509 certificates programmatically leveraging on Bouncycastle lightweight API.
 * <br>
 * list of DN attributes:<br>
 * CN: CommonName<br>
 * OU: OrganizationalUnit<br>
 * O: Organization<br>
 * L: Locality<br>
 * S: StateOrProvinceName<br>
 * C: CountryName<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */

public final class SelfSignedCertificate {

	// alias for keystore
    private static final String CERTIFICATE_ALIAS = "JEM-HTTP";
    // uses RSA
    private static final String CERTIFICATE_ALGORITHM = "RSA";
    // DN for certificate
    private static final String CERTIFICATE_DN = "CN=jem, O=pepstock, L=verona, S=vr, C=it";
    // uses 1024 bit
    private static final int CERTIFICATE_BITS = 1024;
    
    private static final long TEN_YEARS = 10 * TimeUtils.DAY * 365;
    
    static {
        // adds the Bouncy castle provider to java security
        Security.addProvider(new BouncyCastleProvider());
    }
    
    /**
     * To avoid any instantiation
     */
    private SelfSignedCertificate(){	
    }
    
    /**
     * Returns X.509 certificates programmatically leveraging on Bouncycastle lightweight API
     * @param password password of keystore
     * @return certificate in byte array format
     * @throws KeyStoreException if any error occurs
     * @throws NoSuchAlgorithmException if any error occurs
     * @throws CertificateException if any error occurs
     * @throws IOException if any error occurs
     * @throws OperatorCreationException if any error occurs
     */
	public static ByteArrayInputStream getCertificate(String password) throws NoSuchAlgorithmException, OperatorCreationException, CertificateException, KeyStoreException, IOException {
		// creates a key pair generator
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CERTIFICATE_ALGORITHM);
		keyPairGenerator.initialize(CERTIFICATE_BITS, new SecureRandom());
		// creates a couple of keys
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// GENERATE THE X509 CERTIFICATE
		X500Principal principal = new X500Principal(CERTIFICATE_DN);
		// sets times of certificate
		// starting from now
		long now = System.currentTimeMillis();
		Date notBefore = new Date(now - TimeUtils.DAY);
		Date notAfter = new Date(now + TEN_YEARS);
		BigInteger serial = BigInteger.valueOf(now);

		// creaets a builder
		X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(principal, serial, notBefore, notAfter, principal, keyPair.getPublic());
		// creates the content signed by private key
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(keyPair.getPrivate());
		// build certificate and convert in JCA
		X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certGen.build(sigGen));
		return createKeyStore(cert, keyPair.getPrivate(), password);
	}

    /**
     * Creates a key store to save the certificate and then to use
     * @param cert certificate to add to key store 
     * @param key private key
     * @return a array of bytes of key store
     * @throws KeyStoreException if any error occurs
     * @throws NoSuchAlgorithmException if any error occurs
     * @throws CertificateException if any error occurs
     * @throws IOException if any error occurs
     */
    private static ByteArrayInputStream createKeyStore(X509Certificate cert, PrivateKey key, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance("JKS");    
        // creates an empty keystore
        keyStore.load(null, null);
        // adds certificate
        keyStore.setKeyEntry(CERTIFICATE_ALIAS, key, password.toCharArray(),  new java.security.cert.Certificate[]{cert});
        // stores the key store in bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        keyStore.store(baos, password.toCharArray());
        return new ByteArrayInputStream(baos.toByteArray());
    }
}