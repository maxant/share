/*
 *   Copyright 2013 Ant Kutschera
 *   
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package ch.maxant.async;

import java.util.concurrent.Callable;

/**
 * 
 * @author ant
 */
public class Task<T> implements Callable<T> {

	/** back ref to the {@link Future} which will contain the result, so that 
	 * it can be notified when this task finishes running. */
	Future<T> myFuture;

	private Work<T> work;
	
	public Task(Work<T> work) {
		this.work = work;
	}
	
	@Override
	public T call() throws Exception {
		try{
			final T t = work.doWork();
			return t; //this is what a call to "get" on the java Future that is returned from submitting to the pool is given
		}finally{
			myFuture.complete(); //tell the future to call its callbacks - they <i>may</i> run just before this value finishes calling, but the hard long work was completed 4 lines above, so its not an issue, if they have to wait for the result when callling say get().
		}
	}

}
