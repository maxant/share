package ch.maxant.async;

import java.util.function.Function;

/**
 * Similar to {@link Function}, except that the {@link #apply(Object)} method 
 * also declares that it <code>throws Exception</code>, which combined with 
 * {@link Try} means that programmers need to code less.
 * 
 * @see Function
 * 
 * @author ant
 *
 * @param <T1> the input type
 * @param <T2> the return type
 */
public interface Function1<T1, T2> {

	T2 apply(T1 t) throws Exception;
}
