package zhc.lock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 利用节点名称的唯一性来实现独占锁
 * 
 * ZooKeeper机制规定同一个目录下只能有一个唯一的文件名，zookeeper上的一个znode看作是一把锁，通过createznode的方式来实现。所有客户端都去创建/lock/${lock_name}_lock节点，最终成功创建的那个客户端也即拥有了这把锁，创建失败的可以选择监听继续等待，还是放弃抛出异常实现独占锁。
 * @author zhc
 * @time 2019年7月9日 下午4:12:21
 */
public class ZookeeperLock implements Lock, Watcher {
	public static void main(String[] args) {
		ZookeeperLock lock = null;
		try {
		    lock = new ZookeeperLock("127.0.0.1:2181","test1");
		    lock.lock();
		    //业务逻辑处理
		    System.out.println("Hello,World!1");
		    Thread.sleep(3000);
		    System.out.println("Hello,World!2");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    if(lock != null) {
		        lock.unlock();
		    }
		}
	}
	
	
	private ZooKeeper zk;
	private String root = "/locks";
	private String lockName;	//竞争资源的标志
	private String myZnode;		//当前锁
	private int sessionTimeout = 30000;
	private List<Exception> exceptions = new ArrayList<Exception>();
	
	public ZookeeperLock(String connectString, String lockName) {
		this.lockName = lockName;
		try {
			zk = new ZooKeeper(connectString, sessionTimeout, this);
			Stat stat = zk.exists(root, false);
			if (null==stat) {
				zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (IOException e) {
            exceptions.add(e);
        } catch (KeeperException e) {
            exceptions.add(e);
        } catch (InterruptedException e) {
            exceptions.add(e);
            Thread.currentThread().interrupt();
        }
	}
	
	@Override
	public void process(WatchedEvent event) {

	}

	@Override
	public void lock() {
		if (exceptions.size()>0) {
			throw new RuntimeException("LockExecption");
		}
		if (!tryLock()) {
			throw new RuntimeException("LockExecption: 您的操作太频繁，请稍后再试！");
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	@Override
	public boolean tryLock() {
		try {
            myZnode = zk.create(root + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return tryLock();
	}

	@Override
	public void unlock() {
		try {
            zk.delete(myZnode, -1);
            myZnode = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
	}

	@Override
	public Condition newCondition() {
		return null;
	}

}
