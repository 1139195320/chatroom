package com.fy.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author jack
 */
public class BaseDAO {

	private static String URL;
	private static String USER;
	private static String PASSWD;

	private static Connection conn = null;
	private static Statement st = null;
	private static PreparedStatement pst = null;

	static {
		String jdbcPropertiesPath = BaseDAO.class.getResource("/jdbc.properties").getPath();
		Properties pps = new Properties();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			pps.load(new FileInputStream(jdbcPropertiesPath));
			URL = pps.getProperty("jdbc.url");
			USER = pps.getProperty("jdbc.user");
			PASSWD = pps.getProperty("jdbc.passwd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得数据库连接
	 * 
	 * @return 数据库连接
	 */
	private Connection getConn() {
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWD);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Statement getStatement() {
		try {
			st = getConn().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return st;
	}

	public PreparedStatement getPrepareStatement(String sql) {
		try {
			pst = getConn().prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pst;
	}

	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (st != null) {
				st.close();
			}
			if (pst != null) {
				pst.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (pst != null) {
				pst.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
