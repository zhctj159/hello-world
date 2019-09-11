package zhc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
