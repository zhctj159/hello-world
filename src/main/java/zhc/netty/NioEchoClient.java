package zhc.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * ClassName: zhc.netty.NioEchoClient 
 * @Description: TODO
 * @author zhc
 * @date 2019年9月27日
 */
public class NioEchoClient {
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		try (SocketChannel clientChannel = SocketChannel.open()) {
			clientChannel.connect(new InetSocketAddress("127.0.0.1",8011));
			ByteBuffer buffer = ByteBuffer.allocate(50);
			boolean flag = true;
			while (flag) {
				buffer.clear();
				System.out.print("请输入：");
				String inputData = scanner.next();
				buffer.put(inputData.getBytes());
				buffer.flip();
				clientChannel.write(buffer);
				buffer.clear();
				int readCount = clientChannel.read(buffer);
				buffer.flip();
				System.err.println(new String(buffer.array(),0,readCount));
				if ("byebye".equals(inputData)) {
					flag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
