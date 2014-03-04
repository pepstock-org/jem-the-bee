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
package org.pepstock.jem.gwt.client.panels.administration.certificates;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.security.CertificateEntry;


/**
 * Is the column comparator to sort cell table for table with certificates
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class CertificateEntriesComparator extends IndexedColumnComparator<CertificateEntry> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public CertificateEntriesComparator(int index) {
		super(index);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CertificateEntry o1, CertificateEntry o2) {
		int diff = 0;
		switch(getIndex()){
			case 1: 
				// sorts by alias
				diff = o1.getAlias().compareTo(o2.getAlias());
				break;
			case 2: 
				// sorts by issuer
				diff = o1.getIssuer().compareTo(o2.getIssuer());
				break;
			case 3: 
				// sorts by subject
				diff = o1.getSubject().compareTo(o2.getSubject());
				break;
			case 4: 
				// sorts by not before
				diff = o1.getNotBefore().compareTo(o2.getNotBefore());
				break;
			case 5: 
				// sorts by not after 
				diff = o1.getNotAfter().compareTo(o2.getNotAfter());
				break;
			default:
				// sorts by alias
				diff = o1.getAlias().compareTo(o2.getAlias());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}