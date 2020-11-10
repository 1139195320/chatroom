package com.fy.dao;

import java.util.List;

import com.fy.entity.Chatlog;

/**
 * @author jack
 */
public interface IChatlogDao {

	/**
	 * 增加一条聊天记录
	 * @param chatlog 聊天记录
	 * @return （false添加成功，true添加失败）
	 */
	boolean addChatlog(Chatlog chatlog);
	/**
	 * 通过记录 id 删除一条聊天记录
	 * @param chatlog 聊天记录
	 * @return （false删除成功，true删除失败）
	 */
	boolean deleteChatlog(Chatlog chatlog);
	/**
	 * 通过记录读取状态删除一条聊天记录
	 * @param readstate 记录读取状态
	 * @return （false删除成功，true删除失败）
	 */
	boolean deleteChatlogByReadstate(Integer readstate);
	/**
	 * 通过发送者接收者的 id 来修改消息读取状态
	 * @param fromid 发送者 id
	 * @param toid 接收者 id
	 * @param readstate 消息读取状态
	 * @return （true修改成功，false修改失败）
	 */
	boolean updateChatlogReadstateByfronandtoId(Integer fromid,Integer toid ,Integer readstate);
	/**
	 * 通过记录 id 修改该记录读取状态
	 * @param id 记录 id 
	 * @param readstate 消息读取状态
	 * @return （true修改成功，false修改失败）
	 */
	boolean updateChatlogReadstate(Integer id,Integer readstate);
	/**
	 * 通过发送者 id 查询记录的集合
	 * @param fromid 发送者 id 
	 * @return 记录的集合
	 */
	List<Chatlog> findChatlogByFromid(Integer fromid);
	/**
	 * 通过接收者 id 查询记录的集合
	 * @param toid 接收者 id
	 * @return 记录的集合
	 */
	List<Chatlog> findChatlogByToid(Integer toid);
	/**
	 * 通过记录的读取状态查询记录的集合
	 * @param readstate 记录的读取状态
	 * @return 记录的集合
	 */
	List<Chatlog> findChatlogByReadstate(Integer readstate);
	/**
	 * 删除数据库中超过 n 天且已被查看的聊天记录
	 * @param n 天数
	 * @return （false删除成功，true删除失败）
	 */
	boolean deleteOuttimeChatlog(Integer n);
}
