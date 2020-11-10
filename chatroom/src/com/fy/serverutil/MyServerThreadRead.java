package com.fy.serverutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.fy.dao.impl.ChatlogDao;
import com.fy.dao.impl.UsersDao;
import com.fy.entity.Chatlog;
import com.fy.entity.Users;
import com.fy.server.FriendListFrame;
import com.fy.server.start.Server;

/**
 * @author jack
 */
public class MyServerThreadRead extends Thread {
	
	private Socket s;
	private Users sUser;
	private UsersDao ud = new UsersDao();
	private ChatlogDao cd = new ChatlogDao();

	public MyServerThreadRead(Socket s, Users sUser) {
		this.s = s;
		this.sUser = sUser;
	}
	
	@Override
	public void run() {
		InputStream in = null;
		try {
			boolean isLogin = false;
			while (true) {
				// 获得输入流（接收消息）
				in = s.getInputStream();
				byte[] bFrom = new byte[1024];
				if (in != null) {
					int l = in.read(bFrom);
					if (!isLogin) {
						String str_from = new String(bFrom, 0, l);
						String tag = str_from.substring(0, 5);
						String content = str_from.substring(5);
						if ("000l-".equals(tag)) {
							isLogin = checkToFirst(false, content, "l");
						} else if ("000r-".equals(tag)) {
							isLogin = checkToFirst(false, content, "r");
						}
					} else {
						if (-1 == l) {
							continue;
						}
						String strFrom = new String(bFrom, 0, l);

						if (strFrom.startsWith("fychat") && strFrom.endsWith("tahcyf")) {
							changeChatlogByFromMsg(strFrom, "save");
							continue;
						}

						if (strFrom.startsWith("cstate") && strFrom.endsWith("etatsc")) {
							changeChatlogByFromMsg(strFrom, "delete");
							continue;
						}

						System.out.println(sUser.getName() + " ：" + strFrom);

						// 修改MyPane.strGet，提示显示界面更新数据
						MyServerPane.strGet = sUser.getName() + "：" + strFrom;

						OutputStream out;
						Socket sk;
						for (Map.Entry<Users, Socket> entry : Server.uMap.entrySet()) {
							sk = entry.getValue();
							if (!sk.isClosed() && sk.isConnected()) {
								// 获得输出流（给客户端发消息）
								out = sk.getOutputStream();
								out.write((sUser.getName() + " ：" + strFrom).getBytes());
								out.flush();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (sUser.getName() != null) {
				System.out.println(sUser.getName() + "已断开！");
				sUser.setIsonline(0);
				ud.updateUserIsonline(sUser);
				FriendListFrame.MSG = sUser.getName() + "!out";
				MyServerPane.strGet = "用户" + sUser.getName() + "已下线";
			}
			Server.uMap.remove(sUser);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				System.out.println("error2");
			}
		}
	}

	public void changeChatlogByFromMsg(String strFrom ,String changeTag) throws IOException {
		String info = strFrom.substring(6, strFrom.length() - 6);
		if("save".equals(changeTag)) {
			JSONObject jo = JSONObject.parseObject(info);
			String name_from = jo.getString("name_from");
			String name_to = jo.getString("name_to");
			String content = jo.getString("content");
			Integer fromid = ud.findUserByName(name_from).getId();
			Integer toid = ud.findUserByName(name_to).getId();
			Timestamp sendtime = new Timestamp(System.currentTimeMillis());
			Chatlog chatlog = new Chatlog(fromid, toid, content, sendtime);
			boolean result = cd.addChatlog(chatlog);
			System.out.println(result ? "增加记录失败" : "增加记录成功");
			Map<Users, Socket> map = Server.uMap;
			for(Map.Entry<Users, Socket> entry : map.entrySet()) {
				if(entry.getKey().getName().equals(name_to)) {
					// 刷新接收者的消息
					List<Chatlog> chatlogListFrom = cd.findChatlogByFromid(entry.getKey().getId());
					StringBuilder jsonStrFrom = new StringBuilder("[ ");
					for(Chatlog c : chatlogListFrom) {
						jsonStrFrom.append("{\"name\":\"")
								.append(ud.findUserById(c.getToid()).getName())
								.append("\",").append("\"content\":\"")
								.append(c.getContent()).append("\",")
								.append("\"sendtime\":\"")
								.append(c.getSendtime())
								.append("\",").append("\"tag\":\"send\",")
								.append("\"readstate\":\"")
								.append(c.getReadstate()).append("\"},");
					}
					jsonStrFrom = new StringBuilder(jsonStrFrom.substring(0, jsonStrFrom.length() - 1) + "]");
					List<Chatlog> chatlogListTo = cd.findChatlogByToid(entry.getKey().getId());
					StringBuilder jsonStrTo = new StringBuilder("[ ");
					for(Chatlog c : chatlogListTo) {
						jsonStrTo.append("{\"name\":\"")
								.append(ud.findUserById(c.getFromid()).getName())
								.append("\",").append("\"content\":\"")
								.append(c.getContent()).append("\",")
								.append("\"sendtime\":\"")
								.append(c.getSendtime()).append("\",")
								.append("\"tag\":\"get\",").append("\"readstate\":\"")
								.append(c.getReadstate()).append("\"},");
					}
					jsonStrTo = new StringBuilder(jsonStrTo.substring(0, jsonStrTo.length() - 1) + "]");
					String jsonStr_info = "---fyc{ \"jsonStr_to\":"
							+ jsonStrFrom + ",\"jsonStrFrom\":" +jsonStrTo + " }";
					Socket socket = entry.getValue();
					OutputStream os = socket.getOutputStream();
					os.write(jsonStr_info.getBytes());
				}
			}
		}else if("delete".equals(changeTag)) {
			JSONObject jo = JSONObject.parseObject(info);
			String nameFrom = jo.getString("name_from");
			String nameTo = jo.getString("name_to");
			Integer fromid = ud.findUserByName(nameFrom).getId();
			Integer toid = ud.findUserByName(nameTo).getId();
			cd.updateChatlogReadstateByfronandtoId(fromid, toid, 1);
		}
	}

	private boolean checkToFirst(boolean isLogin, String content , String t) throws IOException {
		OutputStream os;
		String[] msg = content.split("-.-");
		String name;
		String passwd;
		String result = "l".equals(t)?"登录验证中...":"注册校验中...";
		if (msg.length > 1) {
			name = msg[0];
			passwd = msg[1];
			Users user = ud.findUserByName(name);
			if ("l".equals(t)) {
				// 登录
				if (user != null && user.getPasswd().equals(passwd)) {
					if (1 != user.getIsonline()) {
						// 登录成功
						result = "l0";
						s.setKeepAlive(true);
						isLogin = true;
						user.setLastloginip(sUser.getLastloginip());
						user.setLastlogintime(new Timestamp(System.currentTimeMillis()));
						user.setIsonline(1);
						ud.updateUsers(user);
						sUser = user;
						Server.uMap.put(sUser, s);
						MyServerPane.strGet = "用户" + sUser.getName() + "已上线";
						FriendListFrame.MSG = sUser.getName() + "!in";

					} else {
						// 该用户已登录
						result = "l2";
					}
				} else {
					// 用户名或密码错误
					result = "l1";
				}
			} else if ("r".equals(t)) {
				// 注册
				if (user == null) {
					// 注册成功
					Users u = new Users();
					u.setName(name);
					u.setPasswd(passwd);
					ud.addUser(u);
					result = "r0";
					FriendListFrame.MSG = u.getName() + "!out";
				} else {
					//用户名已存在
					result = "r1";
				}
			}
		}
		os = s.getOutputStream();
		byte [] bTo = result.getBytes();
		os.write(bTo);
		os.flush();
		if("l0".equals(result)) {
			// 该用户发出的消息
			List<Chatlog> chatlogListFrom = cd.findChatlogByFromid(sUser.getId());
			StringBuilder jsonStrFrom = new StringBuilder("[ ");
			for(Chatlog chatlog : chatlogListFrom) {
				jsonStrFrom.append("{\"name\":\"")
						.append(ud.findUserById(chatlog.getToid()).getName())
						.append("\",").append("\"content\":\"")
						.append(chatlog.getContent()).append("\",")
						.append("\"sendtime\":\"").append(chatlog.getSendtime())
						.append("\",").append("\"tag\":\"send\",")
						.append("\"readstate\":\"")
						.append(chatlog.getReadstate()).append("\"},");
			}
			jsonStrFrom = new StringBuilder(jsonStrFrom.substring(0, jsonStrFrom.length() - 1) + "]");
			// 该用户接收的消息
			List<Chatlog> chatlogListTo = cd.findChatlogByToid(sUser.getId());
			StringBuilder jsonStrTo = new StringBuilder("[ ");
			for(Chatlog chatlog : chatlogListTo) {
				jsonStrTo.append("{\"name\":\"")
						.append(ud.findUserById(chatlog.getFromid()).getName())
						.append("\",").append("\"content\":\"")
						.append(chatlog.getContent()).append("\",")
						.append("\"sendtime\":\"").append(chatlog.getSendtime())
						.append("\",").append("\"tag\":\"get\",")
						.append("\"readstate\":\"")
						.append(chatlog.getReadstate()).append("\"},");
			}
			jsonStrTo = new StringBuilder(jsonStrTo.substring(0, jsonStrTo.length() - 1) + "]");
			String jsonStrInfo = "---fyc{ \"jsonStrTo\":" + jsonStrFrom
					+ ",\"jsonStrFrom\":" +jsonStrTo + " }";
			
			// TODO 这里应该修改为第一次是发所有的消息记录，而后每次只发新消息记录
			os.write(jsonStrInfo.getBytes());
			os.flush();
			
		}
		return isLogin;
	}
}