package zhc.thread;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class ForkJoinTest {
	public static void main(String[] args) throws InterruptedException {
//		ForkJoinTest test = new ForkJoinTest();
//		test.test2();
		
		ThreadJoinTest t1 = new ThreadJoinTest();
		ThreadJoinTest t2 = new ThreadJoinTest();
		t1.start();
		t1.join();
		t2.start();
	}
	
	static class ThreadJoinTest extends Thread{
		@Override
		public void run() {
			String name = Thread.currentThread().getName();
			for (int i = 0; i < 5; i++) {
				System.out.println(name+":"+i);
			}
		}
	}
	
	public void test2(){
		Instant start = Instant.now();
		long sum = LongStream.rangeClosed(0, 10000000000L).parallel().reduce(0, Long::sum);
		System.out.println(sum);
		Instant end = Instant.now();
		System.out.println("花费时间："+Duration.between(start, end).toMillis());
	}
	
	public void test1(){
		Instant start = Instant.now();
		ForkJoinPool pool = new ForkJoinPool();
		ForkJoinTask<Long> task = new ForkJoinImpl(0L, 10000000000L);
		long sum = pool.invoke(task);
		System.out.println(sum);
		Instant end = Instant.now();
		System.out.println("花费时间："+Duration.between(start, end).toMillis());
	}
	
	class ForkJoinImpl extends RecursiveTask<Long> {
		private static final long serialVersionUID = 1L;
		
		private long start;
		private long end;
		public ForkJoinImpl(long start,long end) {
			this.start = start;
			this.end = end;
		}
		
		private long threshold = 10000000L;
		@Override
		protected Long compute() {
			long sum = 0;
			if ((end-start)<=threshold) {
				for (long i = start; i < end; i++) {
					sum+=i;
				}
				return sum;
			}
			long mid = (start+end)/2;
			ForkJoinImpl left = new ForkJoinImpl(start, mid);
			ForkJoinImpl right = new ForkJoinImpl(mid, end);
			left.fork();
			right.fork();
			return left.join()+right.join();
		}
		
	}
}


