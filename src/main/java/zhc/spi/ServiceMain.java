package zhc.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ServiceMain {
	public static void main(String[] args) {
		//load的时候从META-INF/services/下找对应的实现
		ServiceLoader<DubboService> spiLoader = ServiceLoader.load(DubboService.class);
		Iterator<DubboService> iter = spiLoader.iterator();
		while (iter.hasNext()) {
			DubboService dubboService = iter.next();
			dubboService.sayHello();
		}
	}
}
