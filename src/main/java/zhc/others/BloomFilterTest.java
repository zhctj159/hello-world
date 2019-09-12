package zhc.others;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloomFilterTest {
	
	private static int size = 10000000;
	private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, 0.001);

	public static void main(String[] args) {
		long startTime, endTime;
//		startTime = System.nanoTime();
		startTime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			bloomFilter.put(i);
		}
		endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "毫秒");

		// 故意取100000个不在过滤器里的值，看看有多少个会被认为在过滤器里
		startTime = System.currentTimeMillis();
		int count = 0;
		for (int i = size; i < size + 10000000; i++) {
			if (bloomFilter.mightContain(i)) {
				count++;
			}
		}
		System.out.println("误判的数量：" + count);
		endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "毫秒");
	}
}
