import java.util.Arrays;

import Jcg.geometry.Point_3;

public class PI {

	public PI() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		if(args.length!=2) {
			System.out.println("Wrong number of input parameters: required one file .off and an approximation factor for diameter calculation");
			System.exit(0);
		}
		PointSet s = new PointSet(args[0]);
		Point_3[] l= s.toArray();
		FastClosestPair_3 f = new FastClosestPair_3();
		System.out.println(Arrays.toString(f.findClosestPair(l)));
		FastDiameter_3 fast = new FastDiameter_3(Double.parseDouble(args[1]));
		System.out.println(Arrays.toString(fast.findFarthestPair(l)));
	}

}
