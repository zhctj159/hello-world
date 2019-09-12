package zhc.ssm.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 一般情况下，Spring通过反射机制利用<bean>的class属性指定实现类实例化Bean，在某些情况下，实例化Bean过程比较复杂，
 * 如果按照传统的方式，则需要在<bean>中提供大量的配置信息。配置方式的灵活性是受限的，这时采用编码的方式可能会得到一个简单的方案。
 * Spring为此提供了一个org.springframework.bean.factory.FactoryBean的工厂类接口，用户可以通过实现该接口定制实例化Bean的逻辑。
 * FactoryBean接口对于Spring框架来说占用重要的地位，Spring自身就提供了70多个FactoryBean的实现。
 * 它们隐藏了实例化一些复杂Bean的细节，给上层应用带来了便利。从Spring3.0开始，FactoryBean开始支持泛型，即接口声明改为FactoryBean<T>的形式
 * @author zhc
 * @time 2019年8月9日 下午5:00:04
 */
public class MyFactoryBean implements FactoryBean<Object>,InitializingBean,DisposableBean{

	private String interfaceName;
	private Object target;
	private Object proxyObj;
	
	@Override
	public void destroy() throws Exception {
		System.out.println("destroy......");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		proxyObj = Proxy.newProxyInstance(
						getClass().getClassLoader(), 
						new Class[]{Class.forName(interfaceName)}, 
						new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println("invoke method: " + method.getName()+", curTime: "+System.currentTimeMillis());
				Object ret = method.invoke(target, args);
				System.out.println("invoke method: " + method.getName()+", curTime: "+System.currentTimeMillis());
				return ret;
			}
		});
	}

	@Override
	public Object getObject() throws Exception {
		System.out.println("getObject......");
		return proxyObj;
	}

	@Override
	public Class<?> getObjectType() {
		return proxyObj==null ? Object.class : proxyObj.getClass();
	}
	
	@Override
	public boolean isSingleton() {
		return FactoryBean.super.isSingleton();
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getProxyObj() {
		return proxyObj;
	}

	public void setProxyObj(Object proxyObj) {
		this.proxyObj = proxyObj;
	}
}
