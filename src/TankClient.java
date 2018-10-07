import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Tank Client 端
 * @author TinyA
 * @date 2018/10/7 
 */
public class TankClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public final int GAME_WIDTH = 800;
	public final int GAME_HEIGHT = 600;
	public static final int QUIT = 1;
	
	int x, y;
	
	Tank myTank = null;
	Random r = new Random();
	
	List<Tank> tanks = new ArrayList<Tank>();
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	
	MyDialog dialog = null;
	
	NetClient nc = new NetClient(this);
	
	public static void main(String args[]) {
		new TankClient();
	}
	
	private void showErrorMsgDialog(String str) {
		JOptionPane.showMessageDialog(this, str);
	}
	
	TankClient() {
		
		x = r.nextInt(GAME_WIDTH - Tank.WIDTH);
		y = 30 + r.nextInt(GAME_HEIGHT - Tank.HEIGHT - 30);
		myTank = new Tank(x, y, true, this);

		dialog = new MyDialog(this, "Enter Server's & Client's IP and UDP port", true);
		
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setTitle("TankWar ID:" + myTank.ID);
		setResizable(false);
		setLocationRelativeTo(null);
		
		this.addWindowListener(new WindowAdapter() {
			
			/**
			 * 通知 Server 该 Client 退出
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					nc.dos.writeInt(QUIT); //tell server KIA this Client
					nc.send(new TankDeadMsg(myTank.ID)); //then tell all the others that remove this one
				} catch (IOException e1) {
					showErrorMsgDialog("Connection reset, EXIT!");
					System.exit(0);
				} finally {
					try {
						if(nc.s != null) {
							nc.s.close();
							nc.s = null;
						}
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					System.exit(0);
				}
			}
			
		});
		
		getContentPane().setBackground(Color.BLACK);
		setVisible(true);

		new Thread(new PaintThread()).start();
		addKeyListener(new KeyMonitor());

	}

	public void paint(Graphics g) {
		super.paint(g);

		myTank.draw(g);
		
		for(int i = 0; i < tanks.size(); i++) {
			tanks.get(i).draw(g);
		}
		
		for(int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);

			if(m.hitTank(myTank)) {
				TankDeadMsg tankDeadMsg = new TankDeadMsg(myTank.ID);
				nc.send(tankDeadMsg);
				MissileDeadMsg missileDeadMsg = new MissileDeadMsg(m.tankID, m.ID);
				nc.send(missileDeadMsg);
			}
			
			m.draw(g);
		}
		
		for(int i = 0; i < explodes.size(); i++) {
			explodes.get(i).draw(g);
		}
		
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.drawString("Missiles: " + missiles.size(), 10, 50);
		g.drawString("Tanks:  	" + tanks.size(), 10, 70);
		g.setColor(c);
		
	}

	private class PaintThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class KeyMonitor extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
		
	}
	
	JTextField jtfServerIP = new JTextField("127.0.0.1", 12); 

	/**
	 * 指定 server IP & 本地 UDP port 
	 */
	public class MyDialog extends JDialog implements ActionListener{

		private static final long serialVersionUID = 1L;
		
		JLabel jlServerIP = new JLabel("Server IP:");
		JLabel jlClientUDPPort = new JLabel("Client UDP_Port:");
		String udpPort = String.valueOf(r.nextInt(33333) + 10000);
		JTextField jtfClientUDPPort = new JTextField(udpPort, 5);
		JButton jbConfirm = new JButton("Confirm");
		
		MyDialog(JFrame frame, String title, boolean modal) {
			super(frame, title, modal);
			this.setLayout(new FlowLayout());
			this.add(jlServerIP);
			this.add(jtfServerIP);
			this.add(jlClientUDPPort);
			this.add(jtfClientUDPPort);
			this.add(jbConfirm);
			this.pack();
			this.setLocationRelativeTo(frame);
			
			jbConfirm.registerKeyboardAction(this, 
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			
			jbConfirm.addActionListener(this);
			
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				
			});
			
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(jbConfirm)) {
				try {
					String serverIP = jtfServerIP.getText().trim();
					int clientUDPPort = Integer.parseInt(jtfClientUDPPort.getText().trim());
					nc.connect(serverIP, TankServer.TCP_PORT, clientUDPPort); 
					//connect to Server by use Server's IP & TCP_Port + Client UDP port
				} catch(NumberFormatException e1) {
					jtfClientUDPPort.setText(udpPort);
					jtfClientUDPPort.requestFocus(true);
				}
				dispose();
			}
		}
		
	}
	
}
