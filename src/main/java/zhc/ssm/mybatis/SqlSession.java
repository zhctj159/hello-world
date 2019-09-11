package zhc.ssm.mybatis;

/**
 * 使用JDK动态代理
 * @author zhc
 *
 */
public class SqlSession {
	private Configuration configuration = Configuration.getInstance();
	
	public <T> T getMapper(Class<T> clazz) {
		return configuration.getMapper(clazz);
	}
}
