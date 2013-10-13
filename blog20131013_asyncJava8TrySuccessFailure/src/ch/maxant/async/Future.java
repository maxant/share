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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Future is what results when a {@link Task} is submitted to the enclosed
 * execution service.  It will be completed some time in the future, just
 * as a {@link java.util.concurrent.Future} is, and indeed this class
 * implements that interface.  Submit tasks to the underlying
 * execution service by calling the {@link #future(Task)} method.
 * ALWAYS call {@link #shutdown()} at the end of your program
 * otherwise it won't ;-)
 * <br><br>
 * In Scala, the execution service used by the futures and promises
 * framework is ideally the same used by any other framework that needs
 * a pool of threads, e.g. Akka - they ensure only one is used, to reduce
 * the number of threads in the system to the absolute minimum.
 * <br><br>
 * 
 * 
 * @author ant
 *
 * @param <T> the type of object you expect to get out of the future.
 */
public class Future<T> implements java.util.concurrent.Future<T>  {

    private static final ExecutorService pool;
    
    static {
    	//TODO based on number of cores, but configurable
    	int numCpus = Runtime.getRuntime().availableProcessors();
    	pool = Executors.newFixedThreadPool(numCpus+1);
    }

    public static void shutdown() {
    	pool.shutdown();
	}

    /** schedule work to run async, sometime in the future */
	public static <T> Future<T> future(Task<T> task){
		Future<T> f = new Future<T>();
		task.myFuture = f; //setup back reference, so task can complete future when it finishes running
		f.delegate = pool.submit(task);
		return f;
	}

	/** NOTE: not a parameter in the constructor due to timing issues of scheduling in the pool - see the {@link #Future()} method's implementation for the reason. */
	private java.util.concurrent.Future<?> delegate;

	/** registred callbacks which are not ready to run, because this future is not yet complete */
	private List<Pair<Callable<?>, Future<?>>> futuresDependingOnThisFutureFinishing = new ArrayList<>();

	/** provide a function to call, when this future is completed, or immediately, if already complete. 
	 * no guarantee that the thread calling this method will also call the function. */
	public <T2> Future<T2> map(final Function1<Try<T>, T2> func){
		final Future<T2> future = new Future<>();
		Callable<T2> c = new Callable<T2>() {
			@Override
			public T2 call() throws Exception {
				try{
					T t = get();
					T2 t2 = func.apply(new Success<T>(t));
					return t2;
				}catch(Exception e){
					T2 t2 = func.apply(new Failure<T>(e));
					return t2;
				}finally{
					future.complete();
				}
			}
		};
		submit(c, future);
		return future;
	}

	private void submit(Callable<?> c, Future<?> f) {
		if(isDone()){
			f.delegate = pool.submit(c); //so that callback can be done
		}else{
			futuresDependingOnThisFutureFinishing.add(new Pair<Callable<?>, Future<?>>(c, f));
		}
	}

	/** also called by {@link Task#call()}, after the work contained in the task is completed. */
	void complete(){
		for(Pair<Callable<?>, Future<?>> p : futuresDependingOnThisFutureFinishing){
			p.u.delegate = pool.submit(p.t); //so that callback can be done
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return delegate.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return delegate.isCancelled();
	}

	@Override
	public boolean isDone() {
		ensureDelegateHasBeenSet();
		return delegate.isDone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() throws InterruptedException, ExecutionException {
		ensureDelegateHasBeenSet();
		return (T)delegate.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		ensureDelegateHasBeenSet();
		return (T)delegate.get(timeout, unit);
	}

	/** dodgy, but necessary, since the delegate might not have been set, if the job has completed before the current thread has had a chance to set the delegate */
	private void ensureDelegateHasBeenSet() {
		while(delegate == null){
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				//who cares
			}
		}
	}

	/** the given callback is called when all futures in the list are complete */
	public static <T> void registerCallback(List<Future<T>> futures, Callback<List<Try<T>>> c){
		addCallback(0, futures, c, new ArrayList<Try<T>>());
	}

	/** see {@link #registerCallback(List, Callback)} which calls this.  this method also calls itself! */
	private static <T> void addCallback(final int i, final List<Future<T>> futures, final Callback<List<Try<T>>> c, final List<Try<T>> results){
		futures.get(i).map(new Function1<Try<T>, T>() {
			@Override
			public T apply(Try<T> result) throws Exception {
				results.add(result);
				if(i == futures.size()-1){
					//every future in the list is now complete, so lets call the callback!
					c.apply(results);
				}else{
					addCallback(i+1, futures, c, results);
				}
				return null;
			}
		});
	}
}
