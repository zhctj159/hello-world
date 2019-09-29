package zhc.lock;

import java.util.Collections;

import redis.clients.jedis.Jedis;

/**
 * ClassName: zhc.lock.RedisLockDistributed 
 * @Description: TODO
 * @author zhc
 * @date 2019年9月27日
 */
public final class RedisLockDistributed {
	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";
	private static final String OK = "OK";
	private static final Long RELEASE_SUCCESS = 1L;

	/**
	 * 尝试获取分布式锁
	 * @param jedis  Redis客户端
	 * @param lockKey  锁
	 * @param requestId  请求标识
	 * @param expireTime  超期时间
	 * @return  是否获取成功
	 */
	public static boolean tryGetLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
		System.out.println("jedis>>>>>>>>" + jedis);
		//以下的命令不能使用setnx和setex两条命令替换。避免破坏原子性。
		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
		System.out.println("redisLockResult>>>>>>>" + result);
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
	
	public boolean tryGetLock2(Jedis jedis,String lockKey,String requestId,int expireTime) {
		String oo = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
		if (OK.equals(oo)) {
			return true;
		}
		return false;
	}

	/**
	 * 释放分布式锁
	 * @param jedis  redis客户端
	 * @param lockKey  锁
	 * @param requestId  请求标识
	 * @return  是否释放成功
	 */
	public static boolean releaseLock(Jedis jedis, String lockKey, String requestId) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
}
