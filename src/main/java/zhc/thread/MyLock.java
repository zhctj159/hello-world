package zhc.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLock implements Lock {
	
	public static void main(String[] args) {
		Lock lock = new MyLock();
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						lock.lock();
						System.out.println("锁住了" + Thread.currentThread().getName());
						Thread.sleep(2000);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			}).start();
		}
	}

	static class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean tryAcquire(int arg) {
			if (arg != 1) {
				throw new RuntimeException("参数不为1");
			}
			if (compareAndSetState(0, arg)) {
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}

		@Override
		protected boolean tryRelease(int arg) {
			if (getState() == 0) {
				// 状态已经为0，不需要释放
				throw new IllegalMonitorStateException();
			}
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}

		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}
	}

	Sync sync = new Sync();

	@Override
	public void lock() {
		sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.isHeldExclusively();
	}

	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return null;
	}

}
