import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import Jcg.geometry.PointCloud_3;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;


public class OctreeNode {
	public int level; //the level or distance from root of tree
	public OctreeNode[] children = null; // array containing the children of node
	public OctreeNode father; // the father of the node 
	public Point_3 p = null; // point stored in a leaf
	public Point_3[] corners; // two points delimiting the axis parallel rectangular box that contains all the points in the node
	// a point is in the box if all its coordinates are larger than those of corners[0] but smaller than those of corners[1]
	public Point_3 center = null; // the centre of the box (barycentre of corners)
	public Point_3 rep; // a random point chosen to be a representative of all those in u.
	private Point_3[][] new_corners; // table containing the points calculated to be the corners of the children of the node

	/**
	 * Create the octree containing a list of points
	 * first finds a box containing all the points then creates octree using the recursive function defined below
	 */
	public OctreeNode(List<Point_3> points) {
		PointCloud_3 cloud = new PointCloud_3_v2();
		for (Point_3 p : points) {
			cloud.add(p);
		}
		this.corners = cloud.boundingBox();
		OctreeNode O = new OctreeNode(points, 0, null, this.corners);
		this.level = 0;
		this.father = null;
		this.children = O.children;
		this.p = O.p;
		this.rep = O.rep;
		this.center = O.center;

	}

	
	// recursive function to construct Octree
	public OctreeNode(List<Point_3> points, int level, OctreeNode father, Point_3[] corners) {
		this.level = level;
		this.father = father;
		this.corners = corners;

		Point_3 barycentre = new Point_3();
		barycentre.barycenter(corners);
		this.center = barycentre;

		int len = points.size();
		if (len == 1) {
			this.p = points.remove(0);
			this.rep = this.p;
		}
		if (len > 1) {
			// choose a random Point to be the representative of node
			this.rep = points.get(0);
			
			// Find the corners of the 8 subnodes
			calculate_new_corners();
			
			// Determine which point goes in which subnode
			LinkedList<Point_3>[] fils = distribute_points(points);
			
			// recursively create each subnode with the correct points
			this.children = new OctreeNode[8];
			for (int i = 0; i < 8; i++) {
				this.children[i] = new OctreeNode(fils[i], this.level + 1, this, new_corners[i]);
			}
		}
	}
	

	/**
	 * Add a node into the OctreeNode
	 * The point must be in the bounding box of the node
	 */
	public void add(Point_3 p) {
		if (inBox(p, corners)) {
			if (this.p != null) {
				//When the OctreeNode is a leaf
				if (this.p.equals(p)) {
					throw new Error("Point already in Octree");
				}

				OctreeNode o = new OctreeNode(new ArrayList<Point_3>(Arrays.asList(p, this.p)), this.level, this.father,
						this.corners);
				this.p = null; 
				this.children = o.children;
				return;
			}

			if (this.children == null) {
				// when the OctreeNode is an empty box
				this.p = p;
				return;
			}
			
			// else add the point into the correct child of the OctreeNode
			int x = 0;
			for (int i = 0; i < 3; i++) {
				x += (p.compareCartesian(this.center, i) + 1) / 2 * Math.pow(2, i);
			}
			this.children[x].add(p);
		} else {
			throw new Error("Point not in box defined by node");
		}
	}
	
	
	// method determines the corners of the children of the node
	// assumes the data in corners and center is correct and fills in the table new_corners
	private void calculate_new_corners() {
		this.new_corners = new Point_3_v2[8][2];
		for (int i = 0; i < 8; i++) {
			new_corners[i][0] = new Point_3_v2(this.corners[0]);
			new_corners[i][1] = new Point_3_v2(this.center);

			// look at binary decomposition of i to find the subcube it corresponds to
			int n = i;
			for (int k = 0; k < 3; k++) {
				if (n % 2 == 1) {
					new_corners[i][0].setCartesian(k,this.center.getCartesian(k));
					new_corners[i][1].setCartesian(k,corners[1].getCartesian(k));
					assert new_corners[i][0].compareCartesian(new_corners[i][1], 0)<1;
					assert new_corners[i][0].compareCartesian(new_corners[i][1], 1)<1;
					assert new_corners[i][0].compareCartesian(new_corners[i][1], 2)<1;
				}
				n = n / 2;
			}
		}
		
	}

	
	// method takes in the list of points in the node and splits them into the 8 lists corresonding to the 8 children of the node
	LinkedList<Point_3>[] distribute_points(List<Point_3> points) {
		LinkedList<Point_3>[] fils = new LinkedList[8];
		for (int j = 0; j < 8; j++) {
			fils[j] = new LinkedList<Point_3>();
		}
		for (Point_3 p : points) {
			int x = 0;
			for (int i = 0; i < 3; i++) {
				x += (p.compareCartesian(this.center, i) + 1) / 2 * Math.pow(2, i);
			}
			fils[x].add(p);
		}
		return fils;
	}

	// function determines if the points in this and v are s-well-separated
	// Note: this function does not use the smallest possible covering ball unless the node is a leaf. It instead uses a covering ball derived from the bounding box
	public boolean isWellSeperated(OctreeNode v, double s) {
		double r = Math.max(this.radius(), v.radius());
		return ((double) (this.isLeaf() ? this.p : this.center).distanceFrom(v.isLeaf() ? v.p : v.center) - 2 * r >= s
				* r);
	}

	// determines if node is a leaf
	public boolean isLeaf() {
		return (this.p != null);
	}

	
	// determines if a point is in the box defined by 2 points
	public static boolean inBox(Point_3 p, Point_3[] box) {
		boolean b = true;
		for (int i = 0; i < 3; i++) {
			b = b && p.compareCartesian(box[0], i) > -1 && p.compareCartesian(box[1], i) < 1;
		}
		return b;
	}

	// returns a list of all the points in Node (and subnodes)
	public LinkedList<Point_3> get_Points() {
		LinkedList<Point_3> result = new LinkedList<Point_3>();
		if (this.p != null) {
			result.add(this.p);

		} else {
			if (this.children != null) {
				for (OctreeNode o : this.children) {
					result.addAll(o.get_Points());
				}
			}
		}
		return result;
	}

	
	
	// returns upperbound of radius of ball covering all the points in a node
	public double radius() {
		return this.isLeaf() ? 0 : (double) this.corners[0].distanceFrom(this.center);
	}

	// Determines if 2 nodes are the same. The nodes are assumed to be from the same tree (as used in the WSPD decomposition) and as such the corners are enough to determine if the nodes are the same
	public boolean equals(OctreeNode v) {
		return this.corners.equals(v.corners);
	}
}
