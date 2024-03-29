package zhc.others;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class LimiterTest {
	public static void main(String[] args) {
		RateLimiter limiter = RateLimiter.create(5);
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
		int count = 0;
		boolean flag = true;
		while (flag) {
			count++;
			if (count>=100) {
				flag = false;
			}
//			try {
//				Thread.sleep((long)(Math.random()*300));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(new Random(300).nextLong());
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
//					if (limiter.tryAcquire()) {
					if (limiter.tryAcquire(500,TimeUnit.MICROSECONDS)) {
//					if (limiter.tryAcquire(3,500,TimeUnit.MICROSECONDS)) {
						System.out.println("成功");
					} else {
						System.err.println("失败");
					}
				}
			});
			System.out.println(executor.getQueue().size());
			
		}
		executor.shutdown();
	}
}
