package zhc.proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class MyProxy3 implements MethodInterceptor {
	
	public static void main(String[] args) {
		MyProxy3 proxy = new MyProxy3();
		Math math = (Math)proxy.getProxy(new Math());
		math.add(5, 7);
		math.myMethod();
	}

	private Object targetObject;
	
	public Object getProxy(Object object) {
		this.targetObject = object;
		
		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(this);
		enhancer.setSuperclass(object.getClass());
		return enhancer.create();
	}
	
	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		System.out.println("CGLIB动态代理-start");
		Object ret = method.invoke(targetObject, args);
		System.out.println("CGLIB动态代理-end");
		return ret;
	}

}
