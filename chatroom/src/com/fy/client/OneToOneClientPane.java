package com.fy.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class OneToOneClientPane {
	
	private static JTextField jtf_time,jtf_input;
	private static JButton jb;
	public static JTextArea jta;
	private static JScrollPane jsp;
    private String control = "";
    private static String myName = "?";
    private static String toName = "?";
    //被监听改变的数据
    public static String strGet="";
    
    //与服务器的连接
    private static Socket socketToServer;

	public static String friendLine = "{}";
	public static String friendLine_old = "{}";

	public OneToOneClientPane(String name_from ,String name_to ,Socket s) {
		super();
		socketToServer = s;
		myName = name_from;
		toName = name_to;
	}

	public void initPane() {
		JFrame frame=new JFrame("To : " + toName);
		Font font=new Font("微软雅黑", Font.BOLD, 12);

		jtf_time=new JTextField("默认存在", 18);
		jtf_time.setPreferredSize(new Dimension(180, 30));
		jtf_time.setEditable(false);
		jtf_time.setHorizontalAlignment(JTextField.CENTER);
		
		jtf_input=new JTextField(18);
		jtf_input.setPreferredSize(new Dimension(100, 30));
		jtf_input.setMargin(new Insets(0, 5, 0, 5));
		jtf_input.setEditable(true);
		
		jtf_input.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ENTER == e.getKeyCode()) {
					sendMsg(null);
				}
			}
		});
		
		jb=new JButton("发送");
		jb.setPreferredSize(new Dimension(60, 30));
		jb.setMargin(new Insets(2, 2, 2, 2));
		jb.setFont(font);
		//发送按钮的点击事件
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMsg(null);
			}
		});
		
		jta=new JTextArea();
		jta.setSize(new Dimension(290,250));
		jta.setMargin(new Insets(0, 5, 0, 5));
		jta.setText("");
		jta.setColumns(20);
		jta.setRows(12);
		jta.setLineWrap(true);
		jta.setEditable(false);
		jta.setCaretPosition(jta.getDocument().getLength());
		
		jsp=new JScrollPane(jta);
		
		JPanel sJPanel=new JPanel();
		BorderLayout bl=new BorderLayout();
		frame.setLayout(bl);

		frame.add(jtf_time,BorderLayout.NORTH);
		frame.add(jsp,BorderLayout.CENTER);
		sJPanel.add(jtf_input);
		sJPanel.add(jb);
		frame.add(sJPanel,BorderLayout.SOUTH);
		
		frame.addWindowListener(new WindowListener() {
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
				FriendListFrame.chatNow.remove(toName);
				List<String> showIndexName =FriendListFrame.showIndexName;
				Integer index = FriendListFrame.boxModel.getIndexOf(toName + " | 在线");
				if(-1 == index)index = FriendListFrame.boxModel.getIndexOf(toName + " | 离线");
				if(-1 != FriendListFrame.boxModel.getIndexOf(toName + " | 在线") 
						|| -1 != FriendListFrame.boxModel.getIndexOf(toName + " | 离线"))
				showIndexName.remove(toName);
				List<Integer> showIndex = new ArrayList<Integer>();
				for(String name : FriendListFrame.showIndexName) {
					index = FriendListFrame.boxModel.getIndexOf(name + " | 在线");
					if(-1 == index)index = FriendListFrame.boxModel.getIndexOf(name + " | 离线");
					showIndex.add(index);
				}
				Integer []temp = (Integer[])showIndex.toArray(new Integer[showIndex.size()]);
				FriendListFrame.jlist.setCellRenderer(new MyCellRenderer(temp));
				//TODO 发消息给服务器该未读消息已看，修改其读取状态
				String msgToServerStateChange = "{\"name_from\":\"" + toName + "\",\"name_to\":\"" + myName + "\"}";
				sendMsg(msgToServerStateChange);
				
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		frame.setBounds(800, 400, 350, 350);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(OneToOneClientPane.class.getResource("/chats.png")));
		//关闭该窗口时不关闭程序
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		JSONObject jo = JSONObject.parseObject(FriendListFrame.jsonStr_info);
		JSONArray ja_from = jo.getJSONArray("jsonStr_from");
		JSONArray ja_to = jo.getJSONArray("jsonStr_to");
		
		Map<Timestamp, JSONObject> jsonMap = new HashMap<Timestamp , JSONObject>();
		for(Object obj : ja_from) {
			JSONObject jo_from = (JSONObject) obj;
			if(toName.equals(jo_from.get("name"))) {
				jsonMap.put(Timestamp.valueOf(jo_from.getString("sendtime")), jo_from);
			}
		}
		for(Object obj : ja_to) {
			JSONObject jo_to = (JSONObject) obj;
			if(toName.equals(jo_to.get("name"))) {
				jsonMap.put(Timestamp.valueOf(jo_to.getString("sendtime")), jo_to);
			}
		}
		Timestamp [] tArray = new Timestamp[jsonMap.size()];
		int i = 0;
		for(Map.Entry<Timestamp, JSONObject> entry : jsonMap.entrySet()) {
			tArray[i] = entry.getKey();
			i ++;
		}
		Arrays.sort(tArray);
		String str_content = "";
		for(Timestamp t : tArray) {
			JSONObject jotemp = jsonMap.get(t);
//			System.out.println(t + " : " + jotemp.get("content"));
			if("get".equals(jotemp.get("tag"))) {
				str_content += (toName + "说：" + jotemp.get("content") + "\n");
			}else if("send".equals(jotemp.get("tag"))) {
				str_content += ("我说：" + jotemp.get("content") + "\n");
			}
		}
		jta.append(str_content);
		jta.validate();
		
		//TODO 接收消息实时更新界面还未实现
		
	}
	
	/**
	 * 显示并更新界面时间的线程类
	 * @author Administrator
	 *
	 */
	class MyClientTimeThread extends Thread{
		
		private boolean suspend=false;
		
		public void setSuspend(boolean suspend) {  
	        if (!suspend) {  
	            synchronized (control) {  
	                control.notifyAll();  
	            }  
	        }  
	        this.suspend = suspend;  
	    }  
	  
	    public boolean isSuspend() {  
	        return this.suspend;  
	    }
		
		public void run() {
			try {
				while(true){
					synchronized (control) {  
		                if (suspend) {  
		                    try {  
		                        control.wait();  
		                    } catch (InterruptedException e) {  
		                        e.printStackTrace();  
		                    }  
		                }  
		            }
					jtf_time.setText(new Date().toString());
					sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public OneToOneClientPane() {
		
	}
	
	public void startClient() {
		
		initPane();
		
		MyClientTimeThread clientTimeThread=new OneToOneClientPane().new MyClientTimeThread();
		clientTimeThread.start();
		
		new MyClientOneToOneThreadListener().start();
		
	}

	private static void sendMsg(String msg) {
		//发送
		String str=jtf_input.getText();
		boolean isMsg = false;
		if(msg != null) {
			str = msg;
			isMsg = true;
		}
		if(!str.equals("")){
			try {
				OutputStream os = socketToServer.getOutputStream();
				if(isMsg) {
					//将Msg发送给服务器
					os.write(("cstate" + str + "etatsc").getBytes());
					os.flush();
				}else {
					//将输入的信息发送给服务器
					String info = "{"
							+ "\"name_from\":\"" + myName + "\""
							+ ", \"name_to\":\"" + toName + "\""
							+ ", \"content\":\"" + str + "\""
							+ "}";
					os.write(("fychat" + info + "tahcyf").getBytes());
					os.flush();
				}
			} catch (IOException e1) {
				strGet="连接错误或中断！\n";
				jtf_input.setText("");
				return;
			}
			OneToOneClientPane.strGet="我说："+str;
			jtf_input.setText("");
		}else{
			JOptionPane.showMessageDialog(null, "您的输入不能为空！","【出错啦】", 
					 JOptionPane.ERROR_MESSAGE);
		}
	}
}

/**
 * 监视strGet数据改变的线程类
 * @author Administrator
 *
 */
class MyClientOneToOneThreadListener extends Thread{
	
	public void run() {
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!OneToOneClientPane.strGet.equals("")){
				String str = OneToOneClientPane.strGet;
				OneToOneClientPane.jta.append(str + "\n");
				OneToOneClientPane.jta.setCaretPosition(OneToOneClientPane.jta.getDocument().getLength());
				OneToOneClientPane.jta.validate();
				OneToOneClientPane.strGet="";
			}
		}
	}
}


