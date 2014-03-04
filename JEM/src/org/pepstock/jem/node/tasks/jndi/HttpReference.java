/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.node.tasks.jndi;

import java.io.InputStream;

import javax.naming.Reference;

import org.apache.http.client.HttpClient;

/**
 * Sets the constants for <code>JNDI</code> needed by <code>HTTP</code> datasources object. <br>
 * It uses <code>org.apache.http.client</code> classes.
 * It uses {@link HttpFactory} to create a <code>HTTP</code>
 * datasource to be used inside the java programs. <br>
 * The <code>HTTP</code> datasource object allows to connect to a <code>HTTP</code> data provider, 
 * such as <code>Servlets</code>, or generally <code>HTTP URL</code> , call a <code>GET</CODE>
 * or a <code>POST</code> method and it returns the <code>Response</code> content. <br>
 * This object is the {@link InputStream} with the content of the {@link HttpClient} request call.
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public class HttpReference extends Reference {

	private static final long serialVersionUID = 1L;

	/**
	 * Declaration of the factory to be used (when requested) to create the <code>HTTP JNDI</code> datasource object:<br>
	 * <dd><b>{@link HttpFactory}</b>, the <code>HTTP</code> datasource object factory.
	 * 
	 * @see #JNDI_OBJECT
	 */
	public static final String JNDI_FACTORY = HttpFactory.class.getName();
	
	/**
	 * Is the <code>JNDI</code> datasource object created when requested by the {@link HttpFactory}. <br>
	 * This object is useful to access <code>HTTP</code> sources, for example
	 * <code>Servlets</code>, or generally <code>HTTP URL</code>,
	 * and it contains the content of the request call.
	 * 
	 * @see InputStream
	 * @see HttpClient
	 */
	public static final String JNDI_OBJECT = InputStream.class.getName();
	
	/**
	 * Creates a <code>JNDI</code> reference for <code>HTTP</code> purposes.
	 */
	public HttpReference() {
		super(JNDI_OBJECT, JNDI_FACTORY, null);
	}
}