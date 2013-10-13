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

/**
 * A negative {@link Try}.
 * 
 * @author ant
 * @param <T> the type of value which this failure represents
 */
public class Failure<T> implements Try<T> {

	private Exception e;

	public Failure(Exception e) {
		this.e = e;
	}
	
	/** throws the underlying exception */
	@Override
	public T get() throws Exception {
		throw e;
	}
	
	/** @return a new failure, wrapping the underlying exception in this Try. */
	@Override
	public <S> Try<S> map(Function1<T, S> func) {
		return new Failure<S>(e);
	}
	
	/** uses the <code>r</code> callback to return a {@link Success}. */
	@Override
	public Try<T> recover(Recovery<T> r) {
		T t = r.recover(e);
		return new Success<T>(t);
	}

	/** returns the underlying exception */
	public Throwable getException() {
		return e;
	}
	
	@Override
	public String toString() {
		return "Failure(" + e.getClass() + ")";
	}
}
