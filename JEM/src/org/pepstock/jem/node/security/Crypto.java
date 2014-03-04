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

package org.pepstock.jem.node.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.util.CharSet;

/**
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class Crypto {

	private static final String DESEDE_ALGORITHM = "DESede";
	
	/**
	 * To avoid any instantiation
	 */
	private Crypto() {
		
	}

	/**
	 * 
	 * @param pemKeyFile is the pem file of the RSA private key of the user.
	 * @param password the password of the private key if the private key is
	 *            protected by a password, null otherwise
	 * @return the private Key read from pem file
	 * @throws SecurityException if any Exception occurs while extracting private key
	 * @throws MessageException if any Exception occurs while extracting private key
	 * @throws KeyException 
	 */
	public static Key loadPrivateKeyFromFile(File pemKeyFile, String password) throws MessageException, KeyException {
		try {
			if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
				Security.addProvider(new BouncyCastleProvider());
			}
			// private key file in PEM format
			PEMParser pemParser = new PEMParser(new InputStreamReader(new FileInputStream(pemKeyFile), CharSet.DEFAULT));
			Object object = pemParser.readObject();
			pemParser.close();
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			KeyPair kp;
			if (object instanceof PEMEncryptedKeyPair) {
				if(password==null){
					throw new MessageException(NodeMessage.JEMC205E);
				}
				PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
				kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
			} else {
				LogAppl.getInstance().emit(NodeMessage.JEMC199W);
				kp = converter.getKeyPair((PEMKeyPair) object);
			}
			return kp.getPrivate();
		} catch (FileNotFoundException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (PEMException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param text to encrypt in byte
	 * @param key to use to encrypt text
	 * @return the encrypted text as an array of byte
	 * @throws KeyException if any error occurs during the process
	 */
	public static byte[] crypt(byte[] text, Key key) throws KeyException{
		try {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plaintext = text;
			return cipher.doFinal(plaintext);
		} catch (InvalidKeyException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new KeyException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param encryptText the text to endecrypt in byte
	 * @param key used to decrypt text
	 * @return the decrypted text as an array of byte
	 * @throws KeyException if any error occurs during the process
	 */
	public static byte[] decrypt(byte[] encryptText, Key key) throws KeyException {
		try {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(encryptText);
		} catch (InvalidKeyException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new KeyException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @return a secret TripleDES encryption/decryption key with 168 bit
	 * @throws NoSuchAlgorithmException
	 */
	public static SecretKey generateSymmetricKey() throws NoSuchAlgorithmException {
		// Get a key generator for Triple DES (a.k.a DESede)
		KeyGenerator keygen = KeyGenerator.getInstance(DESEDE_ALGORITHM);
		// set key size
		keygen.init(168);
		// Use it to generate a key
		return keygen.generateKey();
	}

}
