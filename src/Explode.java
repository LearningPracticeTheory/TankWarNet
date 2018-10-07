import java.awt.*;

class Explode {
	
	int x, y;
	TankClient tc;

	int diameters[] = {4, 7, 12, 20, 30, 49, 40, 20, 10, 6};
	int step = 0;
	
	public Explode(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	/**
	 * 用不同直径的圆代表爆炸
	 * @param g 画笔
	 * @see java.awt.Graphics
	 */
	public void draw(Graphics g) {
		if(step == diameters.length) {
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		for(int i = 0; i < diameters.length; i++) {
			g.fillOval(x, y, diameters[step], diameters[step]);
		}
		step ++;
		g.setColor(c);
	}
	
}
