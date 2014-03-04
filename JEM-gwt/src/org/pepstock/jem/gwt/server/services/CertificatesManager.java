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
package org.pepstock.jem.gwt.server.services;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.io.BytesArray;
import org.pepstock.jem.node.executors.certificates.AddCertificate;
import org.pepstock.jem.node.executors.certificates.GetCertificates;
import org.pepstock.jem.node.executors.certificates.RemoveCertificate;
import org.pepstock.jem.node.security.CertificateEntry;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;

import com.hazelcast.core.Member;

/**
 * This service manages all roles and their authorizations and users relations inside of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CertificatesManager extends DefaultService{

	/**
	 * Returns a collection of certificates, by a filter (a set of key-values).
	 * 
     * @param filterParm string which contains a set of key-values
     * @return a collection of certificates, matching the filter
	 * @throws ServiceMessageException 
     * @throws Exception if any exception occurs
     */
    public Collection<CertificateEntry> getCertificates(String filterParm) throws ServiceMessageException  {
		// checks if the user is authorized to read aliases of certificate
		// if not, this method throws an exception
    	String filter = filterParm;
    	checkAuthorization(new StringPermission(Permissions.CERTIFICATES_READ));
    	if ("*".equals(filter)){
    		filter = "";
    	} else if (filter.endsWith("*")){
    		filter = StringUtils.substringBeforeLast(filter, "*");
    	} 
		DistributedTaskExecutor<Collection<CertificateEntry>> task = new DistributedTaskExecutor<Collection<CertificateEntry>>(new GetCertificates(filter), getMember());
		return task.getResult();
    }


    /**
     * Adds a new certificate in JEM.
     * 
     * @param certificate 
     * @param alias 
     * @return always true
     * @throws ServiceMessageException 
     * @throws Exception if any exception occurs
     */
    public Boolean addCertificate(byte[] certificate, String alias) throws ServiceMessageException  {
		// checks if the user is authorized to create certificates
		// if not, this method throws an exception    	
    	checkAuthorization(new StringPermission(Permissions.CERTIFICATES_CREATE));
    	BytesArray ba = new BytesArray();
    	ba.write(certificate, 0, certificate.length);
    	
		DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(new AddCertificate(ba, alias), getMember());
		return task.getResult();
    }

    /**
     * Removes a collection of certificates from JEM.
     * 
     * @param entries collection of certificates to be removed 
     * @return <code>false</code> if is not able to remove at least 1 certificate
     * @throws ServiceMessageException if any exception
     */
    public Boolean removeCertificate(Collection<CertificateEntry> entries) throws ServiceMessageException {
		// checks if the user is authorized to delete certificates
		// if not, this method throws an exception
    	checkAuthorization(new StringPermission(Permissions.CERTIFICATES_DELETE));
    	Member member = getMember();
    	
    	boolean result = true;
		// scans all certificates
		for (CertificateEntry entry : entries){
			DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(new RemoveCertificate(entry.getAlias()), member);
			result = task.getResult() && result;
		}
    	return result;
    }

}