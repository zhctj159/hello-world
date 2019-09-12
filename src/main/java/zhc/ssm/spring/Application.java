package zhc.ssm.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class Application {
	public static void main2(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
//		ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		Object obj2 = applicationContext.getBean("myBean3");
		System.out.println(obj2);
		applicationContext.close();
		//beans.xml，applicationContext.xml，application.xml，application-beans.xml
//		BeanFactory bf  = new XmlBeanFactory(new ClassPathResource("transaction/application.xml"))	

		
//		InputStream inputStream = Resources.getResourceAsStream("SqlMapConfig.xml");
//		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//		SqlSession session = sqlSessionFactory.openSession();
//		System.out.println(session);
//		session.close();
	}
	/**
	 * @Autowired 默认按byType注入,byName需要与@Qualifier("对象名")连用
	 * @Resource 默认按byName注入,若byName找不到则byType找(name属性解析为bean的名字，而type属性则解析为bean的类型)
	 */
//	@Resource
	@Autowired
	private User user;
	
	@PostConstruct
    public void test(){
//    	System.out.println(user.getName());
        System.out.println("===============" + user);
    }
}
