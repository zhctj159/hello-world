package zhc.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOEchoServer {
	public static void main(String[] args) throws IOException {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			ExecutorService executorService = Executors.newFixedThreadPool(10);
			serverSocketChannel.configureBlocking(false);	//非阻塞模式
			serverSocketChannel.bind(new InetSocketAddress(8011));
			Selector selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("服务器已经启动成功，服务器的监听端口为：8011");

			@SuppressWarnings("unused")
			int keySelect = 0;	//接收轮询状态
			while ((keySelect=selector.select()) >0) {
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> selectIter = selectionKeys.iterator();
				while (selectIter.hasNext()) {
					SelectionKey selectionKey = selectIter.next();
					if (selectionKey.isAcceptable()) {
						SocketChannel clientChannel = serverSocketChannel.accept();
						if (clientChannel!=null) {
							executorService.submit(new EchoClientHandler(clientChannel));
						}
					}
					selectIter.remove();
				}
			}
			executorService.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static class EchoClientHandler implements Runnable {
		private SocketChannel clientChannel;
		private boolean flag = true;
		
		public EchoClientHandler(SocketChannel clientChannel) {
			this.clientChannel = clientChannel;
		}
		
		@Override
		public void run() {
			ByteBuffer buffer = ByteBuffer.allocate(50);
			try {
				while (flag) {
					buffer.clear();
					int readCount = clientChannel.read(buffer);
					String readMessage = new String(buffer.array(),0,readCount).trim();
					String writeMessage = "[ECHO]"+readMessage+"\n";
					if ("byebye".equals(readMessage)) {
						writeMessage = "[EXIT]拜拜，下次见";
					}
					buffer.clear();
					buffer.put(writeMessage.getBytes());
					buffer.flip();
					clientChannel.write(buffer);
				}
				clientChannel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
