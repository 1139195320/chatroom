package com.fy.client.start;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.fy.client.FriendListFrame;
import com.fy.client.MyClientPane;

public class ClientLoginPanel extends JFrame{

	private static String JDBC_PROPERTIESPATH ;
	private static Properties pps = null;
	//要请求的服务器端口号
	private static int SERVER_PORT;
	private static String SERVER_IP;
	public static Socket s;
	private static JPanel jpanel_name,jpanel_passwd,jpanel_button;
	private static JLabel jl_name,jl_passwd;
	private static JTextField jtf_name;
	private static JPasswordField jpf_passwd;
	private static JButton jb_regist,jb_ok;
	
	static {
		JDBC_PROPERTIESPATH=ClientLoginPanel.class.getResource("/server.properties").getPath();
		try {
			pps = new Properties();
			pps.load(new FileInputStream(JDBC_PROPERTIESPATH));
			SERVER_IP = pps.getProperty("server_ip");
			SERVER_PORT = Integer.valueOf(pps.getProperty("server_port"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new ClientLoginPanel();
	}
	
	public ClientLoginPanel() {
		Font font=new Font("微软雅黑", Font.BOLD, 12);

		jl_name=new JLabel("昵称：");
		jl_name.setFont(font);
		jl_passwd=new JLabel("密码：");
		jl_passwd.setFont(font);
		
		jtf_name=new JTextField(20);
		jtf_name.setHorizontalAlignment(JTextField.CENTER);
		jtf_name.setPreferredSize(new Dimension(100, 28));
		jtf_name.setEditable(true);
		jpf_passwd=new JPasswordField(20);
		jpf_passwd.setHorizontalAlignment(JPasswordField.CENTER);
		jpf_passwd.setPreferredSize(new Dimension(100, 28));
		jpf_passwd.setEditable(true);
		
		jpanel_name=new JPanel(new BorderLayout());
		jpanel_passwd=new JPanel(new BorderLayout());
		jpanel_button=new JPanel(new BorderLayout());
		
		jb_regist=new JButton("注册");
		jb_regist.setFont(font);
		jb_ok=new JButton("登录");
		jb_ok.setFont(font);
		
		initListener();
		
		this.setLayout(new BorderLayout(0,5));

		jpanel_name.add(jl_name,BorderLayout.WEST);
		jpanel_name.add(jtf_name,BorderLayout.CENTER);
		jpanel_passwd.add(jl_passwd,BorderLayout.WEST);
		jpanel_passwd.add(jpf_passwd,BorderLayout.CENTER);
		jpanel_button.add(jb_regist,BorderLayout.WEST);
		jpanel_button.add(jb_ok,BorderLayout.EAST);

		this.add(jpanel_name,BorderLayout.NORTH);
		this.add(jpanel_passwd,BorderLayout.CENTER);
		this.add(jpanel_button,BorderLayout.SOUTH);
		
		this.setTitle("登录");
		this.setBounds(800, 400, 250, 120);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(MyClientPane.class.getResource("/chats.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}

	private void initListener() {
		jtf_name.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ENTER == e.getKeyCode()) {
					//登录
					loginToServer();
				}
			}
		});

		jpf_passwd.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ENTER == e.getKeyCode()) {
					//登录
					loginToServer();
				}
			}
		});

		jb_regist.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ENTER == e.getKeyCode()) {
					//注册
					registFromServer();
				}
			}
		});

		jb_ok.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_ENTER == e.getKeyCode()) {
					//登录
					loginToServer();
				}
			}
		});
		
		jb_regist.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//注册
				registFromServer();
			}
		});
		jb_ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//登录
				loginToServer();
			}
		});
	}

	private void loginToServer() {
		try {
			loginOrRegist("l");
		} catch (Exception ex) {
			System.out.println("客户端登录失败...!!!");
			JOptionPane.showMessageDialog(null, "登陆失败，服务器貌似没开启呢！","【出错啦】", 
					 JOptionPane.ERROR_MESSAGE);
		}
	}

	private void registFromServer() {
		try {
			loginOrRegist("r");
		} catch (Exception ex) {
			System.out.println("客户端注册失败...!!!");
			JOptionPane.showMessageDialog(null, "注册失败，服务器貌似没开启呢！","【出错啦】", 
					 JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loginOrRegist(String tag) throws UnknownHostException, IOException {
		String name=jtf_name.getText();
		char c_passwd[]=jpf_passwd.getPassword();
		String passwd="";
		if(c_passwd!=null && c_passwd.length>0) {
			for(char c:c_passwd) {
				passwd+=c;
			}
		}
		if(name!=null && passwd!=null) {
			if(!name.trim().equals("") && !passwd.trim().equals("")) {
				OutputStream os=null;
				InputStream is=null;
				s=new Socket(SERVER_IP, SERVER_PORT);
				os=s.getOutputStream();
				byte []b_to=new byte[1024];
				String str_to=tag.equals("l")?"000l-":"000r-";
				str_to+=(name+"-.-"+passwd);
				b_to=str_to.getBytes();
				os.write(b_to);
				is=s.getInputStream();
				byte []b_from=new byte[1024];
				int len =is.read(b_from);
				String result=new String(b_from,0,len);
				
				if(result.equals("l0")) {
					//登陆成功
					this.setVisible(false);
					//群聊聊天界面
					new MyClientPane(name).startClient(s);
					//好友列表界面
					new FriendListFrame(name ,s);
				}else {
					if(result.equals("l1")){
						//用户名或密码错误
						JOptionPane.showMessageDialog(null, "您输入的用户名或密码错误！","【出错啦】", 
								 JOptionPane.ERROR_MESSAGE);
					}else if(result.equals("l2")){
						//用户名或密码错误
						JOptionPane.showMessageDialog(null, "很抱歉，该用户已登录！","【出错啦】", 
								 JOptionPane.ERROR_MESSAGE);
					}else if(result.equals("r0")) {
						//注册成功
						JOptionPane.showMessageDialog(null, "恭喜您注册成功！","【Lucky】", 
								 JOptionPane.YES_OPTION);
					}else if(result.equals("r1")) {
						//用户名已存在
						JOptionPane.showMessageDialog(null, "该昵称已存在！","【出错啦】", 
								 JOptionPane.ERROR_MESSAGE);
					}
					is.close();
					os.flush();
					os.close();
					jpf_passwd.setText("");
				}
			}else {
				JOptionPane.showMessageDialog(null, "用户名和密码不能为空！","【出错啦】", 
						 JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
		
}
