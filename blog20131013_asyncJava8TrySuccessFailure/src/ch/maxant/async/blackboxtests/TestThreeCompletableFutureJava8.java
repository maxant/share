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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class TestThreeCompletableFutureJava8 {

	public static void main(String[] args) throws InterruptedException {
		final Random random = new Random();
		int numTasks = 10;
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final List<CompletableFuture<Integer>> futures = new ArrayList<>();
		
		for(int i = 0; i < numTasks; i++){
			final int j = i;
			log("adding future " + i);

			// PART 1
			//start some work async / in the future
			CompletableFuture<String> f = CompletableFuture.supplyAsync( () -> {
				sleep(random.nextInt(1000));
				if(j < 5){
					log("working success");
					return "20";
				}else{
					log("working failure");
					throw new RuntimeException();
				}
			});

			// PART 2
			//register a callback, to be called when the work is done
			log("adding mapping callback to future");
			final CompletableFuture<Integer> f2 = f.exceptionally( (Throwable t) -> {
				log("recovering");
				return "-10";
			}).thenApplyAsync( (String stringNumber) -> {
				log("mapping '" + stringNumber + "' to int");
				return Integer.parseInt(stringNumber);
			});
			
			futures.add(f2);
		}

		// PART 3
		log("registering callback for final result");
		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[]{})).thenApplyAsync( (Void v) -> {
			Integer finalResult = futures.stream().map( (CompletableFuture<Integer> f) -> {
				try {
					return f.get();
				} catch (InterruptedException | ExecutionException e) {
					return 0;
				}
			}).reduce(0, Integer::sum);
			
			log("final result is " + finalResult);
			if(finalResult != 50){
				throw new RuntimeException("FAILED");
			}else{
				log("SUCESS");
			}
			
			latch.countDown(); //coz we are now ready to finish
			return null;
		});
		System.out.println("Completed submitting all tasks on thread " + Thread.currentThread().getId());

		latch.await();
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



