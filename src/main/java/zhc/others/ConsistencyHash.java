package zhc.others;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistencyHash {
	private TreeMap<Long, Object> nodes = null;
	/** 真实服务器节点信息 */
	private List<Object> shards = new ArrayList<>();
	/** 设置虚拟节点数目 */
	private int VIRTUAL_NUM = 4;

	public void init() {
		shards.add("192.168.0.0-服务器0");
		shards.add("192.168.0.1-服务器1");
		shards.add("192.168.0.2-服务器2");
		shards.add("192.168.0.3-服务器3");
		shards.add("192.168.0.4-服务器4");

		nodes = new TreeMap<Long, Object>();
		for (int i = 0; i < shards.size(); i++) {
			Object shardInfo = shards.get(i);
			for (int j = 0; j < VIRTUAL_NUM; j++) {
				nodes.put(hash(md5("SHARD-" + i + "-NODE-" + j), j), shardInfo);
			}
		}
	}

	/** 根据key的hash值取得服务器节点信息 */
	public Object getShardInfo(long hash) {
		Long key = hash;
		SortedMap<Long, Object> tailMap = nodes.tailMap(key);
		if (tailMap.isEmpty()) {
			key = nodes.firstKey();
		} else {
			key = tailMap.firstKey();
		}
		return nodes.get(key);
	}

	/** 打印圆环节点数据 */
	public void printMap() {
		System.out.println(nodes);
	}

	/** 根据2^32把节点分布到圆环上面。 */
	public long hash(byte[] digest, int nTime) {
		long rv = ((long) (digest[3 + nTime * 4] & 0xFF) << 24) | ((long) (digest[2 + nTime * 4] & 0xFF) << 16)
				| ((long) (digest[1 + nTime * 4] & 0xFF) << 8) | (digest[0 + nTime * 4] & 0xFF);

		return rv & 0xffffffffL; /* Truncate to 32-bits */
	}
	
	/** 根据2^32把节点分布到圆环上面。 */
	public long hash2(byte[] digest) {
		long hash = 0;
		for (int i = 0; i < digest.length; i++) {
			hash = hash*31 + (long)(digest[i]&0xff);
		}
		return hash & 0xffffffff;
	}

	public byte[] md5(String key) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		}
		md5.reset();
		byte[] keyBytes = null;
		try {
			keyBytes = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unknown string :" + key, e);
		}
		md5.update(keyBytes);
		return md5.digest();
	}

	public static void main(String[] args) {
		ConsistencyHash consistencyHash = new ConsistencyHash();
		consistencyHash.init();
		consistencyHash.printMap();
		// 循环50次，是为了取50个数来测试效果，当然也可以用其他任何的数据来测试
		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			System.out.println(
					consistencyHash.getShardInfo(
							consistencyHash.hash(
									consistencyHash.md5(String.valueOf(i)), 
									random.nextInt(consistencyHash.VIRTUAL_NUM)
							)
					)
			);
		}
	}

}
