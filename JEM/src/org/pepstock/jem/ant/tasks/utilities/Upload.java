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
package org.pepstock.jem.ant.tasks.utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestClientFactory;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.services.GfsManager;
import org.pepstock.jem.rest.services.LoginManager;

/**
 * TASK ANT which can upload files on all GFS folder, by REST.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Upload extends Task {
	
	private String url = null;
	
	private String userid = null;
	
	private String password = null;
	
	private LoggedUser user = null;
	
	private List<Destination> destinations = new ArrayList<Destination>();

	/**
	 * Empty constructor
	 */
	public Upload() {
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Adds new subtask with destination of upload
	 * @param ds destination task to execute
	 */
	public void addDestination(Destination ds) {
		destinations.add(ds);
	}
    
    /* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		// check if URL is set
		// if not EXCEPTION
		if (url == null){
			throw new BuildException(AntMessage.JEMA063E.toMessage().getFormattedMessage("url"));
		}
		// check if USERID is set
		// if not EXCEPTION
		if (userid == null){
			throw new BuildException(AntMessage.JEMA063E.toMessage().getFormattedMessage("userid"));
		}
		// check if PASSWORD is set
		// if not EXCEPTION
		if (password == null){
			throw new BuildException(AntMessage.JEMA063E.toMessage().getFormattedMessage("password"));
		}
		// creates REST client
		RestClient client = RestClientFactory.getClient(getUrl(), false);
		// gets login manager
		LoginManager loginManager = new LoginManager(client);
		// login and upload files
		try {
			login(loginManager);
			// creates GFS manager
	        GfsManager gfsManager = new GfsManager(client);
	        // scans all destination
	        for(Destination dd : destinations ) {
	        	// passes the GFS manager to destination
	        	dd.setGfsManager(gfsManager);
	        	// executes destination task
	        	dd.execute();
	        }
		} catch (JemException e) {
			throw new BuildException(e);
		} finally {
			// checks if to do logoff or not
			if (user != null){
				try {
					// logoff from JEM
					loginManager.logoff();
				} catch (JemException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
    }
	
	/**
	 * Performs login to JEM by REST
	 * @param loginManager login manager 
	 * @throws JemException if any exception occurs
	 */
	private void login(LoginManager loginManager) throws JemException{
		// gets user from login manager
		user = loginManager.getUser();
		if (user == null) {
			// create object account with userid and password
			Account account = new Account();
			account.setUserId(userid);
			account.setPassword(password);
			// performs login and save logged user
			user = loginManager.login(account);
		}
	}
}
