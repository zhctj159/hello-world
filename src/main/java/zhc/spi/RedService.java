package zhc.spi;

public class RedService implements DubboService {

	@Override
	public void sayHello() {
		System.out.println("我是RedService服务实现");
	}

}
