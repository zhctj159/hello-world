package zhc.ssm.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class MyFactoryBeanTest {

	@Autowired
	private ApplicationContext context;

	/**
	 * 测试验证FactoryBean原理，代理一个servcie在调用其方法的前后，打印日志亦可作其他处理
	 * 从ApplicationContext中获取自定义的FactoryBean context.getBean(String beanName)
	 * ---> 最终获取到的Object是FactoryBean.getObejct(),
	 * 使用Proxy.newInstance生成service的代理类
	 */
	@Test
	public void testFactoryBean() {
		HelloWorldService helloWorldService = (HelloWorldService) context.getBean("fbHelloWorldService");
		helloWorldService.getBeanName();
		helloWorldService.sayHello();
		
		// 当调用getBean(beanName)时，Spring通过反射机制发现MyFactoryBean实现了FactoryBean的接口，则调用MyFactoryBean#getObject()方法返回。  
		Object obj1 = context.getBean("fbHelloWorldService");
		System.err.println(obj1.getClass().getName());
		// 如果希望获取MyFactoryBean的实例，则需要在使用getBean(beanName)方法时在beanName前显示的加上"&"前缀
		Object obj2 = context.getBean("&fbHelloWorldService");
		System.err.println(obj2.getClass().getName());
	}
}
