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
package ch.maxant.async.blackboxtests;

import static ch.maxant.async.Future.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import ch.maxant.async.Callback;
import ch.maxant.async.Future;
import ch.maxant.async.Recovery;
import ch.maxant.async.Task;
import ch.maxant.async.Try;
import ch.maxant.async.Work;

public class TestTwoJava8 {

	public static void main(String[] args) {
		final Random random = new Random();
		int numTasks = 10;
		List<Future<Integer>> futures = new ArrayList<>();
		
		for(int i = 0; i < numTasks; i++){
			final int j = i;
			log("adding future " + i);

			// PART 1
			//start some work async / in the future
			Future<String> f = future(new Task<String>( () -> {
				sleep(random.nextInt(1000));
				if(j < 5){
					log("working success");
					return "20";
				}else{
					log("working failure");
					throw new Exception();
				}
			}));

			// PART 2
			//register a callback, to be called when the work is done
			log("adding mapping callback to future");
			final Future<Integer> f2 = f.map( (Try<String> stringNumber) -> {
				return stringNumber.map( (String s) -> {
					log("mapping '" + s + "' to int");
					return Integer.parseInt(s);
				}).recover( (Exception e) -> {
					log("recovering");
					return -10;
				}).get(); //wont throw an exception, because we provided a recovery!
			});
			
			futures.add(f2);
		}

		// PART 3
		log("registering callback for final result");
		Future.registerCallback(futures, (List<Try<Integer>> results) -> {
				
			Integer finalResult = results.stream().map( (Try<Integer> t) -> {
				log("mapping " + t);
				try {
					return t.get();
				} catch (Exception e) {
					return 0;
				}
			}).reduce(0, Integer::sum);
			
			log("final result is " + finalResult);
			Future.shutdown();
			if(finalResult != 50){
				throw new RuntimeException("FAILED");
			}else{
				log("SUCESS");
			}
		});
		
		System.out.println("Completed submitting all tasks on thread " + Thread.currentThread().getId());
		
		//this main thread will now die, but the Future executor is still up and running.  the callback will shut it down and with it, the jvm.
	}

	protected static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	private static void log(String msg){
		System.out.println("Thread-" + Thread.currentThread().getId() + " says: " + msg);
	}

}



