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
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class DefaultFuture<T> implements Future<T> {
	
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
		if (isDone()){
			return get0();
		}
		await0(timeout, unit);
		if (exception != null){
			throw exception;
		}
		return object;
	}
	
	T getObject(){
		return object;
	}
	
	void setObjectAndNotify(T object){
		this.object = object;
		countDown.countDown();
	}

	void setExcetpionAndNotify(ExecutionException exception){
		this.exception = exception;
		countDown.countDown();
	}
	
	private void await0(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException{
		boolean endedOk= countDown.await(timeout, unit);
		if (!endedOk){
			throw new TimeoutException("Ended in timeout");
		}
	}

	private T get0() throws ExecutionException{
		if (exception != null){
			throw exception;
		}
		return object;
	}

}
