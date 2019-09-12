package zhc.pattern.singleton;

public class Singleton2 {
	private static Singleton2 instance;
	
	private Singleton2(){}
	
	public static synchronized Singleton2 getInstance(){
		if (null==instance) {
			instance = new Singleton2();
		}
		return instance;
	}
}
