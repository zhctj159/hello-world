package zhc.others;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 在应用中，有时没有把某个类注入到IOC容器中，但在运用的时候需要获取该类对应的bean，此时就需要用到@Import注解
 * @author zhc
 * @time 2019年8月14日 上午9:36:48
 */
@SpringBootApplication
//@Import({Dog.class,Cat.class})
@Import(MyConfig.class)
public class ImportTest {
	public static void main2(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ImportTest.class, args);
		System.out.println(context.getBean(Dog.class));
		System.out.println(context.getBean(Cat.class));
		context.close();
	}
}
//@Configuration
class MyConfig {
	@Bean
	public Dog getDog() {
		return new Dog();
	}
	
	@Bean
	public Cat getCat() {
		return new Cat();
	}
}

class Cat {
	
}

class Dog {
	
}
