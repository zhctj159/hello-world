package zhc.ssm.spring;

import org.springframework.beans.factory.FactoryBean;

/**
 * 一般情况下，Spring通过反射机制利用<bean>的class属性指定实现类实例化Bean，在某些情况下，实例化Bean过程比较复杂，
 * 如果按照传统的方式，则需要在<bean>中提供大量的配置信息。配置方式的灵活性是受限的，这时采用编码的方式可能会得到一个简单的方案。
 * Spring为此提供了一个org.springframework.bean.factory.FactoryBean的工厂类接口，用户可以通过实现该接口定制实例化Bean的逻辑。
 * FactoryBean接口对于Spring框架来说占用重要的地位，Spring自身就提供了70多个FactoryBean的实现。
 * 它们隐藏了实例化一些复杂Bean的细节，给上层应用带来了便利。从Spring3.0开始，FactoryBean开始支持泛型，即接口声明改为FactoryBean<T>的形式
 * @author zhc
 * @time 2019年8月9日 下午5:00:04
 */
public class UserFactoryBean implements FactoryBean<User> {

	private String userInfo;
	private User target;
	
	public void setTarget(User target) {
		this.target = target;
	}
	
	public User getTarget() {
		return target;
	}
	
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	
	public String getUserInfo() {
		return userInfo;
	}
	
	@Override
	public User getObject() throws Exception {
		String[] infos = userInfo.split(",");
		if (null==target) {
			target = new User();
		}
		target.setId(Integer.parseInt(infos[0]));
		target.setName(infos[1]);
		target.setAge(Integer.parseInt(infos[2]));
		return target;
	}
	
	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** 用于说明getObject返回的是否是同一个对象，由用户实现 */
	@Override
	public boolean isSingleton() {
		return false;
	}
}
