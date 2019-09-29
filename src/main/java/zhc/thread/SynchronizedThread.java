package zhc.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedThread {
	
	public static void main(String[] args) {
		SynchronizedThread st = new SynchronizedThread();
		st.useThread();
	}

    /** 建立线程，调用内部类 */
	public void useThread() {
		final IBank bank = new Bank6();
		Runnable newThread = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					bank.save(10);
					System.out.println(i + "账户余额为：" + bank.getAccount());
				}
			}
		};
		
		ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>());
		
		System.out.println("线程1");
		executorService.execute(newThread);
		System.out.println("线程2");
		executorService.execute(newThread);
		
		executorService.shutdown();
	}
	
	/** 7.使用原子变量实现线程同步 */
	class Bank7 implements IBank {
		private AtomicInteger account = new AtomicInteger(100);
//		public AtomicInteger getAccount() {
//			return account;
//		}
		@Override
		public int getAccount() {
			return account.get();
		}
		@Override
		public void save(int money) {
			account.addAndGet(money);
		}
	}

	/** 6.阻塞队列(一般用于生产者消费者的同步) */
	class Bank6 implements IBank {
		private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
		@Override
		public int getAccount() {
			return 100+queue.size()*10;
		}

		@Override
		public void save(int money) {
			try {
				queue.put(money);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/** 5.使用ThreadLocal实现线程同步 */
	static class Bank5 implements IBank {
        //使用ThreadLocal类管理共享变量account
        private static ThreadLocal<Integer> account = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue(){
                return 100;
            }
        };
        @Override
        public void save(int money){
            account.set(account.get()+money);
        }
        @Override
        public int getAccount(){
            return account.get();
        }
	}
	
	/** 4.使用重入锁实现线程同步 */
	class Bank4 implements IBank{
        private int account = 100;
        //需要声明这个锁
        private Lock lock = new ReentrantLock();
        @Override
        public int getAccount() {
            return account;
        }
        @Override
        public void save(int money) {
            lock.lock();
            try{
                account += money;
            }finally{
                lock.unlock();
            }
            
        }
	}

	/** 3.使用特殊域变量（volatile）实现线程同步 */
	class Bank3 implements IBank {
		/** 需要同步的变量加上volatile */
		private volatile int account = 100;
		@Override
		public int getAccount() {
			return account;
		}
		@Override
		public void save(int money) {
			account += money;
		}
	}
	
	/** 1.使用同步方法和同步代码块实现线程同步 */
	class Bank1 implements IBank {
		public int account = 100;
		@Override
		public int getAccount() {
			return account;
		}
		/** 1.用同步方法实现 */
		public synchronized void save1(int money) {
			account += money;
		}
		/** 2.用同步代码块实现 */
		@Override
		public void save(int money) {
			synchronized (this) {
				account += money;
			}
		}
	}
	
	interface IBank {
		int getAccount();
		void save(int money);
	}
}
