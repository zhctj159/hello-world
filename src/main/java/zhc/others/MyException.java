package zhc.others;

/**
 * Throwable为java异常类型顶层接口，派生出Error和Exception
 * 一个对象只有是Throwable类的实例才是一个异常对象，才能被异常处理机制识别
 * 
 * 注意以下几种异常的继承关系：
 * OutOfMemoryError -> VirtualMachineError -> Error -> Throwable
 * FileNotFoundException -> IOException -> Exception -> Throwable
 * NullPointerException -> RuntimeException -> Exception -> Throwable
 * @author zhc
 * @time 2019年7月2日 上午10:44:31
 */
public class MyException extends Throwable {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		System.out.println("start");
		try {
			throw new MyException();
		} catch (MyException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printStackTrace() {
		System.out.println("Hello,MyException");
		super.printStackTrace();
	}
}
