package zhc.spi;

public class RedDubboServiceImpl implements DubboService {

	@Override
	public void sayHello() {
		System.out.println("我是RedService服务实现");
	}

}
