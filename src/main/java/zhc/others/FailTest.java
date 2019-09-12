package zhc.others;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

/**
 * fast-fail事件产生的条件：当多个线程对Collection进行操作时，若其中某一个线程通过iterator去遍历集合时，该集合的内容被其他线程所改变；则会抛出ConcurrentModificationException异常。
 * fast-fail解决办法：通过util.concurrent集合包下的相应类去处理，则不会产生fast-fail事件。
 * @author zhc
 * @time 2019年7月29日 上午8:54:00
 */
public class FailTest {
//	private static List<String> list = new ArrayList<String>();
	private static List<String> list = new CopyOnWriteArrayList<String>();
	private static ThreadLocal<List<String>> local = new ThreadLocal<List<String>>(){
		protected List<String> initialValue() {
			return new ArrayList<String>();
		};
	};
	
	private static int size = 10;
	public static void main(String[] args) {
//		new ThreadOne().start();
//		new ThreadTwo().start();
		
		for (int i = 0; i < size; i++) {
			list.add(String.valueOf(i));
		}
		new ThreadOne2().start();
		new ThreadTwo2().start();
	}
	
	@Test
    public void test() {
        ConcurrentHashMap<String, String> phones = new ConcurrentHashMap<>();
        phones.put("Apple", "iPhone");
        phones.put("Samsung", "Galaxy");
        phones.put("Moto", "Z Play");

        Iterator<String> iterator = phones.keySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(phones.get(iterator.next()));
            phones.put("Smartisan", "M1L");
        }
    }
	
	private static void printAll() {
		String value = null;
		List<String> list = local.get();
		Iterator<String> iter = list.iterator();
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(Thread.currentThread().getName()).append(": ");
		while (iter.hasNext()) {
			value = (String) iter.next();
			sb.append(value).append(",");
		}
		System.out.println(sb.toString()+"]");
	}
	
	static class ThreadOne2 extends Thread {
		@Override
		public void run() {
			Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
		}
	}
	
	static class ThreadTwo2 extends Thread {
		@Override
		public void run() {
			for (int i = size-1; i >= 0; i--) {
                list.remove(i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
		}
	}
	
	static class ThreadOne extends Thread {
		@Override
		public void run() {
			int i = 0;
			List<String> list = local.get();
			while (i<6) {
				list.add(String.valueOf(i));
				printAll();
				i++;
			}
		}
	}
	
	static class ThreadTwo extends Thread {
		@Override
		public void run() {
			int i = 10;
			List<String> list = local.get();
			while (i<16) {
				list.add(String.valueOf(i));
				printAll();
				i++;
			}
		}
	}
}
