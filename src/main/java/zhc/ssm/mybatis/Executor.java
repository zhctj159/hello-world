package zhc.ssm.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Executor {
	private Configuration configuration = Configuration.getInstance();
	
	@SuppressWarnings("unchecked")
	public <T> T query(String sql, Object parameter) {
		Connection conn = configuration.getConnection();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, parameter.toString());
			rs = ps.executeQuery();
			User u = new User();
			while (rs.next()) {
				u.setId(rs.getString(1));
				u.setUsername(rs.getString(2));
				u.setPassword(rs.getString(3));
			}
			return (T) u;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null!=rs) {
					rs.close();
				}
				if (null!=ps) {
					ps.close();
				}
				if (null!=conn) {
					conn.close();
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		
		return null;
	}

}

