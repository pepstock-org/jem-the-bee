/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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

import java.io.Serializable;
import java.util.Date;

/**
 * Entity which contains all data related yo a certificate
 * 
 * @author Simone "Busy" Businaro
 *
 */
public class CertificateEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String alias = null;
	
	private String issuer = null;
	
	private String subject = null;
	
	private Date notBefore = null;
	
	private Date notAfter = null;

	/**
	 * Returns alias
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets alias
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Returns issuer
	 * @return the issuer
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * Sets issuer
	 * @param issuer the issuer to set
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	/**
	 * Returns subject
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets subject
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns not before activation date
	 * @return the notBefore
	 */
	public Date getNotBefore() {
		return notBefore;
	}

	/**
	 * Sets not before activation date
	 * @param notBefore the notBefore to set
	 */
	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	/**
	 * Returns not after validation date
	 * @return the notAfter
	 */
	public Date getNotAfter() {
		return notAfter;
	}

	/**
	 * Sets not after validation date
	 * @param notAfter the notAfter to set
	 */
	public void setNotAfter(Date notAfter) {
		this.notAfter = notAfter;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CertificateEntry [alias=" + alias + ", issuer=" + issuer + ", subject=" + subject + ", notBefore=" + notBefore + ", notAfter=" + notAfter + "]";
	}
	
	
	
}
