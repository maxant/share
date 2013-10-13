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
 * Similar to a function, but the {@link #apply(Object)} method has a void return, so that programmers 
 * using this don't think they can usefully return something - because they can't - the code which calls
 * such a callback has no interest in return values.
 * @author ant
 */
public interface Callback<T> {

	void apply(T t);
}
