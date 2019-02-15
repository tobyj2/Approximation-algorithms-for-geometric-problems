import Jcg.geometry.Point_3;

public class Point_3_v2 extends Point_3 {

	public Point_3_v2() {
		super();
	}
	public Point_3_v2(Point_3 p) {
		super(p);
	}

	public void setCartesian(int i, Number x) {
		if (i == 0) {
			this.x = Double.valueOf(x.doubleValue());
		} else if (i == 1) {
			this.y = Double.valueOf(x.doubleValue());
		} else {
			this.z = Double.valueOf(x.doubleValue());
		}
	}
}
