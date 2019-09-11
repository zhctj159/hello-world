package zhc.others;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class MySystem {
//	static {
//		System.out.println(System.getProperty("java.library.path"));
//		//zhc_others_MySystem在jdk的bin目录下
//		System.loadLibrary("zhc_others_MySystem");
//	}
	
	public static native void setOut(PrintStream out);
	
	public static final PrintStream out2 = null;
	
	public static final PrintStream out = newPrintStream();
	
	private static PrintStream newPrintStream() {
		PrintStream ps = null;
		try (FileOutputStream fos = new FileOutputStream(FileDescriptor.out)) {
			ps = new PrintStream(new BufferedOutputStream(fos, 128), true, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ps;
	}
	
	private MySystem() {}
	
	public static void main(String[] args) {
		MySystem.out.println("Hello,World! -- print by MySystem.out");
		
		//以下为通过文件载入，两种获取绝对路径方式
		String tPath = MySystem.class.getResource("")+"zhc_others_MySystem.dll";
		System.load(tPath.substring(6).replace("/", "\\").replace("%20", " "));
//		System.load(new File("").getAbsolutePath()+"\\target\\classes\\zhc\\others\\zhc_others_MySystem.dll");
		//以下为通过库名载入
//		System.loadLibrary("zhc_others_MySystem");
		//载入库之后，使用java native方法调用库函数对final变量重新赋值
		setOut(newPrintStream());
		//测试效果，如果没载入则提示空指针异常
		if (null!=out2) {
			out2.println("Hello,World2! -- print by out2(final PrintStream out2 = null)");
		}
	}
}
