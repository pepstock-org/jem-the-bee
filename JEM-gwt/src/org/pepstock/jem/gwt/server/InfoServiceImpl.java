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
package org.pepstock.jem.gwt.server;

import javax.servlet.ServletContext;

import org.pepstock.jem.gwt.client.services.InfoService;
import org.pepstock.jem.gwt.server.services.InternalsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.About;

/**
 * Is GWT server service which can provide general information to use on headers and
 * logo management. <br>
 * This service doesn't check any authentication or authorization because is provides 
 * common information.<br>
 * It is used by the login page (before login), so it can check any user.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class InfoServiceImpl extends DefaultManager implements InfoService {
	
	// static key for url logo
	// must be put in web.xml, in context definition by init parameter 
	private static final String JEM_LOGO_URL = "jem.logo.url";

	// static key for link for logo
	// must be put in web.xml, in context definition by init parameter
	private static final String JEM_LOGO_LINK = "jem.logo.link";

	private static final long serialVersionUID = 1L;
	
	private final transient InternalsManager internalsManager = new InternalsManager();

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.InfoService#getEnvironmentInformation()
	 */
	@Override
	public String[] getEnvironmentInformation() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		try {
			return internalsManager.getEnvironmentInformation();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG043E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.InfoService#getLogoURL()
	 */
    @Override
    public String[] getLogoURL() throws JemException {
		// gets servlet context
		ServletContext context = super.getServletContext();

		// gets logo and its link from context
		String logoUrl = context.getInitParameter(JEM_LOGO_URL);
		String linkUrl = context.getInitParameter(JEM_LOGO_LINK);
		
		// if they are defined
		// returns them, otherwise null
		if (logoUrl != null){
			String[] infos = new String[Indexes.INFO_LOGO_SIZE.getIndex()];
			infos[Indexes.URL.getIndex()] = logoUrl;
			infos[Indexes.LINK.getIndex()] = linkUrl;
			return infos;
		}
	    return new String[0];
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.InfoService#getAbout()
	 */
	@Override
	public About getAbout() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		try {
			return internalsManager.getAbout();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG043E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @return the number of clients connected to the cluster using a distributed task
	 * @throws JemException 
	 */
	public int getClients() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		try {
			return internalsManager.getClients();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG043E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
		
	}
}
