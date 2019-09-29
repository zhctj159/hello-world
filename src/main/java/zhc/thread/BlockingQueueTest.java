package zhc.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTest {
	public static void main(String[] args) {
		ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
		
		BlockingQueue<Long> queue = new LinkedBlockingQueue<Long>(4);
		
		executorService.execute(new Runnable() {
			long count = 0;
			@Override
			public void run() {
				while (true) {
					try {
						queue.put(++count);
						System.out.println("摊好煎饼"+count);
						if (queue.size()<4) {
							Thread.sleep(700);
						} else {
							Thread.sleep(3000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
		});
//		executorService.shutdown();

		boolean flag = true;
		int i = 0;
		while (flag) {
			i++;
			if (i==10000) {
				flag = false;
			}
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						long l = queue.take();
						System.err.println("买到煎饼"+l);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
					
				}
			});
			try {
				Thread.sleep(1300);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		executorService.shutdown();
	}
}
