package zhc.thread;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Test;

public class ConcurrentCollectionTest {
	public static void main(String[] args) {
		ConcurrentCollectionTest test = new ConcurrentCollectionTest();
		test.testMap();
		
		Map<String, Object> synchronizedMap = Collections.synchronizedMap(new HashMap<String, Object>());
		System.out.println(synchronizedMap.size());
	}
	
	/**
	 * Queue:
	6.ConcurrentLinkedQueue：是使用非阻塞的方式实现的基于链接节点的无界的线程安全队列，性能非常好。（java.util.concurrent.BlockingQueue 接口代表了线程安全的队列。）
	7.ArrayBlockingQueue：基于数组的有界阻塞队列
	8.LinkedBlockingQueue：基于链表的有界阻塞队列。
	9.PriorityBlockingQueue：支持优先级的无界阻塞队列，即该阻塞队列中的元素可自动排序。默认情况下，元素采取自然升序排列
	10.DelayQueue：一种延时获取元素的无界阻塞队列。
	11.SynchronousQueue：不存储元素的阻塞队列。每个put操作必须等待一个take操作，否则不能继续添加元素。内部其实没有任何一个元素，容量是0

	Deque:(Deque接口定义了双向队列。双向队列允许在队列头和尾部进行入队出队操作。)
	12.ArrayDeque:基于数组的双向非阻塞队列。
	13.LinkedBlockingDeque:基于链表的双向阻塞队列。
	 */
	public void testCollections3() {
		new ConcurrentLinkedQueue<String>();
		new ArrayBlockingQueue<String>(16);
		new LinkedBlockingQueue<String>();
		new PriorityBlockingQueue<String>();
		new DelayQueue<Delayed>();
		new SynchronousQueue<String>();
		
		new ArrayDeque<String>();
		new LinkedBlockingQueue<String>();
	}
	
	/**
	 * 	线程不安全--->线程安全的并发容器
	 * ArrayList--->CopyOnWriteArrayList
	 * HashSet--->CopyOnWriteArraySet
	 * TreeSet--->ConcurrentSkipListSet
	 * HashMap--->ConcurrentHashMap
	 * TreeMap--->ConcurrentSkipMap
	 */
	public void testCollections2() {
		new CopyOnWriteArrayList<String>();		//ArrayList
		new CopyOnWriteArraySet<String>();		//ArraySet
		new ConcurrentSkipListSet<String>();	//TreeSet
		new ConcurrentHashMap<String, Integer>();		//HashMap
		new ConcurrentSkipListMap<String, Integer>();	//TreeMap
	}
	
	/**
	 * 同步容器类
	 */
	public void testCollections1() {
		new Vector<String>();
		new Hashtable<String, String>();
	}
	
	@Test
	public void testArrayList() throws InterruptedException {
		List<Integer> list = null;
		long start=0,end=0;
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		CountDownLatch latch = new CountDownLatch(10000000);
		
		list = new ArrayList<Integer>();
		start = System.currentTimeMillis();
		final List<Integer> list1 = list;
		for (int i = 0; i < 10000000; i++) {
			final int j = i;
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					list1.add(j);
					latch.countDown();
				}
			});
//			list.add(i);
		}
		latch.await();
		end = System.currentTimeMillis();
		System.out.println("耗时："+(end-start));
		
		list = new CopyOnWriteArrayList<Integer>();
		start = System.currentTimeMillis();
		final List<Integer> list2 = list;
		for (int i = 0; i < 10000000; i++) {
			final int j = i;
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					list2.add(j);
					latch.countDown();
				}
			});
//			list.add(i);
		}
		latch.await();
		end = System.currentTimeMillis();
		System.out.println("耗时："+(end-start));
		
		executorService.shutdown();
		
//		start = System.currentTimeMillis();
//		for (int i = 0; i < 10000000; i++) {
//			list.add(i);
//		}
//		end = System.currentTimeMillis();
//		System.out.println("耗时："+(end-start));
	}
	
	@Test
	public void testMap(){
		Map<String, Integer> map = null;
		long start=0,end=0;
		map = new HashMap<String, Integer>();
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			map.put(""+i, i);
		}
		end = System.currentTimeMillis();
		System.out.println("耗时："+(end-start));
		
		map = new ConcurrentHashMap<String, Integer>();
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			map.put(""+i, i);
		}
		end = System.currentTimeMillis();
		System.out.println("耗时："+(end-start));
	}
	
}
