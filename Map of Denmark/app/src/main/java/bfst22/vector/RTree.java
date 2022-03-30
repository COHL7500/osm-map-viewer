package bfst22.vector;

import java.util.*;

public class RTree {
	/*private final RNode root;

	public RTree(double[] min, double[] max, List<Drawable> elements){
		this.root = new RNode();
		this.root.setCoords(min);
		this.root.setCoords(max);
		this.root.elements = elements;
		this.generate();
	}

	public void rangeSearch(){

	}

	public void NNSearch(){

	}

	private void generate(){
		Queue<RNode> nodes = new LinkedList<>();
		nodes.add(this.root);

		int depth = 0;

		while(!nodes.isEmpty()){
			depth++;

			for(int i = 0; i < nodes.size(); i++){
				RNode node = nodes.remove();

				if(node.elements.size() > 1000) {
					nodes.add(node.left = new RNode());
					nodes.add(node.right = new RNode());

					for (Drawable element : node.elements) {
						double nodeCenter = node.min[depth % 2] + (node.max[depth % 2] - node.min[depth % 2]) / 2;
						double elementCenter = ((AABB) element).min[depth % 2] + (((AABB) element).max[depth % 2] - ((AABB) element).min[depth % 2]) / 2;

						if(((AABB) element).min[0] == Double.POSITIVE_INFINITY)
							System.out.println(((AABB) element));
						//System.out.println(((AABB) element).min[0] + " " + ((AABB) element).min[1] + ", " + ((AABB) element).max[0] + " " + ((AABB) element).max[1]);

						if(elementCenter < nodeCenter){
							node.left.elements.add(element);
							node.left.setCoords((AABB) element);
						} else {
							node.right.elements.add(element);
							node.right.setCoords((AABB) element);
						}
					}

					node.elements = null;
				}
			}
		}
	}

	private static class RNode extends AABB {
		public List<Drawable> elements = new ArrayList<>();
		public RNode left, right;
	}*/
}