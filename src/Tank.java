import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

class Tank {	 
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	private static final int SPEED = 10;
	
	int x, y;
	TankClient tc = null;
	GunBarrel gb = null;
	
	private boolean live = true;
	private boolean good; 
	boolean bU = false, bD = false, bL = false, bR = false;
	
	Direction dir = Direction.STOP;
	public int ID;
	
	public Tank(int x, int y, boolean good, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
		this.setGood(good);
		gb = new GunBarrel(x + WIDTH / 2, y + HEIGHT / 2, this);
	}
	
	public void draw(Graphics g) {
		if(!live) { //No need compare which tank is good or bad, remove directly if !live
			tc.tanks.remove(this); //synchronized at each Clients
			return;
		}
		Color c = g.getColor();
		if(isGood()) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLUE);
		}
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.drawString("" + ID, x, y-10);
		g.setColor(c);
		if(gb != null) {
			gb.draw(x + WIDTH / 2, y + HEIGHT / 2, g);
		}
		move();
	}

	public Missile fire() {
		int x = this.x + WIDTH / 2 - Missile.getWidth() / 2;
		int y = this.y + HEIGHT / 2 - Missile.getHeight() / 2;
		Missile m = new Missile(ID, x, y, isGood(), gb.dir, tc);
		tc.nc.send(new MissileNewMsg(m));
		return m;
	}
	
	public void direction() {
		
		if(!live) {
			return;
		}
		
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
			tc.nc.send(new TankMoveMsg(ID, x, y, dir, gb.dir)); 
		}
	}
	
	public void move() {
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
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_F5:
			if(!live) {
				live = true;
				tc.nc.send(new TankRebornMsg(this));
			}
			break;
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
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_CONTROL :
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

	public void setGood(boolean good) {
		this.good = good;
	}

}
