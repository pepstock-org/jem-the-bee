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
package org.pepstock.jem.gwt.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.pepstock.jem.gwt.client.panels.administration.certificates.CertificateAdder;
import org.pepstock.jem.gwt.client.services.CertificateAdderManagerService;
import org.pepstock.jem.gwt.server.services.CertificatesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Is GWT server service which can provide methods to add new certificate for a user.<br>
 * This doesn't implement the usual method because MultiPart is not well supported by RPC of GWT.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class CertificateAdderManagerServiceImpl extends FileUploadManager implements CertificateAdderManagerService {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.FileUploadManager#loaded(java.util.List)
	 */
    @Override
    public String loaded(List<FileItem> items) throws JemException {
    	try {
	        CertificatesManager manager = new CertificatesManager();
	        
	        byte[] certificate = null;
	        String alias = null;
	        
	        // scans all files uploaded
	        for (FileItem item : items){
	        	// works only with field of JEM
	        	// other files are ignored
	        	if (item.getFieldName().equalsIgnoreCase(CertificateAdder.FILE_UPLOAD_FIELD)){
	        		// 
	        		// reads certificate uploaded
	        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        		IOUtils.copy(item.getInputStream(), baos);
	        		certificate = baos.toByteArray();

	        	} else if (item.getFieldName().equalsIgnoreCase(CertificateAdder.ALIAS_FIELD)){
	        		// reads Alias
	        		alias = item.getString();
	        	}
	        }

	        // checks is alias and certificate are correct
	        if (alias != null){
	        	if (certificate != null){
	        		try {
	        			// adds them!
	                    manager.addCertificate(certificate, alias);
	                } catch (Exception e) {
	                	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG051E, e, e.getMessage());
	                	throw new JemException(UserInterfaceMessage.JEMG051E.toMessage().getFormattedMessage(e.getMessage()));
	                }
	        	} else {
	        		throw new JemException(UserInterfaceMessage.JEMG050E.toMessage().getFormattedMessage(CertificateAdder.FILE_UPLOAD_FIELD));
	        	}
	        } else {
	        	throw new JemException(UserInterfaceMessage.JEMG051E.toMessage().getFormattedMessage(CertificateAdder.ALIAS_FIELD));
	        }
	        // returns message to show even if ends correctly
	        return UserInterfaceMessage.JEMG052I.toMessage().getFormattedMessage(alias);
        } catch (IOException e) {
        	LogAppl.getInstance().ignore(e.getMessage(), e);
        	throw new JemException(UserInterfaceMessage.JEMG051E.toMessage().getFormattedMessage(e.getMessage()));
    	}
    }

}