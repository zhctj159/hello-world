package zhc.proxy;

public class Math implements IMath {

	@Override
	public int add(int a, int b) {
		int ret = a+b;
		System.out.println("Math.add("+a+","+b+") = " + ret);
		return (a+b);
	}
	
	public void myMethod() {
		System.out.println("Math.myMethod() is not a method contained in the interface IMath.");
	}
	
}
