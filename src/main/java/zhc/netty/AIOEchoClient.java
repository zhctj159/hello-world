package zhc.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class AIOEchoClient {
	public static void main(String[] args) throws IOException {
		new AIOEchoClient().test1();
	}


	public void test1() throws IOException {
		AIOClientThread client = new AIOClientThread();
		new Thread(client).start();
		while (client.sendMessage(getString("请输入要发送的内容："))) {
		}
	}

	private static Scanner scanner = new Scanner(System.in);
	public String getString(String prompt) throws IOException {
		System.out.print(prompt);
		return scanner.next();
	}

	class AIOClientThread implements Runnable {
		private AsynchronousSocketChannel clientChannel;
		private CountDownLatch latch;

		public AIOClientThread() {
			try {
				clientChannel = AsynchronousSocketChannel.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clientChannel.connect(new InetSocketAddress("127.0.0.1", 8011));
			latch = new CountDownLatch(1);
		}

		@Override
		public void run() {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}

		public boolean sendMessage(String msg) {
			ByteBuffer buffer = ByteBuffer.allocate(100);
			buffer.put(msg.getBytes());
			buffer.flip();
			clientChannel.write(buffer, buffer, new ClientWriteHandler(clientChannel));
			if ("byebye".equalsIgnoreCase(msg)) {
				latch.countDown();
				return false;
			}
			return true;
		}
	}

	class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
		private AsynchronousSocketChannel clientChannel;

		public ClientWriteHandler(AsynchronousSocketChannel clientChannel) {
			this.clientChannel = clientChannel;
		}

		@Override
		public void completed(Integer result, ByteBuffer buffer) {
			if (buffer.hasRemaining()) {
				clientChannel.write(buffer, buffer, this);
			} else {
				ByteBuffer readBuffer = ByteBuffer.allocate(100); // 读取服务端回应
				clientChannel.read(readBuffer, readBuffer, new ClientReadHandler());
			}
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

	class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
		@Override
		public void completed(Integer result, ByteBuffer buffer) {
			buffer.flip();
			String readMessage = new String(buffer.array(), 0, buffer.remaining());
			System.out.println(readMessage); // 输出读取内容
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
		}
	}
}
