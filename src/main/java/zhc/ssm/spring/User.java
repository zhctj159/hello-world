package zhc.ssm.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.ServletContextAware;

public class User implements 
		BeanNameAware,
		BeanClassLoaderAware,
		BeanFactoryAware,
		EnvironmentAware,
		EmbeddedValueResolverAware,
		ResourceLoaderAware,
		ApplicationEventPublisherAware,
		MessageSourceAware,
		ApplicationContextAware,
		ServletContextAware,
		InitializingBean,
		DisposableBean {
	
	@Value("31")
	private int id;
	@Value("zhaihc333")
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		System.out.println("setId: " + id);
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		System.out.println("setName: " + name);
		this.name = name;
	}
	@Override
	public String toString() {
		return "[id="+id+",name="+name+"]";
	}
	
	public User() {
		id = 0;
		name = "zhaihc3";
		System.err.println("User Constructor");
	}
	
	@PostConstruct
	public void init2() {
		id = 1;
		name = "zhaihc4";
		System.err.println("PostConstruct init2");
	}
	
	public void myInitMethod() {
		id = 2;
		name = "zhaihc5";
		System.err.println("13 - init-method - myInitMethod");
	}
	
	@PreDestroy
	public void destroy2() {
		id = 1;
		name = "zhaihc4";
		System.err.println("000 - PreDestroy destroy2");
	}
	
	public void myDestroyMethod() {
		id = 2;
		name = "zhaihc5";
		System.err.println("000 - destroy-method - myDestroyMethod");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		id = 3;
		name = "zhaihc6";
		System.err.println("12 - InitializingBean - afterPropertiesSet");
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		System.out.println("10 - ServletContextAware - setServletContext: " + servletContext);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("9 - ApplicationContextAware - setApplicationContext: " + applicationContext);
	}
	@Override
	public void setMessageSource(MessageSource messageSource) {
		System.out.println("8 - MessageSourceAware - setMessageSource: " + messageSource);
	}
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		System.out.println("7 - ApplicationEventPublisherAware - setApplicationEventPublisher: " + applicationEventPublisher);
	}
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		System.out.println("6 - ResourceLoaderAware - setResourceLoader: " + resourceLoader);
	}
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		System.out.println("5 - EmbeddedValueResolverAware - setEmbeddedValueResolver: " + resolver);
	}
	@Override
	public void setEnvironment(Environment environment) {
		System.out.println("4 - EnvironmentAware - setEnvironment: " + environment);
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("3 - BeanFactoryAware - setBeanFactory: " + beanFactory.getClass().getName());
	}
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		System.out.println("2 - BeanClassLoaderAware - setBeanClassLoader: " + classLoader);
	}
	@Override
	public void setBeanName(String name) {
		System.out.println("1 - BeanNameAware - setBeanName: " + name);
	}
	@Override
	public void destroy() throws Exception {
		System.err.println("000 - DisposableBean.destroy");
	}
}
