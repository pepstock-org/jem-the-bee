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
package org.pepstock.jem.protocol;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Common future used to communicate to the user because all IO operations are non-blocking
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
class DefaultFuture<T> implements Future<T> {
	
	private CountDownLatch countDown = new CountDownLatch(1);
	
	private T object = null;
	
	private ExecutionException exception = null;

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return object != null || exception != null;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			// waits forever
			return get(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			throw new ExecutionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// if OK, return the value
		if (isDone()){
			return get0();
		}
		// wait
		await0(timeout, unit);
		// if the future has got 
		// the exception, throws it
		if (exception != null){
			throw exception;
		}
		// return the object
		return object;
	}
	
	/**
	 * Returns the object
	 * @return object 
	 */
	T getObject(){
		return object;
	}
	
	/**
	 * Sets the object and notify the wait that is ended
	 * @param object object to set into future
	 */
	void setObjectAndNotify(T object){
		this.object = object;
		countDown.countDown();
	}

	/**
	 * Sets the exception and notify the wait that is ended
	 * @param exception exception occurred during the connection with server
	 */
	void setExcetpionAndNotify(ExecutionException exception){
		this.exception = exception;
		countDown.countDown();
	}
	
	/**
	 * Waits for a time period. If period is ended, throw a timeout.
	 * @param timeout period of time
	 * @param unit unit of time
	 * @throws InterruptedException if any error occurs
	 * @throws TimeoutException if it went in timeout
	 */
	private void await0(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException{
		boolean endedOk= countDown.await(timeout, unit);
		if (!endedOk){
			throw new TimeoutException("Ended in timeout");
		}
	}

	/**
	 * Returns the object 
	 * @return the object
	 * @throws ExecutionException if there is an exception
	 */
	private T get0() throws ExecutionException{
		if (exception != null){
			throw exception;
		}
		return object;
	}

}
