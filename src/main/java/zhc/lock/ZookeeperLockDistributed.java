package zhc.lock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
 * 利用临时顺序节点控制时序实现
 * 
 * /lock已经预先存在，所有客户端在它下面创建临时顺序编号目录节点，和选master一样，编号最小的获得锁，用完删除，依次方便。
算法思路：对于加锁操作，可以让所有客户端都去/lock目录下创建临时顺序节点，如果创建的客户端发现自身创建节点序列号是/lock/目录下最小的节点，则获得锁。否则，监视比自己创建节点的序列号小的节点（比自己创建的节点小的最大节点），进入等待。
对于解锁操作，只需要将自身创建的节点删除即可。

 * @author zhc
 * @time 2019年7月9日 下午4:13:38
 */
public class ZookeeperLockDistributed implements Lock, Watcher {
	
	public static void main(String[] args) {
		ZookeeperLockDistributed lock = null;
		try {
		    lock = new ZookeeperLockDistributed("127.0.0.1:2181","test1");
		    lock.lock();
		    //业务逻辑处理
		    System.out.println("Hello,World!1");
		    Thread.sleep(10000);
		    System.out.println("Hello,World!2");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    if(lock != null)
		        lock.unlock();
		}
	}

	private ZooKeeper zk;
    private String root = "/locks";
    private String lockName;	//竞争资源的标志
    private String waitNode;	//等待前一个锁
    private String myZnode;		//当前锁
    private CountDownLatch latch;	//计数器
    private int sessionTimeout = 30000;
    private List<Exception> exceptions = new ArrayList<Exception>();
    
    public ZookeeperLockDistributed(String connectString, String lockName) {
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
        	Thread.currentThread().interrupt();
            exceptions.add(e);
        }
    }
    
	@Override
	public void process(WatchedEvent event) {
		System.out.println("process");
		if (null!=latch) {
			latch.countDown();
		}
	}

	@Override
	public void lock() {
		if (exceptions.size()>0) {
			throw new RuntimeException("LockException");
		}
		try {
			if (tryLock()) {
				System.out.println("Thread " + Thread.currentThread().getId() + " " +myZnode + " get lock true");
				return;
			}
		} catch (Exception e) {
			throw new RuntimeException("LockException", e);
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		lock();
	}

	@Override
	public boolean tryLock() {
		try {
            String splitStr = "_lock_";
            if(lockName.contains(splitStr))
                throw new RuntimeException("LockException: lockName can not contains \\u000B");
            //创建临时子节点
            myZnode = zk.create(root + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(myZnode + " is created ");
            //取出所有子节点
            List<String> subNodes = zk.getChildren(root, false);
            //取出所有lockName的锁
            List<String> lockObjNodes = new ArrayList<String>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if(_node.equals(lockName)){
                    lockObjNodes.add(node);
                }
            }
            Collections.sort(lockObjNodes);
            System.out.println(myZnode + "==" + lockObjNodes.get(0));
            if(myZnode.equals(root+"/"+lockObjNodes.get(0))){
                //如果是最小的节点,则表示取得锁
                return true;
            }
            //如果不是最小的节点，找到比自己小1的节点
            String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
            waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
        } catch (KeeperException e) {
        	throw new RuntimeException("LockException", e);
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        	throw new RuntimeException("LockException", e);
        }
        return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		try {
            if(tryLock()){
                return true;
            }
            return waitForLock(waitNode,time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}

	private boolean waitForLock(String lower, long waitTime) throws InterruptedException, KeeperException {
        Stat stat = zk.exists(root + "/" + lower,true);
        //判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
        if(stat != null){
            System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + root + "/" + lower);
            latch = new CountDownLatch(1);
            latch.await(waitTime, TimeUnit.MILLISECONDS);
            latch = null;
        }
        return true;
    }
	
	@Override
	public void unlock() {
		try {
            System.out.println("unlock " + myZnode);
            if (null!=zk.exists(myZnode, false)) {
            	zk.delete(myZnode,-1);
			}
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
