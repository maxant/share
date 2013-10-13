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
 * Abstraction of a {@link Success} and a {@link Failure}.
 * 
 * @author ant
 * @param <T> the type of the success or failure
 * @see Success, Failure
 */
public interface Try<T> {

	/** returns the value, or throws an exception if its a failure. */
	T get() throws Exception;

    /** converts the value using the given function, resulting in a new Try */
	<S> Try<S> map(Function1<T, S> func);

	/** can be used to handle recovery by converting the exception into a {@link Try} */
	Try<T> recover(Recovery<T> r);

}
