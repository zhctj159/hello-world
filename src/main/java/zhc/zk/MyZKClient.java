package zhc.zk;

import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class MyZKClient {
	private static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("127.0.0.1:2181")
			.sessionTimeoutMs(50000).connectionTimeoutMs(30000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	public static void main(String[] args) throws Exception {
		//创建会话
		client.start();

		//创建节点
		client.create().forPath("/China");
		client.create().forPath("/America", "zhangsan".getBytes());
		//创建一个初始内容为空的临时节点。EPHEMERAL：短暂的
		client.create().withMode(CreateMode.EPHEMERAL).forPath("/France");
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/Russia/car", "haha".getBytes());
		
		//异步创建节点
		client.create().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			@Override
			public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
				System.out.println("当前线程:" + Thread.currentThread().getName() + ",code:" + curatorEvent.getResultCode() + ",type:" + curatorEvent.getType());
			}
		}, Executors.newFixedThreadPool(10)).forPath("/async-curator-my");
		client.create().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			@Override
			public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
				System.out.println("当前线程:" + Thread.currentThread().getName() + ",code:" + curatorEvent.getResultCode() + ",type:" + curatorEvent.getType());
			}
		}).forPath("/async-curator-zookeeper");
		
		//获取节点内容
		byte[] data = client.getData().forPath("/America");
		System.out.println(new String(data));
		byte[] data2 = client.getData().storingStatIn(new Stat()).forPath("/America");
		System.out.println(new String(data2));
		
		//更新节点数据
		Stat stat = client.setData().forPath("/America");
		System.out.println(stat);
		client.setData().withVersion(1).forPath("/America", "lisi".getBytes());	//更新指定版本
		
		//删除节点
		client.delete().forPath("/China");
		client.delete().deletingChildrenIfNeeded().forPath("/Russia");
		client.delete().withVersion(0).forPath("/America");	//删除指定版本
		client.delete().guaranteed().forPath("/America");	//注意:由于一些网络原因,上述的删除操作有可能失败,使用guaranteed(),如果删除失败,会记录下来,只要会话有效,就会不断的重试,直到删除成功为止
	
		Thread.sleep(Integer.MAX_VALUE);
		
		client.close();
	}
}
