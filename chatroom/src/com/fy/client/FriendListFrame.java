package com.fy.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static java.awt.Font.CENTER_BASELINE;

/**
 * @author jack
 */
public class FriendListFrame extends JFrame{
	
	private String myName ;
	
	public static JList<String> jList;
	public static DefaultComboBoxModel<String> boxModel;
	/**
	 * 存放已打开 OneToOne 聊天的对象
	 */
	public static List<String> chatNow = new ArrayList<>();

	/**
	 * 与服务器的连接
	 */
	private Socket socketToServer;

	/**
	 * 存放发出和接收的消息JSON字符串
	 */
	public static String jsonStr_info = "";
	public static String jsonStr_info_old = "";
	
	public static boolean isFirst = true;

	/**
	 * 有新消息没看的对象的用户名集合
	 */
	public static List<String> showIndexName = new ArrayList<>();
	
	public FriendListFrame(String myName, Socket s) {
		
		this.socketToServer = s;
		this.myName = myName;
		
		jList = new JList<>();
		jList.setFixedCellHeight(30);
		jList.setSelectionForeground(Color.RED);
		jList.setSelectionBackground(Color.LIGHT_GRAY);
		jList.setFont(new Font("楷体", CENTER_BASELINE, 14));
		
		jList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.GRAY);
				g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
			}
		});
		
		boxModel = new DefaultComboBoxModel<>();
		
		String jsonStr = MyClientPane.friendLine;
		JSONObject jo =JSONObject.parseObject(jsonStr);
		for(Entry<String,Object> entry : jo.entrySet()) {
			String content = entry.getKey();
			content += (1 == Integer.parseInt((String)entry.getValue())) ? " | 在线" : " | 离线";
			boxModel.addElement(content);
		}
		
		jList.setModel(boxModel);

		jList.setCellRenderer(new MyCellRenderer());
		
		//给指定下标的行添加高亮
		/*jList.setCellRenderer(new MyCellRenderer(new int[] {0,1}));*/
		
		initListener();

		JScrollPane jsp = new JScrollPane(jList);
		
		this.add(jsp);
		
		this.setTitle("好友列表（客户端）");
		this.setBounds(300, 200, 180, 360);
		/*this.setLocationRelativeTo(null);*/
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(FriendListFrame.class.getResource("/chats.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		new Thread(new MyFriendListListener()).start();
		new MyClientDbMsgThread().start();
	}

	public void initListener() {
		jList.addMouseListener(new MouseListener() {
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
					String content = jList.getSelectedValue();
					String nameTo = "";
					if (content.length() > 5) {
						nameTo += content.substring(0, content.length() - 5);
					}
					String nameFrom;
					nameFrom = myName;
					if(!chatNow.contains(nameTo) && !nameTo.equals(nameFrom)) {
						new OneToOneClientPane(nameFrom, nameTo , socketToServer).startClient();
						chatNow.add(nameTo);
					}
				}
			}
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
			if(!MyClientPane.friendLine.equals(MyClientPane.friendLine_old)) {
				
				String jsonStr = MyClientPane.friendLine;
				JSONObject jo =JSONObject.parseObject(jsonStr);
				if(FriendListFrame.isFirst) {
					for(Entry<String,Object> entry : jo.entrySet()) {
						String content = entry.getKey();
						content += (1 == Integer.parseInt((String)entry.getValue()))
								? " | 在线" : " | 离线";
						FriendListFrame.boxModel.addElement(content);
					}
					FriendListFrame.isFirst = false;
				}else {
					for(Entry<String,Object> entry : jo.entrySet()) {
						String content = entry.getKey();
						String tag = (String) entry.getValue();
						content += (1 == Integer.parseInt(tag)) ? " | 在线" : " | 离线";
						String contentOld = entry.getKey() +
								(1 != Integer.parseInt((String)entry.getValue()) ? " | 在线" : " | 离线");
						if (-1 != FriendListFrame.boxModel.getIndexOf(contentOld)) {
							FriendListFrame.boxModel.removeElement(contentOld);
							FriendListFrame.boxModel.addElement(content);
							break;
						}
						if(-1 == FriendListFrame.boxModel.getIndexOf(contentOld)
								&& -1 == FriendListFrame.boxModel.getIndexOf(content)) {
							FriendListFrame.boxModel.addElement(content);
							break;
						}
					}
				}
				MyClientPane.friendLine_old = MyClientPane.friendLine;
			}
		}
	}
	
}

/**
 * 监视jsonStr_info数据改变的线程类
 *
 * @author jack
 */
class MyClientDbMsgThread extends Thread {

	@Override
	public void run() {
		while (true) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Integer> showIndex = new ArrayList<>();
			int index;
			if (!FriendListFrame.jsonStr_info.equals(FriendListFrame.jsonStr_info_old)) {
				JSONObject jo = JSONObject.parseObject(FriendListFrame.jsonStr_info);
				JSONArray ja = jo.getJSONArray("jsonStr_from");
				if (ja != null) {
					for (Object obj : ja) {
						JSONObject joFrom = (JSONObject) obj;
						if ("0".equals(joFrom.get("readstate"))) {
							String name = (String) joFrom.get("name");
							if (!FriendListFrame.showIndexName.contains(name)) {
								FriendListFrame.showIndexName.add(name);
							}
						}
					}
				}
				for (String name : FriendListFrame.showIndexName) {
					index = FriendListFrame.boxModel.getIndexOf(name + " | 在线");
					if (-1 == index) {
						index = FriendListFrame.boxModel.getIndexOf(name + " | 离线");
					}
					showIndex.add(index);
				}
				Integer[] temp = showIndex.toArray(new Integer[0]);
				FriendListFrame.jList.setCellRenderer(new MyCellRenderer(temp));
				FriendListFrame.jsonStr_info_old = FriendListFrame.jsonStr_info;
			}
		}
	}
}



