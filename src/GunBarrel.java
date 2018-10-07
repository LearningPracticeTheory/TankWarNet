import java.awt.*;
/**
 * Tank 炮管 
 */
class GunBarrel {


	int x0, y0, x1, y1;
	Tank t = null;
	Direction dir = null;
	
	public GunBarrel(int x, int y, Tank t) {
		x0 = x;
		y0 = y;
		this.t = t;
		dir = Direction.D;
	}
	
	/**
	 * 使用直线模拟 Tank 炮管
	 * @param g 画笔
	 * @see java.awt.Graphics
	 */
	public void draw(Graphics g) {
		if(t.dir != Direction.STOP) {
			dir = t.dir;
		}
		direction();
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.drawLine(x0, y0, x1, y1);
		g.setColor(c);
	}
	
	/**
	 * 实时更新炮筒中心点位置并画炮筒
	 * @param x x 坐标
	 * @param y y 坐标
	 * @param g 画笔
	 * @see java.awt.Graphics
	 * @see draw(Graphics g)
	 */
	public void draw(int x, int y, Graphics g) {
		x0 = x;
		y0 = y;
		this.draw(g);
	}
	
	/**
	 * 根据 dir 方向确定炮口位置
	 */
	private void direction() {
		switch(dir) {
		case U :
			x1 = x0;
			y1 = y0 - Tank.getHeight() / 2;
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
		case STOP : //Tank stop, gun barrel default Down
			dir = Direction.D;
			break;
		}
	}

}
