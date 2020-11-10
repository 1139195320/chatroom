package com.fy.server.start;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.fy.dao.impl.ChatlogDao;
import com.fy.entity.Users;
import com.fy.serverutil.MyServerPane;
import com.fy.serverutil.MyServerThreadRead;

public class Server {

	//服务器端口号
	private static final int SERVER_PORT=8081;
	
	//装载用户的集合
	public static Map<Users, Socket> uMap=new HashMap<Users, Socket>();//users:连接

	public static String friendLine = "";
	public static String friendLine_old = "";
	
	private ChatlogDao cd = new ChatlogDao();
	
	public static void main(String[] args) {
		MyServerPane serverPane=new MyServerPane();
		serverPane.initPane();
		new Server();
	}
	
	public Server(){
		ServerSocket sso=null;
		Socket s=null;
		try {
			sso=new ServerSocket(SERVER_PORT);
			MyServerPane.strGet="服务器启动，正在监听客户端请求...";
			System.out.println("服务器启动，正在监听客户端请求...");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					//删除数据库中超过五天且已被查看的聊天记录
					cd.deleteOuttimeChatlog(5);
				}
			}).start();
			
			while(true){
			
				//监听客户端请求
				s=sso.accept();
				
				Users user=new Users();
				user.setLastloginip(s.getInetAddress().toString().substring(1));
				
				//收消息
				Thread tRead=new MyServerThreadRead(s, user);
				tRead.start();
				
			}
				
		} catch (IOException e) {
			System.out.println("服务器运行异常！");
		}finally{
			try {
				if(sso!=null)sso.close();
			} catch (IOException e) {
				System.out.println("error6");
			}
		}
	}
	
}



