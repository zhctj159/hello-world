package zhc.thread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnPoolTest {

	public static void main(String[] args) throws TimeoutException, SQLException, ClassNotFoundException {
		ConnPoolTest test = new ConnPoolTest();
		Connection conn = test.getConn();
		PreparedStatement ps = conn.prepareStatement("select host,password from mysql.user where user='root'");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString("host"));
			System.out.println(rs.getString("password"));
		}
	}

	/** 连接池最大容量 */
	private final int maxSize = 10;
	/** 存活的连接数 */
	private AtomicInteger activeSize = new AtomicInteger(0);
	/** 存放已用连接的阻塞队列-busy */
	private LinkedBlockingQueue<Connection> busy;
	/** 存放空闲连接的阻塞队列-idle */
	private LinkedBlockingQueue<Connection> idle;

	public ConnPoolTest() {
		init();
	}
	
	public void init() {
		busy = new LinkedBlockingQueue<Connection>();
		idle = new LinkedBlockingQueue<Connection>();
	}

	public void destroy() {
		busy = null;
		idle = null;
	}

	public Connection getConn() throws TimeoutException, ClassNotFoundException, SQLException {
		// idle有值，直接取
		Connection conn = idle.poll();
		if (null != conn) {
			busy.offer(conn);
			return conn;
		}
		// idle无值且没有达到连接数上限，新建
//		if (busy.size()<maxSize) {
//			conn = createConn();
//			busy.offer(conn);
//			return conn;
//		}
		if (activeSize.get() < maxSize) {
			if (activeSize.incrementAndGet() <= maxSize) {
				conn = createConn();
				busy.offer(conn);
				return conn;
			} else {
				activeSize.decrementAndGet();
			}
		}
		// 池满，等待返回
		try {
			System.out.println("等待连接");
			conn = idle.poll(10000, TimeUnit.MICROSECONDS); // remove调用的poll方法，如果poll返回null，则remove抛出异常
			if (null == conn) {
				throw new TimeoutException("等待超时，获取失败");
			}
			System.out.println("等到了连接");
			busy.offer(conn); // add调用的offer方法，如果offer返回false，则add抛出异常
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return conn;
	}

	private Connection createConn() throws ClassNotFoundException, SQLException {
		// jdbc方式创建新连接
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mysql?serverTimezone=UTC", "root", "123456");
//		return null;
	}

	public void release(Connection conn) {
		busy.remove(conn);
		idle.offer(conn);
	}
}
