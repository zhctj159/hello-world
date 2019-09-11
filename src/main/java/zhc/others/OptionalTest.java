package zhc.others;

import java.util.Optional;

public class OptionalTest {
	public static void main(String[] args) {
		User user = new User();
		user.setName("OK");
		int code = getNameHashCode2(user);
		System.out.println(code);
	}
	
	/**
	 * Optional操作2
	 */
	public static int getNameHashCode2(User u) {
		return Optional.ofNullable(u)
					.map(c->c.getName())
					.map(d->d.hashCode())
					.orElseThrow(()->new RuntimeException("Null"));
	}
	
	/**
	 * 普通操作2
	 */
	public static int getNameHashCode(User u) {
	    if (null != u) {
	    	String name = u.getName();
	    	if (null != name) {
				return name.hashCode();
			}
	    }
	    throw new NullPointerException("Null");
	}
	
	/**
	 * Optional的正确用法
	 */
	public static String getName2(User u) {
	    return Optional.ofNullable(u)
	                    .map(user->user.name)
	                    .orElse("Unknown");
	}
	
	/**
	 * Optional的错误用法
	 */
	public static String getName1(User u) {
	    Optional<User> user = Optional.ofNullable(u);
	    if (!user.isPresent())
	        return "Unknown";
	    return user.get().name;
	}
	
	/**
	 * 普通操作
	 */
	public static String getName(User u) {
	    if (null == u) {
	    	return "Unknown";
	    }
	    return u.getName();
	}
	
	
	
	static class User {
		private int id;
		private String name;
		private int age;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
	}
}
