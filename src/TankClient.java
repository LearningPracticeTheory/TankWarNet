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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TankClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public final int GAME_WIDTH = 800;
	public final int GAME_HEIGHT = 600;
	private static final int QUIT = 1;
//	private final int TANK_FIRST_NUM = 5;
	
//	int x = 50, y = 50;
	
//	Image img = null;
	
	Tank myTank = new Tank(50, 50, true, this);
//	Tank enemyTank = new Tank(100, 100, false, this);
	List<Tank> tanks = new ArrayList<Tank>();
	
//	Missile m = null;
	List<Missile> missiles = new ArrayList<Missile>();
//	Explode explode = new Explode(100, 100, this);
	List<Explode> explodes = new ArrayList<Explode>();
	
	NetClient nc = new NetClient(this);
	
	public static void main(String args[]) {
		new TankClient();
	}
	
	TankClient() {
		
		new MyDialog(this, "Enter Server's & Client's IP and UDP port", true);
		
//		setSize(800, 600);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setTitle("TankWar ID:" + myTank.ID);
		setResizable(false);
		setLocationRelativeTo(null);
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				try {
					nc.dos.writeInt(QUIT);
				} catch (IOException e1) {
					e1.printStackTrace();
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
		/*
		for(int i = 0; i < TANK_FIRST_NUM; i++) {
			tanks.add(new Tank(50 + (i + 1) * 40, 50, false, this));
		}
		*/
		new Thread(new PaintThread()).start();
		addKeyListener(new KeyMonitor());
		/*
		nc.connect("localhost", TankServer.TCP_PORT); 
			//use Client's IP connect to Server
			//connect to Server's TCP port;
		*/
	}

	public void paint(Graphics g) {
		super.paint(g);

		myTank.draw(g);
//		enemyTank.draw(g);
		
		for(int i = 0; i < tanks.size(); i++) {
			tanks.get(i).draw(g);
		}
		
//		if(m != null) m.draw(g);
		for(int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			/*
			if(m.hitTank(enemyTank) && enemyTank.isLive()) {
				m.setLive(false);
				enemyTank.setLive(false);
			}
			*/
//			m.hitTanks(tanks);
			if(m.hitTank(myTank)) {
				TankDeadMsg tankDeadMsg = new TankDeadMsg(myTank.ID);
				nc.send(tankDeadMsg);
				MissileDeadMsg missileDeadMsg = new MissileDeadMsg(m.tankID, m.ID);
				nc.send(missileDeadMsg);
			}
			
			m.draw(g);
		}
		
//		explode.draw(g);
		for(int i = 0; i < explodes.size(); i++) {
			explodes.get(i).draw(g);
		}
		
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.drawString("Missiles: " + missiles.size(), 10, 50);
		g.drawString("Tanks:  	" + tanks.size(), 10, 70);
		g.setColor(c);
		
//		y += 8;
	}
	/*
	@Override
	public void update(Graphics g) {
		if(img == null) {
			img = this.createImage(WIDTH, HEIGHT);//JFrame
		}
		Graphics offScreenGraphics = img.getGraphics();
		Color c = offScreenGraphics.getColor();
		offScreenGraphics.setColor(Color.BLACK);
		offScreenGraphics.fillRect(0, 0, WIDTH, HEIGHT);
		paint(offScreenGraphics);
		offScreenGraphics.setColor(c);
		g.drawImage(img, 0, 0, null);
	}
	*/
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
//key pressed && released on Tank will make more stable 
	
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
	
	
	private class MyDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		JLabel jlServerIP = new JLabel("Server IP:");
		JTextField jtfServerIP = new JTextField("192.168.43.98", 12);
//		JLabel jlClientIP = new JLabel("Client IP:");
//		JTextField jtfClientIP = new JTextField("192.168.140.1", 12);
		JLabel jlClientUDPPort = new JLabel("Client UDP_Port:");
		JTextField jtfClientUDPPort = new JTextField("6666", 5);
		JButton jbConfirm = new JButton("Confirm");
		
		MyDialog(JFrame frame, String title, boolean modal) {
			super(frame, title, modal);
			this.setLayout(new FlowLayout());
			this.add(jlServerIP);
			this.add(jtfServerIP);
//			this.add(jlClientIP);
//			this.add(jtfClientIP);
			this.add(jlClientUDPPort);
			this.add(jtfClientUDPPort);
			this.add(jbConfirm);
			this.pack();
			this.setLocationRelativeTo(frame);
			
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				
			});
			
			jbConfirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String serverIP = jtfServerIP.getText().trim();
//						String clientIP = jtfClientIP.getText().trim();
						int clientUDPPort = Integer.parseInt(jtfClientUDPPort.getText().trim());
//						nc.udpPort = udpPort; //Server's TCP port & Client's own UDP port
						nc.connect(serverIP, TankServer.TCP_PORT, clientUDPPort); 
						//connect to Server by use Server's IP & TCP_Port + Client UDP port
					} catch(NumberFormatException e1) {
						jtfClientUDPPort.setText("6666");
						jtfClientUDPPort.requestFocus(true);
					}
					dispose();
				}
				
			});
			
			setVisible(true);
		}
		
	}
	
}
