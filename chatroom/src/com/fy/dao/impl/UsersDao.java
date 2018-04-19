package com.fy.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fy.dao.BaseDAO;
import com.fy.dao.IUsersDao;
import com.fy.entity.Users;

public class UsersDao extends BaseDAO implements IUsersDao{

	@Override
	public List<Users> findAllUsers() {
		List<Users> userList=new ArrayList<Users>();
		String sql="SELECT * FROM users";
		ResultSet rs=null;
		try {
			rs=getStatement().executeQuery(sql);
			Users user=null;
			while(rs.next()) {
				user=new Users();
				user.setId(rs.getInt(1));
				user.setName(rs.getString(2));
				user.setPasswd(rs.getString(3));
				user.setLastlogintime(rs.getTimestamp(4));
				user.setLastloginip(rs.getString(5));
				user.setIsonline(rs.getInt(6));
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return userList;
	}

	@Override
	public List<Users> findUsersOnline() {
		List<Users> userList=new ArrayList<Users>();
		String sql="SELECT * FROM users WHERE isonline = 1";
		ResultSet rs=null;
		try {
			rs=getStatement().executeQuery(sql);
			Users user=null;
			while(rs.next()) {
				user=new Users();
				user.setId(rs.getInt(1));
				user.setName(rs.getString(2));
				user.setPasswd(rs.getString(3));
				user.setLastlogintime(rs.getTimestamp(4));
				user.setLastloginip(rs.getString(5));
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return userList;
	}

	@Override
	public boolean addUser(Users user) {
		boolean result=false;
		String sql="INSERT INTO users (id ,name ,passwd, lastlogintime ,lastloginip,isonline) VALUES ( ?,?,?,?,?,0)";
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, user.getId());
			pst.setObject(2, user.getName());
			pst.setObject(3, user.getPasswd());
			pst.setObject(4, user.getLastlogintime());
			pst.setObject(5, user.getLastloginip());
			result=pst.execute();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	@Override
	public boolean deleteUser(Users user) {
		boolean result=false;
		String sql = "DELETE FROM users WHERE ";
		String str = "";
		if(user.getId() != null) {
			sql+=" id = ?";
			str = user.getId() + "";
		}else if(user.getName()!=null) {
			sql += " name = ?";
			str = user.getName();
		}
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, str);
			result=pst.execute();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	@Override
	public Users findUserById(Integer id) {
		Users user=null;
		String sql="SELECT * FROM users WHERE id = ?";
		ResultSet rs=null;
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, id);
			rs=pst.executeQuery();
			while(rs.next()) {
				user=new Users();
				user.setId(rs.getInt(1));
				user.setName(rs.getString(2));
				user.setPasswd(rs.getString(3));
				user.setLastlogintime(rs.getTimestamp(4));
				user.setLastloginip(rs.getString(5));
				user.setIsonline(rs.getInt(6));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return user;
	}

	@Override
	public Users findUserByName(String name) {
		Users user=null;
		String sql="SELECT * FROM users WHERE name = ?";
		ResultSet rs=null;
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, name);
			rs=pst.executeQuery();
			while(rs.next()) {
				user=new Users();
				user.setId(rs.getInt(1));
				user.setName(rs.getString(2));
				user.setPasswd(rs.getString(3));
				user.setLastlogintime(rs.getTimestamp(4));
				user.setLastloginip(rs.getString(5));
				user.setIsonline(rs.getInt(6));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return user;
	}

	@Override
	public boolean updateUserPasswd(Users user) {
		boolean result = false;
		String sql = "UPDATE users SET passwd = ? WHERE ";
		String str = "";
		if(user.getId()!=null) {
			sql += " id = ?";
			str = user.getId() + "";
		}else if(user.getName() != null) {
			sql += " name = ?";
			str = user.getName() ;
		}else {
			return false;
		}
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, user.getPasswd());
			pst.setObject(2, str);
			result=pst.execute();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	@Override
	public boolean updateUserLastlogintime(Users user) {
		boolean result = false;
		String sql = "UPDATE users SET lastlogintime = ? WHERE ";
		String str = "";
		if(user.getId()!=null) {
			sql += " id = ?";
			str = user.getId() +"";
		}else if(user.getName() != null) {
			sql += " name = ?";
			str = user.getName();
		}else {
			return false;
		}
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, user.getLastlogintime());
			pst.setObject(2, str);
			result=pst.execute();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	@Override
	public boolean updateUserLastloginip(Users user) {
		boolean result = false;
		String sql = "UPDATE users SET lastloginip = ? WHERE ";
		String str = "";
		if(user.getId()!=null) {
			sql += " id = ?";
			str = user.getId() + "";
		}else if(user.getName() != null) {
			sql += " name = ?";
			str = user.getName();
		}else {
			return false;
		}
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, user.getLastloginip());
			pst.setObject(2, str);
			result=pst.execute();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	@Override
	public boolean updateUserIsonline(Users user) {
		boolean result = false;
		String sql = "UPDATE users SET isonline = ? WHERE ";
		String str = "";
		if(user.getId()!=null) {
			sql += " id = ?";
			str = user.getId() + "";
		}else if(user.getName() != null) {
			sql += " name = ?";
			str = user.getName();
		}else {
			return false;
		}
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, user.getIsonline());
			pst.setObject(2, str);
			result=pst.execute();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	@Override
	public Users updateUsers(Users user) {
		Users result = null;
		String sql = "UPDATE users SET lastlogintime = ? , lastloginip = ? , isonline = ? WHERE ";
		String str = "";
		if(user.getId()!=null) {
			sql += " id = ?";
			str = user.getId() + "";
		}else if(user.getName() != null) {
			sql += " name = ?";
			str = user.getName();
		}else {
			return user;
		}
		PreparedStatement pst=getPrepareStatement(sql);
		try {
			pst.setObject(1, user.getLastlogintime());
			pst.setObject(2, user.getLastloginip());
			pst.setObject(3, user.getIsonline());
			pst.setObject(4, str);
			pst.execute();
			result = user;
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		return result;
	}

	
}
