package zhc.ssm.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MyController("zhc")
public class SpringmvcController {
	@MyAutowired("service")
	private Service service;
	
	@MyRequestMapping("select")
	public String select(HttpServletRequest req, HttpServletResponse rsp) {
		return service.select();
	}
	
	@MyRequestMapping("insert")
	public boolean insert(HttpServletRequest req, HttpServletResponse rsp, String value) {
		return service.insert(value);
	}
}
