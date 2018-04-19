package com.fy.dao;

import java.util.List;

import com.fy.entity.Users;

public interface IUsersDao {

	/**
	 * 查询所有的用户
	 * @return
	 */
	public List<Users> findAllUsers();
	/**
	 * 查询所有在线的用户
	 * @return
	 */
	public List<Users> findUsersOnline();
	/**
	 * 增加一个新用户
	 * @param user
	 * @return false成功 true失败
	 */
	public boolean addUser(Users user);
	/**
	 * 删除一个用户（通过用户id或用户名）
	 * @param user
	 * @return
	 */
	public boolean deleteUser(Users user);
	/**
	 * 修改更新一个用户密码（通过用户id或用户名）
	 * @param user
	 * @return
	 */
	public boolean updateUserPasswd(Users user);
	/**
	 * 修改更新一个用户登陆时间（通过用户id或用户名）
	 * @param user
	 * @return
	 */
	public boolean updateUserLastlogintime(Users user);
	/**
	 * 修改更新一个用户登录IP（通过用户id或用户名）
	 * @param user
	 * @return
	 */
	public boolean updateUserLastloginip(Users user);
	/**
	 * 修改更新一个用户在线状态（通过用户id或用户名）
	 * @param user
	 * @return
	 */
	public boolean updateUserIsonline(Users user);
	/**
	 * 通过用户id查询用户
	 * @param id
	 * @return
	 */
	public Users findUserById(Integer id);
	/**
	 * 通过用户名查询用户
	 * @param name
	 * @return
	 */
	public Users findUserByName(String name);
	/**
	 * 通过ID或用户名更新用户信息（上次登陆时间，上次登录IP，是否在线）
	 * @return 更新后的该用户对象
	 */
	public Users updateUsers(Users user);
}
