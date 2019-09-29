package zhc.pattern.singleton;

public class Singleton4 {
	
	private Singleton4(){
		if (null!=SingletonHolder.INSTANCE) {
			throw new RuntimeException("Can not create second instance");
		}
	}
	
	public static final Singleton4 getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private static final Singleton4 INSTANCE = new Singleton4();
	}
}
