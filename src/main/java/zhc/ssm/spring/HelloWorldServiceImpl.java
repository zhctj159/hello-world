package zhc.ssm.spring;

public class HelloWorldServiceImpl implements HelloWorldService {
	public String getBeanName() {
		return "BeanName";
	}
	
	public void sayHello() {
		System.out.println("hello");
	}
}
