package zhc.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static int port = 5228;
	
	/**
	 * http://127.0.0.1:8080/zhc_tomcat/index.do!show?name=zhaihc3&age=32
	 * 协议、IP、端口、路径、类方法名
	 * @param args
	 */
	public static void main(String[] args) {
		//一个内部匿名类，继承于Object，包含一个方法getClassName()，其类名由所在类的类名加$符号和数字序号构成
		String clazzName = new Object(){
			public String getClassName(){
				String clazzName = this.getClass().getName();
				return clazzName.substring(0, clazzName.lastIndexOf("$"));
			}
		}.getClassName();
		System.out.println(clazzName);
		
		System.out.println(" My Tomcat is Running");
		try(ServerSocket server = new ServerSocket(port)) {
			while (true) {
				Socket socket = server.accept();// 服务器每接受一次请求，就创建一个socket对象
				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String info = "";
				String tStr = null;
				while ((tStr = br.readLine())!=null) {
					info = info + tStr;
				}
				System.out.println("info: " + info);
				//解析url
				int tag1 = info.indexOf(":");
				if (-1 == tag1) {
					continue;
				}
				int tag2 = info.indexOf(":",tag1+1);
				if (-1 == tag2) {
					continue;
				}
				int tag3 = info.indexOf("/", tag2);
				if (-1 == tag3) {
					continue;
				}
				int tag4 = info.indexOf("/",tag3+1);
				if (-1 == tag4) {
					continue;
				}
				int tag5 = info.indexOf(".",tag4);
				if (-1 == tag5) {
					continue;
				}
				String methodName = "index";
				int tag6 = info.indexOf("?");
				int tag7 = info.indexOf("!",tag5);
				if (-1 != tag2) {
					methodName = info.substring(tag7+1, tag6);
				}
				String protocol = info.substring(0, tag1);
				String host = info.substring(tag1 + 3, tag2);
				String port = info.substring(tag2 + 1, tag3);
				String path = info.substring(tag3 + 1, tag4);
				String className = info.substring(tag4 + 1, tag5);
				String parameter = info.substring(tag6 + 1, info.trim().length());
				if (host != null && path != null && className != null) {
					if (null==protocol) {
						protocol = "http";
					}
					if (null==port) {
						port = "8080";
					}
					try {
						if (path==null || !path.equals("zhc_tomcat")) {
							continue;
						}
						if (null==className || "".equals(className)) {
							continue;
						}
						if (className.equals("index")) {
							className = "Index";
						}
						Class<?> clazz = Class.forName("zhc.tomcat."+className);
						if (parameter!=null) {
							String[] kvs = parameter.split("&");
							Method method=clazz.getMethod(methodName,String.class,String.class);
							method.invoke(clazz.newInstance(),kvs[0].split("=")[1],kvs[1].split("=")[1]);
						} else {
							Method method=clazz.getMethod(methodName);
							method.invoke(clazz.newInstance());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Index {
	public void show(){
		System.out.println("Hello,World!");
	}
	
	public void show(String name,String age){
		System.out.println("Hello,World! ["+name+","+age+"]");
	}
}


