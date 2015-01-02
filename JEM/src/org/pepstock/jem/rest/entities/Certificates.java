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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.node.security.CertificateEntry;

/**
 * POJO container of certificates data.<br>
 * Is used for CERTIFICATES REST service.<br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
@XmlRootElement
public class Certificates extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;

	private Collection<CertificateEntry> entries = null;
	
	private byte[] certificate = null;
	
	private String alias = null;
	
	/**
	 * Empty constructor
	 */
	public Certificates() {
	}

	/**
	 * @return the entries
	 */
	public Collection<CertificateEntry> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(Collection<CertificateEntry> entries) {
		this.entries = entries;
	}

	/**
	 * @return the certificate
	 */
	public byte[] getCertificate() {
		return certificate;
	}

	/**
	 * @param certificate the certificate to set
	 */
	public void setCertificate(byte[] certificate) {
		System.arraycopy(certificate, 0, this.certificate, 0, certificate.length);
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Certificates [entries=" + entries + ", certificate=" + Arrays.toString(certificate) + ", alias=" + alias + "]";
	}

}