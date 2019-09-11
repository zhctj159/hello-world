package zhc.thread;

public class WaitNotifyTest {
	public static void main(String[] args) {
		new WaitNotifyTest().test();
	}
	
	public void test() {
		Thread w1 = new ThreadWait();
		
		Thread w2 = new ThreadWait();
		Thread w3 = new ThreadWait();
		Thread n1 = new ThreadNotify();
		w1.setPriority(8);
		w2.setPriority(6);
		w3.setPriority(7);
		n1.start();
		w1.start();
		w2.start();
		w3.start();
	}
	
	private String[] shareObj = {"true"};
	
	class ThreadWait extends Thread {

		@Override
		public void run() {
			if ("true".equals(shareObj[0])) {
				synchronized (shareObj) {
					try {
						shareObj.wait();
						System.out.println(Thread.currentThread().getName()+"被唤醒");
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	class ThreadNotify extends Thread {
		@Override
		public void run() {
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (shareObj) {
				shareObj[0]="false";
				shareObj.notifyAll();
				System.out.println("通知结束");
			}
		}
	}
}
