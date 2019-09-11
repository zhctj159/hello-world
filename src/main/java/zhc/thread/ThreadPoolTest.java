package zhc.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 1.当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。 
 * 2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行 
 * 3.当workQueue已满，且maximumPoolSize>corePoolSize时，新提交任务会创建新线程执行任务 
 * 4.当提交任务数超过maximumPoolSize时，新提交任务由RejectedExecutionHandler处理 
 * 5.当线程池中超过corePoolSize线程，空闲时间达到keepAliveTime时，关闭空闲线程
 * 6.当设置allowCoreThreadTimeOut(true)时，线程池中corePoolSize线程空闲时间达到keepAliveTime也将关闭
 * @author zhc
 * @time 2019年7月1日 下午2:50:57
 */
public class ThreadPoolTest {
	public static void main(String[] args) throws Exception {
		new ThreadPoolTest().testThreadPool();
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(5);
//		executor.setMaxPoolSize(10);
//		executor.setKeepAliveSeconds(200);
//		executor.setQueueCapacity(20);
//		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//		executor.initialize();
//		
//		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//		scheduler.setPoolSize(5);
//		scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//		scheduler.initialize();
	}
	
	/**
	 * spring的线程池：ThreadPoolTaskExecutor、ThreadPoolTaskScheduler
	 */
	public void test9() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(1000);
		executor.setKeepAliveSeconds(200);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setAllowCoreThreadTimeOut(true);
		
		executor.initialize();
	}
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
		}
	};
	
	public void testThreadPool() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		Future<?> future = executorService.submit(runnable);
		Object result = future.get();
		System.out.println(result);
		System.out.println(future.isCancelled());
		System.out.println(future.isDone());
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		cachedThreadPool.execute(runnable);
		
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(runnable);
		
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
		scheduledThreadPool.schedule(runnable, 5, TimeUnit.SECONDS);
		scheduledThreadPool.scheduleAtFixedRate(runnable, 3, 5, TimeUnit.SECONDS);
		scheduledThreadPool.scheduleWithFixedDelay(runnable, 3, 4, TimeUnit.SECONDS);
//		scheduledThreadPool.shutdown();
//		scheduledThreadPool.shutdownNow();
		
		ExecutorService workStealingPool = Executors.newWorkStealingPool();
		workStealingPool.submit(runnable);
	}
}
