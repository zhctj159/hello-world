package zhc.thread;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport的park/unpark相对于对象的wait/notify的好处在于：
 * 1、它针对于线程、更符合阻塞的场景
 * 2、它不需要写在同步代码块中
 * 3、unpark可以在park之前执行
 * 
 * LockSupport的park和unpark的底层使用的也是互斥锁p_thread_mutex(Linux下)
 * @author zhc
 * @time 2019年7月5日 上午10:03:01
 */
public class LockSupportTest {
	private static Thread mainThread;
	
	public static void main(String[] args) throws InterruptedException {
		Thread ta = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
//					Thread.sleep(1000);
					System.out.println(Thread.currentThread().getName()+" wakeup others");
					LockSupport.unpark(mainThread);		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mainThread = Thread.currentThread();
		System.out.println(Thread.currentThread().getName()+" start "+ta.getName());
		ta.start();
		Thread.sleep(1000);
		System.out.println(Thread.currentThread().getName()+" block");
		LockSupport.park(mainThread);
		System.out.println(Thread.currentThread().getName()+" continue");
	}
}
