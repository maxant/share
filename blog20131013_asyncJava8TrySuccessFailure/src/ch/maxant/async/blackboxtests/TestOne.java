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

import java.util.Random;

import ch.maxant.async.Function1;
import ch.maxant.async.Future;
import ch.maxant.async.Recovery;
import ch.maxant.async.Task;
import ch.maxant.async.Try;
import ch.maxant.async.Work;

public class TestOne {

	private static final Random random = new Random();
		
	public static void main(String[] args) throws Exception {
		
		try{
			Future<String> f = future(new Task<String>(new Work<String>(){
				@Override
				public String doWork() throws Exception {
					sleep();
					return "1";
				}
			}));
			
			Future<Integer> finalResult = f.map(new Function1<Try<String>, Integer>() {
				@Override
				public Integer apply(Try<String> s) {
					sleep();
					Try<Integer> result = s.map(new Function1<String, Integer>() {
						public Integer apply(String s) {
							return Integer.parseInt(s);
						}
					}).recover(new Recovery<Integer>() {
						public Integer recover(Exception e) {
							return 666;
						}
					});
					try{
						return result.get(); //wont throw an exception, because we provided a recovery!
					}catch(Exception e){throw new RuntimeException(e);}
				}
			});
			
			if(!finalResult.get().equals(1)){
				throw new RuntimeException("failed since finalResult was " + finalResult.get());
			}
			System.out.println("Success.");
			
		}finally{
			Future.shutdown();
		}
	}

	protected static void sleep() {
		try {
			Thread.sleep(random.nextInt(10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
		
