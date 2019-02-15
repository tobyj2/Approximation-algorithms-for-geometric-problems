import java.util.LinkedList;

public class WSPD {
	LinkedList<Pair> content;

	// recursive construction of WSPD from Octree following the algorithm in report
	public WSPD(OctreeNode T, OctreeNode u, OctreeNode v, double s) {
		// if u is deeper than v swap their roles
		if (u.level > v.level) {
			WSPD w = new WSPD(T, v, u, s);
			this.content = w.content;
		}
		// Deal with edge cases where WSPD is empty
		if (u.rep == null || v.rep == null || (u.children == null && u.p == null) || (v.children == null && v.p == null)
				|| (u.p != null && v.p != null && u.p.equals(v.p))) {
			this.content = new LinkedList<Pair>();
		} else {
			
			if (u.isWellSeperated(v, s)) {
				this.content = new LinkedList<Pair>();
				this.content.add(new Pair(u, v));
			} else {
				// recursive call deeper in the tree if u and v aren't well separated
				this.content = new LinkedList<Pair>();
				if (u.children != null) {
					for (OctreeNode child : u.children) {
						this.content.addAll(new WSPD(T, v, child, s).content);
					}
				}
			}
		}
	}

	// s-well-separated pair decomposition from Octree T
	public WSPD(Octree T, double s) {
		this.content = new WSPD(T.root, T.root, T.root, s).content;
	}

	
	// Function to display WSPD
	// Note: poor complexity, used for debugging with small Octrees
	@Override
	public String toString() {
		String result = new String();
		for (Pair p : content) {
			result = result + p.first.get_Points().toString() + " " + p.second.get_Points().toString() + "//";
		}
		return result;
	}	
	
}
