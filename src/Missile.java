import java.awt.*;
import java.util.List;

class Missile {
	
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	
	int x, y;
	int tankID;
	private static final int SPEED = Tank.getSpeed() * 2;

	private boolean live = true;
	boolean good;
	
	Direction dir = null;
	TankClient tc = null;
	
	public Missile(int tankID, int x, int y, Direction dir) {
		this.tankID = tankID;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public Missile(int tankID, int x, int y, boolean good, Direction dir, TankClient tc) {
		// TODO Auto-generated constructor stub
		this(tankID, x, y, dir);
		this.good = good;
		this.tc = tc;
	}

	public void draw(Graphics g) {
		
		if(!live) {
			tc.missiles.remove(this);
			return;
		}
		
		Color c = g.getColor();
		
		if(good) {
			g.setColor(new Color(255, 200, 255));//Pink
		} else {
			g.setColor(Color.GREEN);
		}
		
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		move();
	}

	private void move() {
		// TODO Auto-generated method stub
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
		
		if(x <= 0 || y <= 0 || x >= tc.GAME_WIDTH || y >= tc.GAME_HEIGHT) {
			this.setLive(false);
		}

	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public boolean hitTank(Tank t) {
		if(this.getRect().intersects(t.getRect()) && t.isLive() && t.isGood() != this.isGood()) {
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
			this.setLive(false);
			t.setLive(false);
			return true;
		}
		return false;
	}
	
	public boolean hitTanks(List<Tank> tanks) {
		for(int i = 0; i < tanks.size(); i++) {
			if(hitTank(tanks.get(i))) {
				return true;
			}
		}
		return false;
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
