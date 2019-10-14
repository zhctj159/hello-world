package zhc.others;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * ClassName: zhc.others.MyClassLoaderTest
 * 
 * @author zhc
 * @date 2019年10月12日
 */
public class MyClassLoaderTest {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		//"C:/Users/zhc/Workspaces/MyEclipse 2017 CI/zhc_0903/target/classes/"
		MyClassLoader classLoader1 = new MyClassLoader("C:/Users/zhc/Workspaces/MyEclipse 2017 CI/zhc_0903/target/classes/");
		MyClassLoader classLoader2 = new MyClassLoader("C:/Users/zhc/Workspaces/MyEclipse 2017 CI/zhc_0903/target/classes/");
//		Class<?> c = Class.forName("zhc.others.Hello", true, classLoader);
		Class<?> c0 = MySystem.class;
		System.out.println("c0的加载器："+c0.getClassLoader());
		Class<?> c1 = classLoader1.findClass("zhc.others.MySystem");
		System.out.println("c1的加载器："+c1.getClassLoader());
		Class<?> c2 = classLoader2.findClass("zhc.others.MySystem");
		System.out.println("c2的加载器："+c2.getClassLoader());
		System.out.println("c1==c0  "+(c1==c0));
		System.out.println("c1.equals(c0)  " + c1.equals(c0));
		System.out.println("c1==c2  "+(c1==c2));
		System.out.println("c1.equals(c2)  " + c1.equals(c2));
		
		Method method = c1.getDeclaredMethod("main", String[].class);
		method.invoke(null, (Object) new String[] { "hello", "world" });
	}
}

class MyClassLoader extends ClassLoader {
	private String path;

	public MyClassLoader(String path) {
		this.path = path;
	}
	
//	@Override
//	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//		if (!name.startsWith("zhc.others.")) {
//			return super.loadClass(name, resolve);
//		}
//		File file = new File(this.path + name.replaceAll("\\.", "/") + ".class");
//		byte[] bytes = new byte[0];
//		try {
//			// 将class文件读入字节数据中
//			bytes = getClassBytes(file);
//		} catch (Exception ee) {
//			ee.printStackTrace();
//		}
//		// 写入内存返回Class对象
//		return this.defineClass(name, bytes, 0, bytes.length);
//	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		File file = new File(this.path + name.replaceAll("\\.", "/") + ".class");
		byte[] bytes = new byte[0];
		try {
			// 将class文件读入字节数据中
			bytes = getClassBytes(file);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		// 写入内存返回Class对象
		return this.defineClass(name, bytes, 0, bytes.length);
	}

	private byte[] getClassBytes(File file) throws Exception {
		// 这里要读入.class的字节，因此要使用字节流
		FileInputStream fis = new FileInputStream(file);
		FileChannel fc = fis.getChannel();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		WritableByteChannel wbc = Channels.newChannel(baos);
		ByteBuffer by = ByteBuffer.allocate(1024);

		while (true) {
			int i = fc.read(by);
			if (i == 0 || i == -1)
				break;
			by.flip();
			wbc.write(by);
			by.clear();
		}

		fis.close();

		return baos.toByteArray();
	}
}

