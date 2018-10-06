import java.awt.*;

class GunBarrel {


	int x0, y0, x1, y1;
	Tank t = null;
	Direction dir = null;

	public GunBarrel(int x, int y, Tank t) {
		x0 = x;
		y0 = y;
		this.t = t;
//System.out.println("gun barrel dir " + dir);
		dir = Direction.D;
	}
	
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
//System.out.println("Draw");
		if(t.dir != Direction.STOP) {
			dir = t.dir;
		}
		direction();
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.drawLine(x0, y0, x1, y1);
		g.setColor(c);
	}
	
	public void draw(int x, int y, Graphics g) {
		x0 = x;
		y0 = y;
		this.draw(g);
	}
	
	private void direction() {
		switch(dir) {
		case U :
			x1 = x0;
			y1 = y0 - Tank.getHeight() / 2;
//System.out.println("gun barrel dir " + dir);
			break;
		case UL :
			x1 = x0 - Tank.getWidth() / 2;
			y1 = y0 - Tank.getHeight() / 2;
			break;
		case UR :
			x1 = x0 + Tank.getWidth() / 2;
			y1 = y0 - Tank.getHeight() / 2;
			break;
		case D :
			x1 = x0;
			y1 = y0 + Tank.getHeight() / 2;
			break;
		case DL :
			x1 = x0 - Tank.getWidth() / 2;
			y1 = y0 + Tank.getHeight() / 2;
			break;
		case DR :
			x1 = x0 + Tank.getWidth() / 2;
			y1 = y0 + Tank.getHeight() / 2;
			break;
		case L :
			x1 = x0 - Tank.getWidth() / 2;
			y1 = y0;
			break;
		case R :
			x1 = x0 + Tank.getWidth() / 2;
			y1 = y0;
			break;
		case STOP ://Tank stop, gun barrel default Down
			dir = Direction.D;
			break;
		}
	}

	
}
