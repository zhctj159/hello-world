package zhc.tomcat;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static int port = 5228;
	private static String host = "127.0.0.1";
	
	// http://127.0.0.1:8080/zhc.tomcat/index.java?show
	public static void main(String[] args) {
		try {
			Socket socket = new Socket(host, port);
			System.out.println("请输入url地址：");
			Scanner scanner = new Scanner(System.in);
			String info = scanner.nextLine().trim();
			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write(info);
			writer.flush();
			writer.close();
			scanner.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
