package zhc.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AIOEchoServer{
	
	public static void main(String[] args) throws Exception {
		new AIOEchoServer().test1();
	}
	
	public void test1() throws Exception {
		new Thread(new AIOServerThread()).start();
	}
	
	class AIOServerThread implements Runnable {
		private AsynchronousServerSocketChannel serverChannel;
		private CountDownLatch latch;
		public AIOServerThread() throws Exception {
			latch = new CountDownLatch(1);
			serverChannel = AsynchronousServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(8011));
			System.out.println("服务器启动成功，监听端口为：8011");
		}
		
		public AsynchronousServerSocketChannel getServerChannel() {
			return serverChannel;
		}
		
		public CountDownLatch getLatch() {
			return latch;
		}
		
		@Override
		public void run() {
			System.out.println("AIOServerThread run()");
			serverChannel.accept(this, new AcceptHandler());
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			System.out.println("AIOServerThread run() end");
		}
	}
	
	class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AIOServerThread> {

		@Override
		public void completed(AsynchronousSocketChannel clientChannel, AIOServerThread aioThread) {
			System.out.println("AcceptHandler completed()");
			aioThread.getServerChannel().accept(aioThread, this);	//接收连接
			ByteBuffer buffer = ByteBuffer.allocate(50);
			clientChannel.read(buffer, buffer, new EchoHandler(clientChannel));
		}

		@Override
		public void failed(Throwable exc, AIOServerThread attachment) {
			System.err.println("客户端连接创建失败...");
			attachment.getLatch().countDown();
		}
		
	}
	
	class EchoHandler implements CompletionHandler<Integer, ByteBuffer> {
		private AsynchronousSocketChannel clientChannel;
		private boolean exit = false;
		public EchoHandler(AsynchronousSocketChannel clientChannel) {
			this.clientChannel = clientChannel;
		}

		@Override
		public void completed(Integer result, ByteBuffer buffer) {
			buffer.flip();
			String readMessage = new String(buffer.array(),0,buffer.remaining());
			String writeMessage = "[ECHO]"+readMessage;
			if ("byebye".equals(readMessage)) {
				writeMessage = "[EXIT]拜拜，下次再见";
				this.exit = true;
			}
			echoWrite(writeMessage);
		}
		
		private void echoWrite(String content) {
			ByteBuffer buffer = ByteBuffer.allocate(50);
			buffer.put(content.getBytes());
			buffer.flip();
			clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer buf) {
					if (buf.hasRemaining()) {	//缓存中是否有数据
						EchoHandler.this.clientChannel.write(buffer,buffer,this);
					} else {
						if (EchoHandler.this.exit == false) {
							ByteBuffer readBuffer = ByteBuffer.allocate(50);
							EchoHandler.this.clientChannel.read(readBuffer, readBuffer, new EchoHandler(EchoHandler.this.clientChannel));
						}
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						EchoHandler.this.clientChannel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			try {
				clientChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
