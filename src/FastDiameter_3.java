import Jcg.geometry.*;

/**
 * Implementation of a fast algorithm for computing an approximation of the diameter of a 3D point cloud,
 * based on WSP.
 *
 * @author Code by Luca Castelli Aleardi (INF421 2018, Ecole Polytechnique)
 *
 */
public class FastDiameter_3 implements Diameter_3 {
	
	/** approximation factor (for approximating the diameter) **/
	public double epsilon;
	
	// table to keep track of runtime, first element is time to create octree, second is WSPD from octree, third is time to find estimation of diameter from WSPD all in ms
	public long[] time;
	
	public FastDiameter_3(double epsilon) {
		this.time = new long[3]; 
	}
	
	/**
	 * Compute a farthest pair of points realizing an (1-epsilon)-approximation of the diameter of a set of points in 3D space
	 * 
	 * @param points the set of points
	 * @return a pair of farthest points
	 */
	
	
    public Point_3[] findFarthestPair(Point_3[] points) {
		if(points.length<2) throw new Error("Error: too few points");
		long t0 = System.currentTimeMillis();
		Point_3[] result = new Point_3[2];
		double diameter = 0;
		
		// Construct Octree from points
		Octree o = new Octree(points);
		long t1 = System.currentTimeMillis();
		time[0] = t1 - t0;
		
		//Construct WSPD from Octree
		WSPD w = new WSPD(o,epsilon/4);
		System.out.println(w.content.size());
		long t2 = System.currentTimeMillis();
		time[1] = t2 - t1;
		
		// calculate max of distances between representatives of all the pairs in WSPD
		for (Pair p: w.content) {
			Point_3 rep1 = p.first.rep;
			Point_3 rep2 = p.second.rep;
			double distance = (double)rep1.distanceFrom(rep2);
			if(distance>diameter) {
				result[0] = rep1;
				result[1] = rep2;
				diameter = distance;
			}
		}
		long t3 = System.currentTimeMillis();
		time[2] = t3 - t2;
		System.out.println("done, took: " + (t3-t0) + "ms" );
		return result;
    }

}