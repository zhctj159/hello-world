package zhc.pattern.singleton;

public class Singleton3 {
	private static volatile Singleton3 instance;
	
	private Singleton3(){}
	
	public static Singleton3 getInstance() {
		if (null==instance) {
			synchronized (Singleton3.class) {
				if (null==instance) {
					instance = new Singleton3();
				}
			}
		}
		return instance;
	}
}
