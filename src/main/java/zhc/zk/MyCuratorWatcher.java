package zhc.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class MyCuratorWatcher {
	private static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("127.0.0.1:2181").sessionTimeoutMs(50000).connectionTimeoutMs(30000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
	
	public static void main(String[] args) throws Exception {
		//创建会话
		client.start();
		client.create().creatingParentsIfNeeded().forPath("/book/computer", "java".getBytes());
		
		//监听指定节点本身的变化
		NodeCache nodeCache = new NodeCache(client, "/book/computer");
		nodeCache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				System.out.println("新的节点数据:" + new String(nodeCache.getCurrentData().getData()));
			}
		});
		nodeCache.start(true);
		nodeCache.close();
		
		client.setData().forPath("/book/computer","c++".getBytes());
		
		//监听子节点变化情况
		PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/book/computer", true);
		pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
				switch (pathChildrenCacheEvent.getType()) {
				case CHILD_ADDED:
					System.out.println("新增子节点:" + pathChildrenCacheEvent.getData().getPath());
					break;
				case CHILD_UPDATED:
					System.out.println("子节点数据变化:" + pathChildrenCacheEvent.getData().getPath());
					break;
				case CHILD_REMOVED:
					System.out.println("删除子节点:" + pathChildrenCacheEvent.getData().getPath());
					break;
				default:
					break;
				}
			}
		});
		pathChildrenCache.start();
		pathChildrenCache.close();
		
		client.create().forPath("/book13");
		client.create().forPath("/book13/car", "bmw".getBytes());
		client.setData().forPath("/book13/car", "audi".getBytes());
		client.delete().forPath("/book13/car");
	}
}
