package zhc.pattern.singleton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
//import java.lang.reflect.Constructor;

public class SingletonTest {
	public static void main(String[] args) throws Exception {
		test1(Singleton4.getInstance());
		test2(Singleton4.class);
		test3();
	}
	
	/** 反射破坏单例测试-enum */
	public static void test3() throws Exception {
		// 反射破坏单例
		Singleton5 instance = Singleton5.instance;
		Constructor<Singleton5> con = Singleton5.class.getDeclaredConstructor(String.class,int.class);
		con.setAccessible(true);
		Singleton5 obj = con.newInstance("zhaihc3", 32);
		System.out.println("instance==obj ===> " + (instance == obj));
	}
	
	/** 反射破坏单例测试 */
	public static void test2(Class<?> clazz) throws Exception {
		Method method = clazz.getDeclaredMethod("getInstance");
		Object instance = method.invoke(clazz);

		Constructor<?> con = clazz.getDeclaredConstructor();
		con.setAccessible(true);
		Object obj = con.newInstance();
		
		System.out.println(instance);
		System.out.println(obj);
	}
	
	/** 反序列化破坏单例测试 */
	public static void test1(Object instance) {
		//反序列化
		String fileName = "temp.temp";
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
			oos.writeObject(instance);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
			Object obj = ois.readObject();
			System.out.println(instance);
			System.out.println(obj);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
}
