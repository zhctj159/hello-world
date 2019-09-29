package zhc.spi;

public class YellowDubboServiceImpl implements DubboService {

	@Override
	public void sayHello() {
		System.out.println("我是YellowService服务实现");
	}

}
