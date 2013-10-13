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
 * @see Try
 * @author ant
 */
public class Success<T> implements Try<T> {

	/** the successful value */
	private T t;

	public Success(T t) {
		this.t = t;
	}

	/** returns the value, since it was successfully calculated */
	@Override
	public T get() throws Exception {
		return t;
	}

	/** maps the value to a new one, using the given function */
	@Override
	public <S> Try<S> map(Function1<T, S> mapper) {
		try{
			S s = mapper.apply(t);
			return new Success<S>(s);
		}catch(Exception e){
			return new Failure<S>(e);
		}
	}

	/** nothing to do here, because its a success already! */
	@Override
	public Try<T> recover(Recovery<T> r) {
		return this;
	}

	@Override
	public String toString() {
		return "Success(" + t + ")";
	}
}
