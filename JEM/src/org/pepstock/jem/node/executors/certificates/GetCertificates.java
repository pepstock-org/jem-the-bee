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
package org.pepstock.jem.node.executors.certificates;

import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.security.CertificateEntry;
import org.pepstock.jem.node.security.keystore.CertificatesUtil;

/**
 * Returns a list of already defined certificates inside of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class GetCertificates extends DefaultExecutor<Collection<CertificateEntry>> {

	private static final long serialVersionUID = 1L;

	private String filter = null;

	/**
	 * Constructs the object using the filter to apply to get the list
	 * @param filter filter to get a subset (or whole) list
	 * 
	 */
	public GetCertificates(String filter) {
		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public Collection<CertificateEntry> execute() throws ExecutorException {
		try {
			// creates reuslt list
			List<CertificateEntry> result = new ArrayList<CertificateEntry>();
			// gets all piblished certificates 
			List<CertificateEntry> list = CertificatesUtil.getCertificates();
			// scans to filter
			for (CertificateEntry entry : list) {
				// scans only certificate with an issuer
				// filter by alias (then userid)
				if (entry.getIssuer() != null && ("".equals(filter) || StringUtils.containsIgnoreCase(entry.getAlias(), filter))) {
					result.add(entry);
				}
			}
			return result;
		} catch (CertificateException e) {
			throw new ExecutorException(NodeMessage.JEMC239E, e);
		} catch (KeyStoreException e) {
			throw new ExecutorException(NodeMessage.JEMC239E, e);
		}
	}

}
