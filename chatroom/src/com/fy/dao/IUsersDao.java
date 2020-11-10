package com.fy.dao;

import java.util.List;

import com.fy.entity.Users;

/**
 * @author jack
 */
public interface IUsersDao {

	/**
	 * 查询所有的用户
	 * @return 用户
	 */
	List<Users> findAllUsers();
	/**
	 * 查询所有在线的用户
	 * @return 用户
	 */
	List<Users> findUsersOnline();
	/**
	 * 增加一个新用户
	 * @param user 新增的用户信息
	 * @return false成功 true失败
	 */
	boolean addUser(Users user);
	/**
	 * 删除一个用户（通过用户id或用户名）
	 * @param user 删除的用户信息
	 * @return false成功 true失败
	 */
	boolean deleteUser(Users user);
	/**
	 * 修改更新一个用户密码（通过用户id或用户名）
	 * @param user 修改的用户信息
	 * @return false成功 true失败
	 */
	boolean updateUserPasswd(Users user);
	/**
	 * 修改更新一个用户登陆时间（通过用户id或用户名）
	 * @param user 修改的用户信息
	 * @return false成功 true失败
	 */
	boolean updateUserLastlogintime(Users user);
	/**
	 * 修改更新一个用户登录IP（通过用户id或用户名）
	 * @param user 修改的用户信息
	 * @return false成功 true失败
	 */
	boolean updateUserLastloginip(Users user);
	/**
	 * 修改更新一个用户在线状态（通过用户id或用户名）
	 * @param user 修改的用户信息
	 * @return false成功 true失败
	 */
	boolean updateUserIsonline(Users user);
	/**
	 * 通过用户 id 查询用户
	 * @param id 用户 id 
	 * @return 用户
	 */
	Users findUserById(Integer id);
	/**
	 * 通过用户名查询用户
	 * @param name 用户名
	 * @return 用户
	 */
	Users findUserByName(String name);
	/**
	 * 通过ID或用户名更新用户信息（上次登陆时间，上次登录IP，是否在线）
	 * @param user 修改的用户信息
	 * @return 更新后的该用户对象
	 */
	Users updateUsers(Users user);
}
