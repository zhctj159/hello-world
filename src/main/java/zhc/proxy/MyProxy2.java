package zhc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxy2 implements InvocationHandler {
	
	public static void main(String[] args) {
		MyProxy2 proxy = new MyProxy2();
		IMath math = (IMath)proxy.getProxy(new Math());
		math.add(5, 7);
	}

	private Object targetObject;
	
	public Object getProxy(Object object) {
		this.targetObject = object;
		return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("JDK动态代理-start");
		Object ret = method.invoke(targetObject, args);
		System.out.println("JDK动态代理-end");
		return ret;
	}
}
