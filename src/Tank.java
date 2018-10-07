import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class Tank {	 
	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;
	private static final int SPEED = 10;
	private final int AI_MOVE_LEVEL = 2;//1~9
	private final int AI_FIRE_LEVEL = 1;
	
	int x, y;
	TankClient tc = null;
	GunBarrel gb = null;
	
	private boolean live = true;
	private boolean good; 
	boolean bU = false, bD = false, bL = false, bR = false;
	
	Direction dir = Direction.STOP;
	public int ID;
	
	private static Random r = new Random();
	private static Direction dirs[] = Direction.values();
	
	public Tank(int x, int y, boolean good, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
		this.good = good;
		gb = new GunBarrel(x + WIDTH / 2, y + HEIGHT / 2, this);
//		tc.addKeyListener(new KeyMonitor());
//System.out.println("gun barrel" + tc.gb);
		
	}

	public void draw(Graphics g) {
		if(!live) {
			if(!good) {
				tc.tanks.remove(this);
			}
			return;
		}
		Color c = g.getColor();
		if(good) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLUE);
		}
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.drawString("" + ID, x, y-10);
		g.setColor(c);
		if(gb != null) gb.draw(x + WIDTH / 2, y + HEIGHT / 2, g);
		AIDirection();
		AIFire();
		move();
	}

	public void AIFire() {
		if(!good && r.nextInt(10) < AI_FIRE_LEVEL) {
			tc.missiles.add(fire());
		}
	}
	
	public Missile fire() {
		int x = this.x + WIDTH / 2 - Missile.getWidth() / 2;
		int y = this.y + HEIGHT / 2 - Missile.getHeight() / 2;
		Missile m = new Missile(ID, x, y, good, gb.dir, tc); 
		tc.nc.send(new MissileNewMsg(m));
		return m;
	}
	
	public void AIDirection() {
		if(!good && r.nextInt(10) < AI_MOVE_LEVEL) {
			dir = dirs[r.nextInt(dirs.length)];
		}
	}
	
	public void direction() {
		
		Direction oldDir = dir;
		
		if(bU && !bD && !bL && !bR) dir = Direction.U;
		else if(bU && !bD && bL && !bR) dir = Direction.UL;
		else if(bU && !bD && !bL && bR) dir = Direction.UR;
		else if(!bU && bD && !bL && !bR) dir = Direction.D;
		else if(!bU && bD && bL && !bR) dir = Direction.DL;
		else if(!bU && bD && !bL && bR) dir = Direction.DR;
		else if(!bU && !bD && bL && !bR) dir = Direction.L;
		else if(!bU && !bD && !bL && bR) dir = Direction.R;
		else if(!bU && !bD && !bL && !bR) dir = Direction.STOP;
		
		if(oldDir != dir) { //only send moveMsg when direction change
			tc.nc.send(new TankMoveMsg(ID, x, y, dir)); 
		}
	}
	
	public void move() {
//System.out.println(dir);
		switch(dir) {
		case U :
			y -= SPEED;
			break;
		case UL :
			x -= SPEED;
			y -= SPEED;
			break;
		case UR :
			x += SPEED;
			y -= SPEED;
			break;
		case D :
			y += SPEED;
			break;
		case DL :
			x -= SPEED;
			y += SPEED;
			break;
		case DR :
			x += SPEED;
			y += SPEED;
			break;
		case L :
			x -= SPEED;
			break;
		case R :
			x += SPEED;
			break;
		case STOP :
			break;
		}
		
		if(x < 0) x = 0;
		if(y < 30) y = 30;
		if(x > tc.GAME_WIDTH - WIDTH) x = tc.GAME_WIDTH - WIDTH;
		if(y > tc.GAME_HEIGHT - HEIGHT) y = tc.GAME_HEIGHT - HEIGHT;
		
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_UP :
			bU = true;
			break;
		case KeyEvent.VK_DOWN :
			bD = true;
			break;
		case KeyEvent.VK_LEFT :
			bL = true;
			break;
		case KeyEvent.VK_RIGHT :
			bR = true;
			break;
		}
		direction();
		
	}
	
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_CONTROL :
//			tc.m = fire();
			if(live) {
				tc.missiles.add(fire());
			}
			break;
		case KeyEvent.VK_UP :
			bU = false;
			break;
		case KeyEvent.VK_DOWN :
			bD = false;
			break;
		case KeyEvent.VK_LEFT :
			bL = false;
			break;
		case KeyEvent.VK_RIGHT :
			bR = false;
			break;
		}
		direction();
		
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public static int getSpeed() {
		return SPEED;
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isGood() {
		return good;
	}

}
