package zhc.others;

/**
 * 分布式系统唯一ID
 * 
 * @author zhc
 * @time 2019年7月25日 上午11:31:19
 */
public class UniqueIdDistributed {

	public static void main(String[] args) {

	}

	/** 1.UUID */
	public void testUUID() {

	}

	/** 2.数据库生成 */
	public void testDB() {

	}

	/** 3.Redis生成ID */
	public void testRedis() {

	}

	/** 4.利用zookeeper生成唯一ID */
	public void testZK() {

	}

	/**
	 * 5.snowflake(雪花算法) MongoDB官方文档
	 * ObjectID可以算作是和snowflake类似方法，通过“时间+机器码+pid+inc”共12个字节，通过4+3+2+3的方式最终标识成一个24长度的十六进制字符。
	 */
	public void testSnowflake() {

	}
	/** 6.百度开源的分布式ID生成器，基于snowflake算法的实现 */
	public void testUidGenerator() {

	}
	/** 7.美团开源的分布式ID生成器，需要依赖关系数据库Zookeeper等中间件 */
	public void testLeaf() {

	}
	/**
	 * 总长度64位，从低位到高位依次是：
	 * 1、0~11位(共12bit)表示序列号，最大值2^12=4096，意味着在一个时间单位内(毫秒)内最多可以生成的4096个ID
	 * 2、12~21位(共10bit)表示机器id，其中5位表示dataCenterId，5位表示workerId
	 * 3、22~62位(共41bit)表示时间戳，单位毫秒
	 * 4、63位(共1bit)最高位置0，表示正数
	 * @author zhc
	 * @time 2019年7月25日 下午2:24:23
	 */
	public class IdWorker {
		/** 数据标识ID（0～31） */
		private long dataCenterId;
		/** 机器ID（0～31） */
		private long workerId;
		/** 毫秒内序列（0～4095） */
		private long sequence;
		/** 上次生成ID的时间戳 */
		private long lastTimestamp = -1L;

		public IdWorker(long dataCenterId, long workerId) {
			if (dataCenterId > 31 || dataCenterId < 0) {
				throw new IllegalArgumentException("dataCenterId can't be greater than 31 or less than 0");
			}
			if (workerId > 31 || workerId < 0) {
				throw new IllegalArgumentException("worker Id can't be greater than 31 or less than 0");
			}
			this.dataCenterId = dataCenterId;
			this.workerId = workerId;
		}

		/** 获得下一个ID (该方法是线程安全的) */
		public synchronized long nextId() {
			long timestamp = timeGen();
			// 如果当前时间小于上一次ID生成的时间戳,说明系统时钟回退过,这个时候应当抛出异常
			if (timestamp < lastTimestamp) {
				throw new RuntimeException(
						String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
								lastTimestamp - timestamp));
			}
			// 如果是同一时间生成的，则进行毫秒内序列
			if (timestamp == lastTimestamp) {
				long sequenceMask = 4095;
				sequence = (sequence + 1) & sequenceMask;
				// 毫秒内序列溢出
				if (sequence == 0) {
					// 阻塞到下一个毫秒,获得新的时间戳
					timestamp = nextMillis(lastTimestamp);
				}
			} else {
				// 时间戳改变，毫秒内序列重置
				sequence = 0L;
			}
			lastTimestamp = timestamp;
			// 移位并通过按位或运算拼到一起组成64位的ID
			long epoch = 1491004800000L;		//起始时间戳：1970-04-01
			return ((timestamp - epoch) << 22) | (dataCenterId << 17)
					| (workerId << 12) | sequence;
		}

		/** 返回以毫秒为单位的当前时间 */
		protected long timeGen() {
			return System.currentTimeMillis();
		}

		/** 阻塞到下一个毫秒，直到获得新的时间戳 */
		protected long nextMillis(long lastTimestamp) {
			long timestamp = timeGen();
			while (timestamp <= lastTimestamp) {
				timestamp = lastTimestamp;
			}
			return timestamp;
		}
	}
}
