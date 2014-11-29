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
package org.pepstock.jem.rest;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;

/**
 * Is a REST client which uses a HTTP basic authentication, closing the
 * connection every time.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class HTTPBaseAuthRestClient extends RestClient {

	private String userid = null;

	private String password = null;
	
	private ApacheHttpClient4 client = null;
	
	/**
	 * Constructs the object.
	 * 
	 * @param uriString REST context, restAuth
	 * @param userid user id to authenticate
	 * @param password password of userid
	 */
	public HTTPBaseAuthRestClient(String uriString, String userid, String password) {
		super(uriString);
		this.userid = userid;
		this.password = password;
		client = initialHttpClient();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.rest.RestClient#getBaseWebResource()
	 */
	@Override
	public WebResource getBaseWebResource() {
		WebResource resource = client.resource(getBaseURI());
		resource.addFilter(new HTTPBasicAuthFilter(userid, password));
		return resource;
	}

}
