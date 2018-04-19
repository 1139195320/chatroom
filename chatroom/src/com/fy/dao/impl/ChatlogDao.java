package com.fy.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fy.dao.BaseDAO;
import com.fy.dao.IChatlogDao;
import com.fy.entity.Chatlog;

public class ChatlogDao extends BaseDAO implements IChatlogDao{

	@Override
	public boolean addChatlog(Chatlog chatlog) {
		boolean result = false;
		String sql = "INSERT INTO chatlog (fromid,toid,content,sendtime,readstate) VALUES (?,?,?,?,0)";
		PreparedStatement pst = getPrepareStatement(sql);
		try {
			pst.setObject(1, chatlog.getFromid());
			pst.setObject(2, chatlog.getToid());
			pst.setObject(3, chatlog.getContent());
			pst.setObject(4, chatlog.getSendtime());
			result = pst.execute();
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
	public boolean deleteChatlog(Chatlog chatlog) {
		boolean result = false;
		String sql = "DELETE FROM chatlog WHERE id = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		try {
			pst.setObject(1, chatlog.getId());
			result =pst.execute();
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
	public boolean deleteChatlogByReadstate(Integer readstate) {
		boolean result = false;
		String sql = "DELETE FROM chatlog WHERE readstate = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		try {
			pst.setObject(1, readstate);
			result =pst.execute();
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
	public boolean deleteOuttimeChatlog(Integer n) {
		boolean result = false;
		Timestamp minTime = new Timestamp(new Date().getTime() - 1000*60*60*24*n);
		String sql = "DELETE FROM chatlog WHERE sendtime < ? AND readstate = 1";
		PreparedStatement pst = getPrepareStatement(sql);
		try {
			pst.setObject(1, minTime);
			result =pst.execute();
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
	public boolean updateChatlogReadstate(Integer id, Integer readstate) {
		boolean result = false;
		int res = 0;
		String sql = "UPDATE chatlog SET readstate = ? WHERE id = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		try {
			pst.setObject(1, readstate);
			pst.setObject(2, id);
			res = pst.executeUpdate();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		if(res > 0)result = true;
		return result;
	}

	@Override
	public boolean updateChatlogReadstateByfronandtoId(Integer fromid,Integer toid ,Integer readstate) {
		boolean result = false;
		int res = 0;
		String sql = "UPDATE chatlog SET readstate = ? WHERE fromid = ? AND toid = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		try {
			pst.setObject(1, readstate);
			pst.setObject(2, fromid);
			pst.setObject(3, toid);
			res = pst.executeUpdate();
			commit();
		} catch (SQLException e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		if(res > 0)result = true;
		return result;
	}

	@Override
	public List<Chatlog> findChatlogByFromid(Integer fromid) {
		List<Chatlog> clist = new ArrayList<Chatlog>();
		String sql = "SELECT id,fromid,toid,content,sendtime,readstate FROM chatlog WHERE fromid = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		ResultSet rs = null;
		try {
			pst.setObject(1, fromid);
			rs = pst.executeQuery();
			Chatlog chatlog = null;
			while(rs.next()) {
				chatlog = new Chatlog();
				chatlog.setId(rs.getInt(1));
				chatlog.setFromid(rs.getInt(2));
				chatlog.setToid(rs.getInt(3));
				chatlog.setContent(rs.getString(4));
				chatlog.setSendtime(rs.getTimestamp(5));
				chatlog.setReadstate(rs.getInt(6));
				clist.add(chatlog);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return clist;
	}

	@Override
	public List<Chatlog> findChatlogByToid(Integer toid) {
		List<Chatlog> clist = new ArrayList<Chatlog>();
		String sql = "SELECT id,fromid,toid,content,sendtime,readstate FROM chatlog WHERE toid = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		ResultSet rs = null;
		try {
			pst.setObject(1, toid);
			rs = pst.executeQuery();
			Chatlog chatlog = null;
			while(rs.next()) {
				chatlog = new Chatlog();
				chatlog.setId(rs.getInt(1));
				chatlog.setFromid(rs.getInt(2));
				chatlog.setToid(rs.getInt(3));
				chatlog.setContent(rs.getString(4));
				chatlog.setSendtime(rs.getTimestamp(5));
				chatlog.setReadstate(rs.getInt(6));
				clist.add(chatlog);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return clist;
	}

	@Override
	public List<Chatlog> findChatlogByReadstate(Integer readstate) {
		List<Chatlog> clist = new ArrayList<Chatlog>();
		String sql = "SELECT id,fromid,toid,content,sendtime,readstate FROM chatlog WHERE readstate = ?";
		PreparedStatement pst = getPrepareStatement(sql);
		ResultSet rs = null;
		try {
			pst.setObject(1, readstate);
			rs = pst.executeQuery();
			Chatlog chatlog = null;
			while(rs.next()) {
				chatlog = new Chatlog();
				chatlog.setId(rs.getInt(1));
				chatlog.setFromid(rs.getInt(2));
				chatlog.setToid(rs.getInt(3));
				chatlog.setContent(rs.getString(4));
				chatlog.setSendtime(rs.getTimestamp(5));
				chatlog.setReadstate(rs.getInt(6));
				clist.add(chatlog);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(rs);
		}
		return clist;
	}

}
