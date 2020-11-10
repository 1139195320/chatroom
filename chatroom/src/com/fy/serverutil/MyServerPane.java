package com.fy.serverutil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.fy.dao.impl.UsersDao;
import com.fy.entity.Users;
import com.fy.server.FriendListFrame;
import com.fy.server.start.Server;

/**
 * @author jack
 */
public class MyServerPane {
	
	private static JFrame frame;
	private static JTextField jtf_time,jtf_input;
	private static JButton jb_ok;
	public static JTextArea jta;
	private final String control = "";
	/**
	 * 被监听改变的数据
	 */
	public static String strGet = "";

	public void initPane() {
		frame=new JFrame("服务器");
		Font font=new Font("微软雅黑", Font.BOLD, 12);

		jtf_time=new JTextField("默认存在", 18);
		jtf_time.setPreferredSize(new Dimension(300, 30));
		jtf_time.setEditable(false);
		jtf_time.setHorizontalAlignment(JTextField.CENTER);
		
		jtf_input=new JTextField(18);
		jtf_input.setPreferredSize(new Dimension(100, 30));
		jtf_input.setMargin(new Insets(0, 5, 0, 5));
		jtf_input.setEditable(true);
		
		jb_ok=new JButton("发送");
		jb_ok.setPreferredSize(new Dimension(60, 30));
		jb_ok.setMargin(new Insets(2, 2, 2, 2));
		jb_ok.setFont(font);
		
		jta=new JTextArea();
		jta.setSize(new Dimension(290,250));
		jta.setMargin(new Insets(0, 5, 0, 5));
		jta.setText("");
		jta.setColumns(20);
		jta.setRows(12);
		jta.setLineWrap(true);
		//设置该显示框不可编辑
		jta.setEditable(false);
		jta.setCaretPosition(jta.getDocument().getLength());

		JScrollPane jsp = new JScrollPane(jta);
		jsp.setSize(new Dimension(300,250));

		JPanel sjPanel=new JPanel();
		BorderLayout bl=new BorderLayout();
		frame.setLayout(bl);

		frame.add(jtf_time,BorderLayout.NORTH);
		frame.add(jsp,BorderLayout.CENTER);
		sjPanel.add(jtf_input);
		sjPanel.add(jb_ok);
		frame.add(sjPanel,BorderLayout.SOUTH);
		
		initListener();
		
		frame.setBounds(800, 400, 350, 350);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MyServerPane.class.getResource("/chats.png")));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		MyServerTimeThread serverTimeThread=new MyServerTimeThread();
		serverTimeThread.start();
		
		// 打开MyPane的数据监视线程
		MyServerThreadListener listener=new MyServerThreadListener();
		listener.start();
		
		// 打开Server.friendline的数据监视线程
		new MyServerFriendLine().start();
		
		new FriendListFrame();
		
	}

	private void initListener() {
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
		// 发送按钮的点击事件
		jb_ok.addActionListener(e -> sendMsg(null));
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
				UsersDao ud = new UsersDao();
				for(Map.Entry<Users, Socket> entry : Server.uMap.entrySet()) {
					Users u = entry.getKey();
					u.setIsonline(0);
					ud.updateUserIsonline(u);
					sendMsg("各位亲们，服务器要关闭了，聊天结束，拜~");
				}
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	public static void sendMsg(String msg) {
		//发送
		String str = jtf_input.getText();
		if (msg != null && !"".equals(msg)) {
			str = msg;
		}
		if (str != null && !"".equals(str)) {
			try {
				OutputStream out;
				Socket s;
				for (Map.Entry<Users, Socket> entry : Server.uMap.entrySet()) {
					s = entry.getValue();
					//获得输出流（给客户端发消息）
					out = s.getOutputStream();
					out.write(("---fy" + str).getBytes());
					out.flush();
				}
			} catch (IOException e1) {
				strGet = "连接错误或中断！\n";
				jtf_input.setText("");
				return;
			}
			if ("f".equals(str.substring(0, 1))) {
				return;
			}
			strGet = "我说：" + str;
			jtf_input.setText("");
		} else {
			JOptionPane.showMessageDialog(null,
					"您的输入不能为空！", "【出错啦】",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 显示并更新界面时间的线程类
	 * @author Administrator
	 *
	 */
	class MyServerTimeThread extends Thread{
		
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
		
		@Override
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
}

/**
 * 监视strGet数据改变的线程类
 * @author Administrator
 *
 */
class MyServerThreadListener extends Thread{
	
	private MyServerPane myPane=null;
	
	@Override
	public void run() {
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!"".equals(MyServerPane.strGet)){
				if(myPane==null){
					myPane = new MyServerPane();
				}
				MyServerPane.jta.append(MyServerPane.strGet+"\n");
				MyServerPane.jta.setCaretPosition(MyServerPane.jta.getDocument().getLength());
				MyServerPane.jta.validate();
				MyServerPane.strGet="";
			}
		}
	}
}

/**
 * 监视friendLine数据改变的线程类
 * @author Administrator
 *
 */
class MyServerFriendLine extends Thread{
	
	@Override
	public void run() {
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!Server.friendLine.equals(Server.friendLine_old)){
				MyServerPane.sendMsg("f" + Server.friendLine);
				Server.friendLine_old = Server.friendLine;
			}
		}
	}
}

