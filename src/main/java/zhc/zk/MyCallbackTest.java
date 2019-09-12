package zhc.zk;

public class MyCallbackTest {
	public static void main(String[] args) {
		MyCaller caller = new MyCaller();
		caller.setCallfunc(new MyCallbackImpl());
		caller.exec(4);
	}
}

class MyCaller {
	public MyCallback mc;
	
	public void setCallfunc(MyCallback mc) {
		this.mc = mc;
	}
	public void exec(int x) {
		int y = x*x;
		System.out.println("执行完本地方法，开始执行回调");
		mc.method(y);
	}
}

class MyCallbackImpl implements MyCallback {
	@Override
	public void method(int x) {
		System.out.println("回调函数，"+x);
	}
}

interface MyCallback {
	public void method(int x);
}
