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

public class MyServerThreadRead extends Thread{
	
	private Socket s;
	private Users sUser = null;
	private UsersDao ud = new UsersDao();
	private ChatlogDao cd = new ChatlogDao();
	
	public MyServerThreadRead(Socket s,Users sUser){
		this.s=s;
		this.sUser=sUser;
	}
	
	@Override
	public void run() {
		InputStream in=null;
		try {
			boolean logined=false;
			while(true){
				//获得输入流（接收消息）
				in=s.getInputStream();
				byte[]b_from=new byte[1024];
				if(in!=null){
					if(!logined) {
						int l =in.read(b_from);
						String str_from=new String(b_from,0,l);
						String tag=str_from.substring(0, 5);
						String content=str_from.substring(5);
						if(tag.equals("000l-")) {
							logined = checkToFirst(logined, content,"l");
						}else if(tag.equals("000r-")) {
							logined = checkToFirst(logined, content,"r");
						}
					}else {
						int len=in.read(b_from);
						if(-1 == len)continue;
						String strFrom=new String(b_from,0,len);
						
						if(strFrom.startsWith("fychat") && strFrom.endsWith("tahcyf")) {
							changeChatlogByFromMsg(strFrom ,"save");
							continue;
						}
						
						if(strFrom.startsWith("cstate") && strFrom.endsWith("etatsc")) {
							changeChatlogByFromMsg(strFrom ,"delete");
							continue;
						}
						
						System.out.println(sUser.getName()+ " ：" +strFrom);
						
						//修改MyPane.strGet，提示显示界面更新数据
						MyServerPane.strGet=sUser.getName()+ "：" +strFrom;

						OutputStream out=null;
						Socket sk=null;
						for (Map.Entry<Users, Socket> entry : Server.uMap.entrySet()) {
							
							sk=entry.getValue();
							if(!sk.isClosed() && sk.isConnected()){
								
								//获得输出流（给客户端发消息）
								out=sk.getOutputStream();
								out.write((sUser.getName()+ " ：" +strFrom).getBytes());
								out.flush();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if(sUser.getName()!=null) {
				System.out.println(sUser.getName() + "已断开！");
				sUser.setIsonline(0);
				ud.updateUserIsonline(sUser);
				FriendListFrame.MSG = sUser.getName() + "!out";
				MyServerPane.strGet = "用户" + sUser.getName()+ "已下线";
			}
			Server.uMap.remove(sUser);
		}finally{
			try {
				if(in!=null)in.close();
				if(s!=null)s.close();
			} catch (IOException e) {
				System.out.println("error2");
			}
		}
	}

	public void changeChatlogByFromMsg(String strFrom ,String changeTag) throws IOException {
		if("save".equals(changeTag)) {
			String info = strFrom.substring(6, strFrom.length()-6);
			JSONObject jo = JSONObject.parseObject(info);
			String name_from = jo.getString("name_from");
			String name_to = jo.getString("name_to");
			String content = jo.getString("content");
			Integer fromid = ud.findUserByName(name_from).getId();
			Integer toid = ud.findUserByName(name_to).getId();
			Timestamp sendtime = new Timestamp(new Date().getTime());
			Chatlog chatlog = new Chatlog(fromid, toid, content, sendtime);
			boolean result = cd.addChatlog(chatlog);
			System.out.println(result ? "增加记录失败" : "增加记录成功");
			Map<Users, Socket> map = Server.uMap;
			for(Map.Entry<Users, Socket> entry : map.entrySet()) {
				if(entry.getKey().getName().equals(name_to)) {
					//刷新接收者的消息
					List<Chatlog> clist_from = cd.findChatlogByFromid(entry.getKey().getId());
					String jsonStr_from = "[ ";
					for(Chatlog c : clist_from) {
						jsonStr_from += "{\"name\":\"" + ud.findUserById(c.getToid()).getName() + "\"," 
								+ "\"content\":\"" + c.getContent() + "\","
								+ "\"sendtime\":\"" + c.getSendtime() + "\","
								+ "\"tag\":\"send\","
								+ "\"readstate\":\"" + c.getReadstate() + "\"},";
					}
					jsonStr_from = jsonStr_from.substring(0,jsonStr_from.length()-1) + "]";
					List<Chatlog> clist_to = cd.findChatlogByToid(entry.getKey().getId());
					String jsonStr_to = "[ ";
					for(Chatlog c : clist_to) {
						jsonStr_to += "{\"name\":\"" + ud.findUserById(c.getFromid()).getName() + "\","
								+ "\"content\":\"" + c.getContent() + "\","
								+ "\"sendtime\":\"" + c.getSendtime() + "\","
								+ "\"tag\":\"get\","
								+ "\"readstate\":\"" + c.getReadstate() + "\"},";
					}
					jsonStr_to = jsonStr_to.substring(0,jsonStr_to.length()-1) + "]";
					String jsonStr_info = "---fyc{ \"jsonStr_to\":" + jsonStr_from + ",\"jsonStr_from\":" +jsonStr_to + " }";
					Socket socket = entry.getValue();
					OutputStream os = socket.getOutputStream();
					os.write(jsonStr_info.getBytes());
				}
			}
		}else if("delete".equals(changeTag)) {
			String info = strFrom.substring(6, strFrom.length()-6);
			JSONObject jo = JSONObject.parseObject(info);
			String name_from = jo.getString("name_from");
			String name_to = jo.getString("name_to");
			Integer fromid = ud.findUserByName(name_from).getId();
			Integer toid = ud.findUserByName(name_to).getId();
			cd.updateChatlogReadstateByfronandtoId(fromid, toid, 1);
		}
	}

	private boolean checkToFirst(boolean logined, String content , String t) throws SocketException, IOException {
		OutputStream os=null;
		String msg[]=content.split("-.-");
		String name="";
		String passwd="";
		String result=t.equals("l")?"登录验证中...":"注册校验中...";
		if(msg!=null && msg.length>1) {
			name=msg[0];
			passwd=msg[1];
			Users user = ud.findUserByName(name);
			if(t.equals("l")) {
				//登录
				if(user != null && user.getPasswd().equals(passwd)) {
					if(1 != user.getIsonline()) {
						//登录成功
						result="l0";
						s.setKeepAlive(true);
						logined=true;
						user.setLastloginip(sUser.getLastloginip());
						user.setLastlogintime(new Timestamp(new Date().getTime()));
						user.setIsonline(1);
						ud.updateUsers(user);
						sUser=user;
						Server.uMap.put(sUser, s);
						MyServerPane.strGet = "用户" + sUser.getName()+ "已上线";
						FriendListFrame.MSG = sUser.getName() + "!in";
						
					}else {
						//该用户已登录
						result = "l2";
					}
				}else {
					//用户名或密码错误
					result="l1";
				}
			}else if(t.equals("r")) {
				//注册
				if(user == null) {
					//注册成功
					Users u = new Users();
					u.setName(name);
					u.setPasswd(passwd);
					ud.addUser(u);
					result="r0";
					FriendListFrame.MSG = u.getName() + "!out";
				}else {
					//用户名已存在
					result="r1";
				}
			}
		}
		os=s.getOutputStream();
		byte []b_to=new byte[1024];
		b_to=result.getBytes();
		os.write(b_to);
		os.flush();
		if("l0".equals(result)) {
			//该用户发出的消息
			List<Chatlog> clist_from = cd.findChatlogByFromid(sUser.getId());
			String jsonStr_from = "[ ";
			for(Chatlog chatlog : clist_from) {
				jsonStr_from += "{\"name\":\"" + ud.findUserById(chatlog.getToid()).getName() + "\"," 
						+ "\"content\":\"" + chatlog.getContent() + "\","
						+ "\"sendtime\":\"" + chatlog.getSendtime() + "\","
						+ "\"tag\":\"send\","
						+ "\"readstate\":\"" + chatlog.getReadstate() + "\"},";
			}
			jsonStr_from = jsonStr_from.substring(0,jsonStr_from.length()-1) + "]";
			//该用户接收的消息
			List<Chatlog> clist_to = cd.findChatlogByToid(sUser.getId());
			String jsonStr_to = "[ ";
			for(Chatlog chatlog : clist_to) {
				jsonStr_to += "{\"name\":\"" + ud.findUserById(chatlog.getFromid()).getName() + "\","
						+ "\"content\":\"" + chatlog.getContent() + "\","
						+ "\"sendtime\":\"" + chatlog.getSendtime() + "\","
						+ "\"tag\":\"get\","
						+ "\"readstate\":\"" + chatlog.getReadstate() + "\"},";
			}
			jsonStr_to = jsonStr_to.substring(0,jsonStr_to.length()-1) + "]";
			String jsonStr_info = "---fyc{ \"jsonStr_to\":" + jsonStr_from + ",\"jsonStr_from\":" +jsonStr_to + " }";
			
			//TODO 这里应该修改为第一次是发所有的消息记录，而后每次只发新消息记录
			os.write(jsonStr_info.getBytes());
			os.flush();
			
		}
		return logined;
	}
}