package zhc.others;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AopProxyTest {
	
	public static void main(String[] args) {
		UserService userService = AopProxyTest.createService();
        userService.addUser();
	}
	
	public static UserService createService() {
		final UserService userService = new UserServiceImpl();
		final MyAspect myAspect = new MyAspect();
		return (UserService)Proxy.newProxyInstance(AopProxyTest.class.getClassLoader(), userService.getClass().getInterfaces(), new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				myAspect.before();
				Object obj = method.invoke(userService, args);
				myAspect.after();
				return obj;
			}
		});
	}
}

class MyAspect {
	public void before() {
		System.out.println("before");
	}
	
	public void after() {
		System.out.println("after");
	}
}

class UserServiceImpl implements UserService {
	@Override
	public void addUser() {
		System.out.println("a_proxy a_jdk add user......");
	}
}

interface UserService {
	void addUser();
}
