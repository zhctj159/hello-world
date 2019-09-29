package zhc.ssm.spring;

public class HelloWorldServiceImpl implements HelloWorldService {
	
	@Override
	public String getBeanName() {
		return "BeanName";
	}
	
	@Override
	public void sayHello() {
		System.out.println("hello");
	}
}
