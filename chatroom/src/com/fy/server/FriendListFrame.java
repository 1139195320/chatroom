package com.fy.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.alibaba.fastjson.JSONObject;
import com.fy.dao.impl.UsersDao;
import com.fy.entity.Users;
import com.fy.server.start.Server;

public class FriendListFrame extends JFrame{
	
	private static UsersDao ud = new UsersDao();
	
	public static JList<String> jlist ;
	private static JScrollPane jsp;
	public static DefaultComboBoxModel<String> boxModel;
	
	public static String MSG = "";
	
	public FriendListFrame() {
		
		jlist = new JList<String>();
		jlist.setFixedCellHeight(30);
		jlist.setSelectionForeground(Color.RED);
		jlist.setSelectionBackground(Color.LIGHT_GRAY);
		jlist.setFont(new Font("楷体", Font.CENTER_BASELINE, 14));
		
		jlist.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.GRAY);
				g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
			}
		});
		
		boxModel = new DefaultComboBoxModel<>();
		List<Users>userList = ud.findAllUsers();
		
		String lineContent = "{";
		for(Users user : userList) {
			String content = user.getName();
			lineContent += "\"" + user.getName() + "\":\"" + user.getIsonline() + "\",";
			content += (1 == user.getIsonline()) ? " | 在线" : " | 离线";
			boxModel.addElement(content);
		}
		lineContent = lineContent.substring(0,lineContent.length()-1) + "}";
		Server.friendLine = lineContent;
		jlist.setModel(boxModel);

		jlist.setCellRenderer(new MyCellRenderer());

		//给指定下标的行添加高亮
//		jlist.setCellRenderer(new MyCellRenderer(new int[] {0,1}));
		
		initListener();
		
		jsp = new JScrollPane(jlist);
		
		this.add(jsp);
		
		this.setTitle("好友列表（服务器）");
		this.setBounds(300, 200, 180, 360);
//		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(FriendListFrame.class.getResource("/chats.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		new Thread(new MyFriendListListener()).start();
		
	}

	public void initListener() {
		jlist.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(2 == e.getClickCount()) {
					String content = jlist.getSelectedValue();
					String name = "";
					if(content.length()>5) {
						name += content.substring(0, content.length()-5);
					}
					for(Map.Entry<Users, Socket> entry : Server.uMap.entrySet()) {
						if(name.equals(entry.getKey().getName())) {
							try {
								entry.getValue().close();
								Server.uMap.remove(entry.getKey());
								System.out.println("强制" + name + "下线成功");
							} catch (IOException e1) {
								System.out.println("强制" + name + "下线失败");
								e1.printStackTrace();
							}
							break;
						}
					}
				}
			}
		});
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				UsersDao ud = new UsersDao();
				for(Map.Entry<Users, Socket> entry : Server.uMap.entrySet()) {
					Users u = entry.getKey();
					u.setIsonline(0);
					ud.updateUserIsonline(u);
				}
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
}

class MyFriendListListener implements Runnable{

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if(!FriendListFrame.MSG.equals("")) {
				if(FriendListFrame.boxModel.getSize()>0) {
					String msg[] = FriendListFrame.MSG.split("!");
					//上下线的标志串
					String tag = msg[msg.length-1];
					String lineStr = tag.equals("in") ? " | 离线" : " | 在线" ;
					//实际的用户名
					String name = FriendListFrame.MSG.substring(0, FriendListFrame.MSG.length()-tag.length()-1);
					//显示出来的
					String name_show = name + lineStr;
					if("in".equals(tag)){
						FriendListFrame.boxModel.removeElement(name_show);
						FriendListFrame.boxModel.addElement(name + " | 在线");
					}else if("out".equals(tag)){
						FriendListFrame.boxModel.removeElement(name_show);
						FriendListFrame.boxModel.addElement(name + " | 离线");
					}
					
					JSONObject jo = JSONObject.parseObject(Server.friendLine);
					boolean isNewUser = true;
					for(Entry<String,Object> entry :jo.entrySet()) {
						if(name.equals(entry.getKey())) {
							entry.setValue(tag.equals("in") ? "1" : "0");
							isNewUser = false;
							break;
						}
					}
					if(isNewUser)jo.put(name, "0");
					Server.friendLine = JSONObject.toJSONString(jo);
				}
				FriendListFrame.MSG = "";
			}
		}
	}
	
}



