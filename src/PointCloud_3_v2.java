import Jcg.geometry.*;

public class PointCloud_3_v2 extends PointCloud_3 {
	public PointCloud_3_v2() {
		super();
	}
	
	
	public Point_3 max(int d)
	  {
	    if (size() == 0) {
	      return null;
	    }
	    Point_3 maxPoint = null;
	    double maxCoordinate = - Double.MAX_VALUE;
	    for (Point_3 p : this.points) {
	      if (p.getCartesian(d).doubleValue() > maxCoordinate)
	      {
	        maxPoint = p;
	        maxCoordinate = p.getCartesian(d).doubleValue();
	      }
	    }
	    return maxPoint;
	  }
	
	
	  public Point_3[] boundingBox()
	  {
	    if (size() == 0) {
	      return null;
	    }
	    Point_3[] result = new Point_3[2];
	    result[0] = new Point_3(min(0).getX(), min(1).getY(), min(2).getZ());
	    result[1] = new Point_3(max(0).getX(), max(1).getY(), max(2).getZ());
	    return result;
	  }
	
}
