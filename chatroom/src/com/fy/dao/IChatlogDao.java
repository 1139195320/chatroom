package com.fy.dao;

import java.util.List;

import com.fy.entity.Chatlog;

public interface IChatlogDao {

	/**
	 * 增加一条聊天记录
	 * @param chatlog
	 * @return （false添加成功，true添加失败）
	 */
	public boolean addChatlog(Chatlog chatlog);
	/**
	 * 通过记录id删除一条聊天记录
	 * @param chatlog
	 * @return （false删除成功，true删除失败）
	 */
	public boolean deleteChatlog(Chatlog chatlog);
	/**
	 * 通过记录读取状态删除一条聊天记录
	 * @param chatlog
	 * @return （false删除成功，true删除失败）
	 */
	public boolean deleteChatlogByReadstate(Integer readstate);
	/**
	 * 通过发送者接收者的名字来修改消息读取状态
	 * @param name_from
	 * @param name_to
	 * @param readstate
	 * @return
	 */
	public boolean updateChatlogReadstateByfronandtoId(Integer fromid,Integer toid ,Integer readstate);
	/**
	 * 通过记录id修改该记录读取状态
	 * @param id
	 * @param readstate
	 * @return （true修改成功，false修改失败）
	 */
	public boolean updateChatlogReadstate(Integer id,Integer readstate);
	/**
	 * 通过发送者id查询记录的集合
	 * @param fromid
	 * @return
	 */
	public List<Chatlog> findChatlogByFromid(Integer fromid);
	/**
	 * 通过接收者id查询记录的集合
	 * @param toid
	 * @return
	 */
	public List<Chatlog> findChatlogByToid(Integer toid);
	/**
	 * 通过记录的读取状态查询记录的集合
	 * @param readstate
	 * @return
	 */
	public List<Chatlog> findChatlogByReadstate(Integer readstate);
	/**
	 * 删除数据库中超过n天且已被查看的聊天记录
	 * @return
	 */
	public boolean deleteOuttimeChatlog(Integer n);
}
