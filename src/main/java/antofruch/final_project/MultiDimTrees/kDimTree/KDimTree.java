package antofruch.final_project.MultiDimTrees.kDimTree;

import antofruch.final_project.MultiDimTrees.KDAABB;
import antofruch.final_project.MultiDimTrees.IPositionable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A k-dimensional tree data structure for storing and querying multidimensional spatial data.
 * @param <T> T must implement Positionable
 */
public class KDimTree<T extends IPositionable> {

    /** The dimensionality of the tree */
    private final int dimension;

    static class Node<T extends IPositionable> {
        T data;

        int depth;

        Node<T> left;
        Node<T> right;

        public Node(T data, int depth) {
            this.data = data;
            this.depth = depth;
            this.left = null;
            this.right = null;
        }
    }

    Node<T> root;

    public KDimTree(int dimension){
        this.dimension = dimension;
        this.root = null;
    }

    public boolean insert(final T element){
        // Preventive programming
        if (element == null)
            throw new IllegalArgumentException("Element must not be null");
        if (element.getDimension() != dimension)
            throw new IllegalArgumentException("Element must be " + dimension + "-dimensional");

        // first node insertion
        if (root==null) {
            root = new Node<>(element, 0);
            return true;
        }

        // Insertion Logic
        Node<T> cur = root;
        while (true){
            if (element.getCoordinate(cur.depth % dimension) == cur.data.getCoordinate(cur.depth % dimension)){
              return false;
            } else if (element.getCoordinate(cur.depth % dimension) < cur.data.getCoordinate(cur.depth % dimension)) {
                if (cur.left == null) {
                    cur.left = new Node<>(element, cur.depth + 1);
                    return true;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Node<>(element, cur.depth + 1);
                    return true;
                }
                cur = cur.right;
            }
        }
    }

    public List<T> find(final KDAABB searchRegion){
        // Preventive Programming
        if (searchRegion == null)
            throw new IllegalArgumentException("Search region must not be null");
        if (searchRegion.getDimension() != dimension)
            throw new IllegalArgumentException("Search region must be " + dimension + "-dimensional");

        if (root == null)
            return new ArrayList<>();

        // Searching Logic
        List<T> results = new ArrayList<>();
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T> cur = queue.poll();

            // Leaf node
            if (cur == null) {
                continue;
            }

            // We add the data if it is contained in the search region
            if (searchRegion.contains(cur.data)) {
                results.add(cur.data);
            }

            // if search region in ENTIRELY left -> search only in left tree
            if (searchRegion.getMax().get(cur.depth % dimension) < cur.data.getCoordinate(cur.depth % dimension)) {
                if (cur.left != null){
                    queue.add(cur.left);
                }
            } else
            // if search region in ENTIRELY right -> search only in right tree
            if (searchRegion.getMin().get(cur.depth % dimension) > cur.data.getCoordinate(cur.depth % dimension)) {
                if (cur.right != null){
                    queue.add(cur.right);
                    continue;
                }
            } else
            // if it overlaps -> search in both
            {
                if (cur.left != null)
                    queue.add(cur.left);
                if (cur.right != null)
                    queue.add(cur.right);
            }
        }
        return results;
    }

}
