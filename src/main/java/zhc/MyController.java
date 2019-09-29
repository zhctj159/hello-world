package zhc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: zhc.MyController 
 * @Description: TODO
 * @author zhc
 * @date 2019年9月27日
 */
@Controller("myController2")
public class MyController {
	
	@RequestMapping("/test")
	@ResponseBody
	public String test() {
		return "hello, test";
	}
	
	public static void main(String[] args) {
	}
}
