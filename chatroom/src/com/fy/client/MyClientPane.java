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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MyClientPane {
	
	public static Socket s;
	private static JTextField jtf1,jtf2;
	private static JButton jb;
	public static JTextArea jta;
	private static JScrollPane jsp;
    private String control = "";
    private String myName = "?";
    //被监听改变的数据
    public static String strGet="";

	public static String friendLine = "{}";
	public static String friendLine_old = "{}";

	public MyClientPane(String myName) {
		super();
		this.myName = myName;
	}

	public void initPane() {
		JFrame frame=new JFrame(myName);
		Font font=new Font("微软雅黑", Font.BOLD, 12);

		jtf1=new JTextField("默认存在", 18);
		jtf1.setPreferredSize(new Dimension(180, 30));
		jtf1.setEditable(false);
		jtf1.setHorizontalAlignment(JTextField.CENTER);
		
		jtf2=new JTextField(18);
		jtf2.setPreferredSize(new Dimension(100, 30));
		jtf2.setMargin(new Insets(0, 5, 0, 5));
		jtf2.setEditable(true);
		
		jtf2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ENTER == e.getKeyCode()) {
					sendMsg();
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
				sendMsg();
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

		frame.add(jtf1,BorderLayout.NORTH);
		frame.add(jsp,BorderLayout.CENTER);
		sJPanel.add(jtf2);
		sJPanel.add(jb);
		frame.add(sJPanel,BorderLayout.SOUTH);
		
		frame.setBounds(800, 400, 350, 350);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MyClientPane.class.getResource("/chats.png")));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
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
					jtf1.setText(new Date().toString());
					sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public MyClientPane() {
		
	}
	
	public void startClient(Socket s0) {
		
		s=s0;
		
		initPane();
		
		MyClientTimeThread clientTimeThread=new MyClientPane().new MyClientTimeThread();
		clientTimeThread.start();
		
		MyClientThreadListener listener=new MyClientThreadListener();
		listener.start();
		
		//收消息
		Thread tRead=new ThreadGetMsg(s);
		tRead.start();
		
	}

	private static void sendMsg() {
		//发送
		String str=jtf2.getText();
		if(!str.equals("")){
			try {
				OutputStream out=null;
				//获得输出流（给客户端发消息）
				out=s.getOutputStream();
				out.write(str.getBytes());
			} catch (IOException e1) {
				strGet="连接错误或中断！\n";
				jtf2.setText("");
				return;
			}
			MyClientPane.strGet="我说："+str;
			jtf2.setText("");
		}else{
			JOptionPane.showMessageDialog(null, "您的输入不能为空！","【出错啦】", 
					 JOptionPane.ERROR_MESSAGE);
		}
	}

}

class ThreadGetMsg extends Thread{
	private Socket s;
	
	public ThreadGetMsg(Socket s){
		this.s=s;
	}
	
	public void run() {
		InputStream in=null;
		try {
			while(true){
				//获得输入流（接收消息）
				in=s.getInputStream();
				//TODO 这里byte接收大小的问题还未解决，暂时换成比1024大的数
				byte[]buf=new byte[2048];
				if(in!=null){
					int len=in.read(buf);
					if(len == -1)continue;
					String strFrom=new String(buf,0,len);
					if(strFrom.substring(0, 6).equals("---fyf")){
						MyClientPane.friendLine = strFrom.substring(6);
						continue;
					}else if(strFrom.substring(0, 6).equals("---fyc")){
						FriendListFrame.jsonStr_info = strFrom.substring(6);
						continue;
					}else if(strFrom.substring(0, 5).equals("---fy")){
						strFrom="服务器说："+strFrom.substring(5);
					}
					if(MyClientPane.strGet.equals(""))MyClientPane.strGet=strFrom;
				}
			}
		} catch (Exception e) {
			MyClientPane.strGet="服务器已断开";
			System.out.println("服务器已断开！");
		}finally{
			try {
				if(in!=null)in.close();
				if(s!=null)s.close();
			} catch (IOException e) {
				System.out.println("error2");
			}
		}
	}
}

/**
 * 监视strGet数据改变的线程类
 * @author Administrator
 *
 */
class MyClientThreadListener extends Thread{
	
	private MyClientPane myPane=null;
	
	public void run() {
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!MyClientPane.strGet.equals("")){
				if(myPane==null){
					myPane = new MyClientPane();
				}
				String str = MyClientPane.strGet;
				MyClientPane.jta.append(str + "\n");
				MyClientPane.jta.setCaretPosition(MyClientPane.jta.getDocument().getLength());
				MyClientPane.jta.validate();
				MyClientPane.strGet="";
			}
		}
	}
}

