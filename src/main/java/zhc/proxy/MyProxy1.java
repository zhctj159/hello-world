package zhc.proxy;

/**
 * 这里实际是装饰器模式。装饰器模式与代理模式区别：前者可以层层叠加，由外部传入被装饰对象；后者是在内部自己创建被代理对象。
 * @author zhc
 * @time 2019年8月9日 下午3:52:55
 */
public class MyProxy1 implements IMath {
	
	public static void main(String[] args) {
		MyProxy1 proxy = new MyProxy1();
		IMath math = proxy.getProxy(new Math());
		math.add(5, 7);
	}

	private IMath targetObject;
	
	public IMath getProxy(IMath object) {
		this.targetObject = object;
		return this;
	}
	
	@Override
	public int add(int a, int b) {
		System.out.println("JDK静态代理-start");
		int ret = targetObject.add(a, b);
		System.out.println("JDK静态代理-end");
		return ret;
	}

}
