import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class TankClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public final int GAME_WIDTH = 800;
	public final int GAME_HEIGHT = 600;
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
//		setSize(800, 600);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setTitle("TankWar");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
		setVisible(true);
		/*
		for(int i = 0; i < TANK_FIRST_NUM; i++) {
			tanks.add(new Tank(50 + (i + 1) * 40, 50, false, this));
		}
		*/
		new Thread(new PaintThread()).start();
		addKeyListener(new KeyMonitor());
		
		nc.connect("localhost", TankServer.TCP_PORT);
		
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
			m.draw(g);
			/*
			if(m.hitTank(enemyTank) && enemyTank.isLive()) {
				m.setLive(false);
				enemyTank.setLive(false);
			}
			*/
			m.hitTanks(tanks);
			m.hitTank(myTank);
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
	
}
