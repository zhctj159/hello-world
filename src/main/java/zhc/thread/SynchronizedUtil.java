package zhc.thread;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedUtil {
//juc包下有：线程池、阻塞队列、Callable、同步工具、
	public static void main(String[] args) throws Exception {
		SynchronizedUtil util = new SynchronizedUtil();
		// util.testCountDownLatch();
		// util.testCyclicBarrier();
		// util.testSemaphore();
		util.testReentrantLock();
	}
	
	class MyClass2 {
		private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		private final Lock r = rwl.readLock();
		private final Lock w = rwl.writeLock();
		
		private final MyClass obj = new MyClass();
		
		public int get() {
			r.lock();
			try {
				return obj.get();
			} finally {
				r.unlock();
			}
		}
		
		public void add(int delta) {
			w.lock();
			try {
				obj.add(delta);
			} finally {
				w.unlock();
			}
		}
	}
	
	class MyClass {
		private int value=0;
		
		public int get() {
			return value;
		}
		
		public void add(int delta) {
			value += delta;
		}
	}

	public void testReentrantLock() {
		int size = 9;
		ExecutorService executorService = Executors.newFixedThreadPool(size);
		ReentrantLock lock = new ReentrantLock();
		for (int i = 0; i < size; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String name = Thread.currentThread().getName();
						lock.lock();
						System.err.println(name + "锁定成功");
						long time = new Random(2000).nextLong();
						Thread.sleep(time);
						System.out.println(name + "完成工作，耗时：" + time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			});
		}
		executorService.shutdown();
	}

	public static void testReentrantLock2() {
		final Lock lock = new ReentrantLock();
		final Condition reachThreeCondition = lock.newCondition();	// 第一个条件当屏幕上输出到3
		final Condition reachSixCondition = lock.newCondition();	// 第二个条件当屏幕上输出到6
		final NumberWrapper num = new NumberWrapper();
		
		ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>());
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					System.out.println("threadA start write");
					while (num.value <= 3) {
						System.out.println(num.value);
						num.value++;
					}
					reachThreeCondition.signal();
				} finally {
					lock.unlock();
				}
				lock.lock();
				try {
					reachSixCondition.await();
					System.out.println("threadA start write");
					while (num.value <= 9) {
						System.out.println(num.value);
						num.value++;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		});
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
					while (num.value <= 3) {
						reachThreeCondition.await();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
				try {
					lock.lock();
					System.out.println("threadB start write");
					while (num.value <= 6) {
						System.out.println(num.value);
						num.value++;
					}
					reachSixCondition.signal();
				} finally {
					lock.unlock();
				}
			}
		});
		executorService.shutdown();
	}

	static class NumberWrapper {
		public int value = 1;
	}

	public void testCountDownLatch() throws InterruptedException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		int size = 9;
		CountDownLatch comeFlag = new CountDownLatch(size);
		CountDownLatch startFlag = new CountDownLatch(1);
		CountDownLatch endFlag = new CountDownLatch(size);
		for (int i = 0; i < size; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Random r = new Random(5000);
						long time = r.nextLong();
						Thread.sleep(time);
						comeFlag.countDown();
						System.out.println(Thread.currentThread().getName() + "已经到达，耗时：" + time);
						startFlag.await();
						time = r.nextLong();
						Thread.sleep(time);
						endFlag.countDown();
						System.out.println(Thread.currentThread().getName() + "已经吃完，耗时：" + time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		comeFlag.await();
		System.out.println("------ 人员都已经到齐，开饭 ------");
		startFlag.countDown();
		endFlag.await();
		System.out.println("------ 人员都已经吃完，散场 ------");
		executorService.shutdown();
	}

	public void testCyclicBarrier() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		int size = 9;
		CyclicBarrier cyclicBarrier = new CyclicBarrier(size);
		for (int i = 0; i < size; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Random r = new Random(5000);
						long time = r.nextLong();
						Thread.sleep(time);
						System.out.println(Thread.currentThread().getName() + "已经到达，耗时：" + time);
						if (cyclicBarrier.await() <= 0) {
							System.out.println("------ 人员都已经达到，开饭 ------");
						}
						time = r.nextLong();
						Thread.sleep(time);
						System.out.println(Thread.currentThread().getName() + "已经吃完，耗时：" + time);
						if (cyclicBarrier.await() <= 0) {
							System.out.println("------ 人员都已经吃完，散场 ------");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}
				}
			});
		}
		executorService.shutdown();
	}

	public void testSemaphore() {
		int size = 9;
		ExecutorService executorService = Executors.newFixedThreadPool(size);
		Semaphore semaphore = new Semaphore(3);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				try {
					semaphore.acquire();
					System.err.println(name + "获得笔");
					long time = new Random(2000).nextLong();
					Thread.sleep(time);
					System.out.println(name + "用完笔，耗时：" + time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}
		};
		for (int i = 0; i < size; i++) {
			executorService.execute(runnable);
		}
		executorService.shutdown();
	}
}
