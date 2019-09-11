package zhc.ssm.mybatis;

public class MybatisTest {
	public static void main(String[] args) {
		System.out.println();
		//创建SqlSession
		SqlSession sqlSession = new SqlSession();
		//创建代理对象
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		//由代理对象完成sql操作
		User user = userMapper.getUserById("1");
		System.out.println(user);
	}
}
