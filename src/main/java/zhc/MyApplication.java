package zhc;

import javax.servlet.Servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import zhc.ssm.springmvc.DispatcherServlet;
//import zhc.ssm.springmvc.DispatcherServlet2;

/**
 * ClassName: zhc.MyApplication 
 * @Description: 我的springboot项目入口
 * @author zhc
 * @date 2019年9月27日
 */
@SpringBootApplication
public class MyApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyApplication.class, args);
//		ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication.class, args);
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		Object obj2 = applicationContext.getBean("myBean3");
		System.out.println(obj2);
		applicationContext.close();
	}
	
	@Bean
	public ServletRegistrationBean<Servlet> dispatcherRegistration() {
		return new ServletRegistrationBean<Servlet>(new DispatcherServlet(), "/*");
//		return new ServletRegistrationBean<Servlet>(new DispatcherServlet2(), "/*");
	}
}
