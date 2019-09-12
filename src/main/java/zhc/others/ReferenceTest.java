package zhc.others;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ReferenceTest {
	public static void main(String[] args) throws InterruptedException {
		WeakIdentityMap map = new WeakIdentityMap();
		map.put("1", "a");
		map.put(new MyDate(), "b");
		map.put(new MyDate(), "2");
		System.gc();
		Thread.sleep(1000);
		map.cleanup();
		map.put("1", "a");
		
		// ReferenceTest test = new ReferenceTest();
		// test.testSoftReference();
	}

	private static class WeakIdentityMap {

		private transient ReferenceQueue<Object> idKeys = new ReferenceQueue<Object>();

		private Map<WeakIdKey, Object> objHashcodeToPyId = new HashMap<WeakIdKey, Object>();

		private void cleanup() {
			@SuppressWarnings("rawtypes")
			Reference k;
			while ((k = idKeys.poll()) != null) {
				System.out.println("cleanup from Map within ReferenceQueue");
				objHashcodeToPyId.remove(k);
			}
		}

		private class WeakIdKey extends WeakReference<Object> {
			private final int hashcode;

			WeakIdKey(Object obj) {
				super(obj, idKeys);
				hashcode = System.identityHashCode(obj);
			}

			@Override
			public int hashCode() {
				return hashcode;
			}

			@Override
			public boolean equals(Object other) {
				if (null==other) {
					return false;
				}
				if (this.getClass()!=other.getClass()) {
					return false;
				}
				Object obj = get();
				if (obj != null) {
					return obj == ((WeakIdKey) other).get();
				} else {
					return this == other;
				}
			}
		}

//		public int _internal_map_size() {
//			return objHashcodeToPyId.size();
//		}

		public void put(Object key, Object val) {
			objHashcodeToPyId.put(new WeakIdKey(key), val);
		}

//		public Object get(Object key) {
//			cleanup();
//			return objHashcodeToPyId.get(new WeakIdKey(key));
//		}

//		public void remove(Object key) {
//			cleanup(key.toString());
//			objHashcodeToPyId.remove(new WeakIdKey(key));
//		}
	}

	
	@Test
	public void testPhantomReference() {
		MyDate date = new MyDate();
		ReferenceQueue<MyDate> queue = new ReferenceQueue<MyDate>();
		PhantomReference<MyDate> ref = new PhantomReference<MyDate>(date, queue);

		date = null;
		System.gc();	//建议虚拟机回收垃圾
		try {
//			Reference<?> ref2 = queue.poll();
			Reference<?> ref2 = queue.remove();
			System.out.println(ref2);
			System.out.println(ref2==ref);
			ref.clear(); //幽灵引用不会自动清空，要手动运行
            ref = null;
            date = new MyDate();
            ref = new PhantomReference<MyDate>(date, queue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testWeakReference() {
		// 创建MyDate对象的两个引用(幽灵引用和弱引用)放到引用队列
		ReferenceQueue<MyDate> queue = new ReferenceQueue<MyDate>();
		MyDate date = new MyDate();
		WeakReference<MyDate> ref = new WeakReference<MyDate>(date, queue);
		WeakReference<MyDate> ref2 = new WeakReference<MyDate>(date, queue);
		date = null;
		System.out.println("ref = " + ref);
		System.out.println("ref2 = " + ref2);
		// 垃圾回收
		Reference<?> ref3 = null;
		ref3 = queue.poll();
		System.out.println("queue.poll() = " + ref3); // 垃圾回收之前队列为空，两个引用还没有被放入到队列
		System.gc(); // 显式要求执行垃圾回收
		try {
			Thread.sleep(3000); // 等待JVM实际执行垃圾回收
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		// ref3 = queue.poll();
		while ((ref3 = queue.poll()) != null) {
			System.out.println("queue.poll()2 = " + ref3);
		}
		ref3 = null;
				
//		WeakReference<MyDate> ref = new WeakReference<MyDate>(new MyDate());
//		System.out.println(ref);
//		System.gc();
	}

	/**
	 * 软引用：内存不足时可被垃圾回收器回收。如果全部释放完这些对象之后，内存还不足，才会抛出OutOfMemory错误。
	 * 联合ReferenceQueue构造有效期短/占内存大/生命周期长的对象的二级高速缓冲器
	 */
	@Test
	public void testSoftReference() {
		SoftReference<MyDate> ref = new SoftReference<MyDate>(new MyDate());
		System.out.println(ref);
		Util.drainMemory();
	}

	@Test
	public void testStrongReference() {
		MyDate date = new MyDate();
		System.out.println(date);
		System.gc();
	}

	@Test
	public void test1() {
		MyDate date = new MyDate();
		date = null;
		System.out.println(date);
		// System.gc(); //显示垃圾回收
		// ReferenceTest2.drainMemory(); //隐式垃圾回收
	}

	/**
	 * 作用：定义一个方法drainMemory()，消耗大量内存，以此来引发JVM回收内存。
	 */
	private static class Util {
		public static void drainMemory() {
			String[] array = new String[1024 * 1000000];
			for (int i = 0; i < 1024 * 10; i++) {
				for (int j = 'a'; j <= 'z'; j++) {
					array[i] += (char) j;
				}
			}
		}
	}

	/**
	 * 作用：覆盖finalize()函数，在finalize()中输出打印信息，方便追踪。
	 * 说明：finalize()函数是在JVM回收内存时执行的，但JVM并不保证在回收内存时一定会调用finalize()
	 */
	private static class MyDate extends Date {
		private static final long serialVersionUID = 1L;

		// 覆盖finalize()方法
		protected void finalize() throws Throwable {
			super.finalize();
			System.out.println("obj [Date: " + this.getTime() + "] is gc");
		}

		public String toString() {
			return "Date: " + this.getTime();
		}
	}
}
