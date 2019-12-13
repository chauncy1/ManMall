package com.man.test;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import lombok.AllArgsConstructor;

public class ThreadTest {
	
	private static final int NUM = 100; 
	private int COUNT = 0; 
	private static CountDownLatch countDownLatch = new CountDownLatch(NUM);
	
	@Test
	public void test1() {
		for(int i = 0;i < NUM;i++) {
			new Thread(new minusRequest()).start();
			countDownLatch.countDown();
		}
//		try {
//			Thread.currentThread().sleep(5000);
//			Collections.sort(array);
//			System.out.println(array);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	@AllArgsConstructor
	public class minusRequest implements Runnable{
		
		@Override
		public void run() {
			try {
				countDownLatch.await();
				synchronized(this) {
					System.out.println(++COUNT);
				}
//				array.add(++COUNT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
