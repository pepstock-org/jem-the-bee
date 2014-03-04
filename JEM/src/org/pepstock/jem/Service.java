package org.pepstock.jem;


/**
 * Is the abstract class for a service
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public abstract class Service {

	private ServiceStatus status;

	/**
	 * Start the service
	 */
	public abstract void start();

	/**
	 * Shut down the service
	 */
	public abstract void shutdown();

	/**
	 * @return the status of the service
	 */
	public ServiceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status of the service to set
	 */
	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	
}
