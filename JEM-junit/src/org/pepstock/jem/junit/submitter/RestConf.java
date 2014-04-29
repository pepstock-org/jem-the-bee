package org.pepstock.jem.junit.submitter;

public class RestConf {
	private String url;
	
	private String user;
	
	private String password;
	
	/**
	 * 
	 * @return the url to connect to
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url to connect to
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return the user for authentication
	 */
	public String getUser() {
		return user;
	}

	/**
	 * 
	 * @param user for authentication
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * 
	 * @return password for authentication
	 */ 
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password for authentication
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
