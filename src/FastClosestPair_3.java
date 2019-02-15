import Jcg.geometry.*;

/**
 * Implementation of a fast algorithm for computing the closest pair,
 * based on WSP.
 *
 * @author Code by Luca Castelli Aleardi (INF421 2018, Ecole Polytechnique)
 *
 */
public class FastClosestPair_3 implements ClosestPair_3 {
	long[] time;
	FastClosestPair_3(){
		this.time = new long[3]; // table to keep track of runtime, first element is time to create octree, second is WSPD from octree, third is time to find closest pair from WSPD all in ms
	}
	
	
	/**
	 * Compute the closest pair of a set of points in 3D space
	 * 
	 * @param points the set of points
	 * @return a pair of closest points
	 */
    public Point_3[] findClosestPair(Point_3[] points) {
		if(points.length<2) throw new Error("Error: too few points");
		
		Point_3[] result = new Point_3[2];
		double min_distance = Double.MAX_VALUE;
		
		//construct Octree from points
		long t0 = System.currentTimeMillis();
		Octree o = new Octree(points);

		
		long t1 = System.currentTimeMillis();
		this.time[0] = t1-t0;
		
		//Construct WSPD from Octree
		WSPD w = new WSPD(o,0.5);
		long t2 = System.currentTimeMillis();
		this.time[1] = t2- t1;
		
		// go through pairs in WSPD and find smallest distance where both elements of pair are leaves
		for(Pair pair: w.content) {
			if (pair.first.isLeaf() && pair.second.isLeaf() && (double)pair.first.p.distanceFrom(pair.second.p)<min_distance) {
				result[0] = pair.first.p;
				result[1] = pair.second.p;
				min_distance = (double)pair.first.p.distanceFrom(pair.second.p);
			}
		}
		long t3 = System.currentTimeMillis();
		this.time[2] = t3-t2;
		System.out.println("done, took: " + (t3-t0) + "ms" );
		return result;
    }

}