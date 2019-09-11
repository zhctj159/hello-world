package zhc.others.sc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhc
 * @time 2019年8月12日 上午9:08:22
 */
public class PropagationTest extends SpringTestCase {
	@Autowired
	private UserService userService;
	
	@Test
	public void test() {
//		userService.test11();
//		userService.test23();
//		userService.test24();
//		userService.test25();
//		userService.test33();
//		userService.test41();
//		userService.test51();
		userService.test61();
//		userService.test71();
	}
	
	
}
