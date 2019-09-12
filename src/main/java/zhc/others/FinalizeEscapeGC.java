package zhc.others;

/**
 * 1.对象可以再被GC时自我拯救
 * 2.这种自救的机会只有一次，因为一个对象的finalize()方法最多只会被系统自动调用一次
 * @author zhc
 * @time 2019年8月6日 上午9:56:41
 */
public class FinalizeEscapeGC {
	
	public static FinalizeEscapeGC SAVE_HOOK = null;
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("finalize method executed!");
		FinalizeEscapeGC.SAVE_HOOK = this;
	}
	
	public static void main(String[] args) throws Throwable {
		SAVE_HOOK = new FinalizeEscapeGC();
		
		//对象第一次成功拯救了自己
		SAVE_HOOK = null;
		System.gc();
		//因为Finalizer方法优先级很低，暂停0.5秒，以等待它
		Thread.sleep(500);
		if (SAVE_HOOK!=null) {
			System.out.println("yes, i am still alive");
		} else {
			System.out.println("no, i am dead");
		}
		
		//对象第二次拯救失败
		SAVE_HOOK = null;
		System.gc();
		Thread.sleep(500);
		if (SAVE_HOOK!=null) {
			System.out.println("yes, i am still alive");
		} else {
			System.out.println("no, i am dead");
		}
	}
}
