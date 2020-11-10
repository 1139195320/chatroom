package com.fy.client.start;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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

/**
 * @author jack
 */
public class ClientLoginPanel extends JFrame {

	/**
	 * 要请求的服务器端口号
	 */
	private static int SERVER_PORT;
	private static String SERVER_IP;
	public static Socket s;
	private static JTextField jtf_name;
	private static JPasswordField jpfPasswd;
	private static JButton jb_register, jb_ok;

	static {
		String jdbcPropertiesPath = ClientLoginPanel.class
				.getResource("/server.properties").getPath();
		try {
			Properties pps = new Properties();
			pps.load(new FileInputStream(jdbcPropertiesPath));
			SERVER_IP = pps.getProperty("server_ip");
			SERVER_PORT = Integer.parseInt(pps.getProperty("server_port"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ClientLoginPanel();
	}

	public ClientLoginPanel() {
		Font font = new Font("微软雅黑", Font.BOLD, 12);

		JLabel jlName = new JLabel("昵称：");
		jlName.setFont(font);
		JLabel jlPasswd = new JLabel("密码：");
		jlPasswd.setFont(font);

		jtf_name = new JTextField(20);
		jtf_name.setHorizontalAlignment(JTextField.CENTER);
		jtf_name.setPreferredSize(new Dimension(100, 28));
		jtf_name.setEditable(true);
		jpfPasswd = new JPasswordField(20);
		jpfPasswd.setHorizontalAlignment(JPasswordField.CENTER);
		jpfPasswd.setPreferredSize(new Dimension(100, 28));
		jpfPasswd.setEditable(true);

		JPanel jpanelName = new JPanel(new BorderLayout());
		JPanel jpanelPasswd = new JPanel(new BorderLayout());
		JPanel jpanelButton = new JPanel(new BorderLayout());

		jb_register = new JButton("注册");
		jb_register.setFont(font);
		jb_ok = new JButton("登录");
		jb_ok.setFont(font);

		initListener();

		this.setLayout(new BorderLayout(0, 5));

		jpanelName.add(jlName, BorderLayout.WEST);
		jpanelName.add(jtf_name, BorderLayout.CENTER);
		jpanelPasswd.add(jlPasswd, BorderLayout.WEST);
		jpanelPasswd.add(jpfPasswd, BorderLayout.CENTER);
		jpanelButton.add(jb_register, BorderLayout.WEST);
		jpanelButton.add(jb_ok, BorderLayout.EAST);

		this.add(jpanelName, BorderLayout.NORTH);
		this.add(jpanelPasswd, BorderLayout.CENTER);
		this.add(jpanelButton, BorderLayout.SOUTH);

		this.setTitle("登录");
		this.setBounds(800, 400, 250, 120);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(MyClientPane.class
				.getResource("/chats.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	private void initListener() {
		KeyListener loginKeyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					//登录
					loginToServer();
				}
			}
		};
		KeyListener registerKeyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					//注册
					registerFromServer();
				}
			}
		};
		jtf_name.addKeyListener(loginKeyListener);

		jpfPasswd.addKeyListener(loginKeyListener);

		jb_register.addKeyListener(registerKeyListener);

		jb_ok.addKeyListener(loginKeyListener);

		jb_register.addActionListener(e -> {
			//注册
			registerFromServer();
		});
		jb_ok.addActionListener(e -> {
			//登录
			loginToServer();
		});
	}

	private void loginToServer() {
		try {
			loginOrRegister("l");
		} catch (Exception ex) {
			System.out.println("客户端登录失败...!!!");
			JOptionPane.showMessageDialog(null,
					"登陆失败，服务器貌似没开启呢！", "【出错啦】",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void registerFromServer() {
		try {
			loginOrRegister("r");
		} catch (Exception ex) {
			System.out.println("客户端注册失败...!!!");
			JOptionPane.showMessageDialog(null,
					"注册失败，服务器貌似没开启呢！", "【出错啦】",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loginOrRegister(String tag) throws IOException {
		String name = jtf_name.getText();
		char[] cPasswd = jpfPasswd.getPassword();
		StringBuilder passwd = new StringBuilder();
		if (cPasswd != null && cPasswd.length > 0) {
			for (char c : cPasswd) {
				passwd.append(c);
			}
		}
		if (name != null) {
			if (!"".equals(name.trim()) && !"".equals(passwd.toString().trim())) {
				OutputStream os;
				InputStream is;
				s = new Socket(SERVER_IP, SERVER_PORT);
				os = s.getOutputStream();
				byte[] bTo;
				String strTo = "l".equals(tag) ? "000l-" : "000r-";
				strTo += (name + "-.-" + passwd);
				bTo = strTo.getBytes();
				os.write(bTo);
				is = s.getInputStream();
				byte[] bFrom = new byte[1024];
				int len = is.read(bFrom);
				String result = new String(bFrom, 0, len);

				if ("l0".equals(result)) {
					//登陆成功
					this.setVisible(false);
					//群聊聊天界面
					new MyClientPane(name).startClient(s);
					//好友列表界面
					new FriendListFrame(name, s);
				} else {
					switch (result) {
						case "l1":
							//用户名或密码错误
							JOptionPane.showMessageDialog(null,
									"您输入的用户名或密码错误！", "【出错啦】",
									JOptionPane.ERROR_MESSAGE);
							break;
						case "l2":
							//用户名或密码错误
							JOptionPane.showMessageDialog(null,
									"很抱歉，该用户已登录！", "【出错啦】",
									JOptionPane.ERROR_MESSAGE);
							break;
						case "r0":
							//注册成功
							JOptionPane.showMessageDialog(null,
									"恭喜您注册成功！", "【Lucky】",
									JOptionPane.YES_OPTION);
							break;
						case "r1":
							//用户名已存在
							JOptionPane.showMessageDialog(null,
									"该昵称已存在！", "【出错啦】",
									JOptionPane.ERROR_MESSAGE);
							break;
						default:
							break;
					}
					is.close();
					os.flush();
					os.close();
					jpfPasswd.setText("");
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"用户名和密码不能为空！", "【出错啦】",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

}
