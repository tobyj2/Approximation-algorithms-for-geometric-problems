
import java.util.*;

import Jcg.geometry.PointCloud_3;
import Jcg.geometry.Point_3;
//import Jcg.geometry.Vector_3;

/**
 * A class for representing an Octree
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */
public class Octree {
	public OctreeNode root;

	public Octree(Point_3[] points) {
		List<Point_3> linkList = new LinkedList<>(Arrays.asList(points));
		this.root = new OctreeNode(linkList);
	}

	public static void main(String args[]) {
		PointSet s = new PointSet("C:/Users/tobyi/eclipse-workspace/INF421/PI_data/pointclouds/horse.off");

		LinkedList<Integer> indices = new LinkedList<Integer>();
		for (int k = 0; k < s.size(); k++) {
			indices.add(k);
		}
		Collections.shuffle(indices);
		Integer[] index = indices.toArray(new Integer[s.size()]);
		
		long[][] closest_times = new long[30][3];
		int count = 0;
		
		for (int n = 1000; n < 20000; n *= 2) {
			Point_3[] l = new Point_3[n];
			for (int i = 0; i < n; i++) {
				l[i] = s.toArray()[index[i]];
			}
			
			FastClosestPair_3 f = new FastClosestPair_3();
			SlowClosestPair_3 f2 = new SlowClosestPair_3();
			//f.findClosestPair(l);
			//f2.findClosestPair(l);
			
			FastDiameter_3 fast = new FastDiameter_3(1 / 2);
			SlowDiameter_3 slow = new SlowDiameter_3();
			fast.findFarthestPair(l);
			slow.findFarthestPair(l);closest_times[count] = fast.time;
			System.out.println(n);
			
			count++;
		}
		System.out.println(Arrays.deepToString(closest_times));
	}

}
