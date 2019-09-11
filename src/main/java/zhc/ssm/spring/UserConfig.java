package zhc.ssm.spring;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

	// @Bean(initMethod="myInitMethod",destroyMethod="myDestroyMethod")
	// public User myBean() {
	// return new User();
	// }
	//
	// @Bean(initMethod="myInitMethod",destroyMethod="myDestroyMethod")
	// public User myBean2() {
	// return new User();
	// }

	@Bean(initMethod = "myInitMethod", destroyMethod = "myDestroyMethod")
	public User user() {
		return new User();
	}

	@Bean
	public BeanPostProcessor getInitBeanHandle() {
		return new AutowiredAnnotationBeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean.getClass() == User.class) {
					System.out.println("11 - BeanPostProcessor - postProcessBeforeInitialization");
				}
				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean.getClass() == User.class) {
					System.out.println("14 - BeanPostProcessor - postProcessAfterInitialization");
				}
				return bean;
			}
		};
//		return new BeanPostProcessor() {
//			@Override
//			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//				if (bean.getClass() == User.class) {
//					System.out.println("11 - BeanPostProcessor - postProcessBeforeInitialization");
//				}
//				return bean;
//			}
//
//			@Override
//			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//				if (bean.getClass() == User.class) {
//					System.out.println("14 - BeanPostProcessor - postProcessAfterInitialization");
//				}
//				return bean;
//			}
//		};
	}
	
	@Bean
	public BeanPostProcessor getInitBeanHandle2() {
		return new InstantiationAwareBeanPostProcessor() {
			@Override
			public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
				if (bean.getClass() == User.class) {
					System.out.println("-- -- InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation");
				}
				return true;
			}

			@Override
			public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
				if (beanClass==User.class) {
					System.out.println("-- -- InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation");
				}
				return null;
			}
			@Override
			public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
					String beanName) throws BeansException {
				if (bean.getClass() == User.class) {
					System.out.println("-- -- InstantiationAwareBeanPostProcessor.postProcessPropertyValues");
				}
				return pvs;
			}
		};
	}
}
