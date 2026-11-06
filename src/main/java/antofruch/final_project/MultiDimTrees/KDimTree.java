package antofruch.final_project.MultiDimTrees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A k-dimensional tree data structure for storing and querying multidimensional spatial data.
 * @param <T> T must implement Positionable
 */
public class KDimTree<T> implements MultiDimTree<T> {

    /** The dimensionality of the tree */
    private final int dimension;

    private final IComparer<T> comparer;

    /**
     * Internal Node class
     * @param <T>
     */
    static class Node<T> {
        /** Data store by the node */
        T data;

        /** The depth of the node in the tree */
        int depth;

        /** left child node */
        Node<T> left;
        /** right child node */
        Node<T> right;

        /**
         * Constructor of the inner class Node
         * @param data - data to store
         * @param depth - depth of the node
         */
        public Node(T data, int depth) {
            this.data = data;
            this.depth = depth;
            this.left = null;
            this.right = null;
        }
    }

    /** The root node of the K-Dim Tree */
    Node<T> root;

    /**
     * Constructor of KDimTree
     * @param dimension - the number of dimensions of the data contained in the tree
     * @param comparer - the comparer to use for comparing elements
     */
    public KDimTree(final int dimension, final IComparer<T> comparer){
        this.dimension = dimension;
        this.comparer = comparer;
        this.root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear(){
        root = null;
    }

    /**
     * Insert the element in the tree
     * @param element - element to insert
     * @return - true if inserted successfully, false if not
     */
    @Override
    public boolean insert(final T element){
        // Preventive programming
        if (element == null)
            throw new IllegalArgumentException("Element must not be null");

        // first node insertion
        if (root==null) {
            root = new Node<>(element, 0);
            return true;
        }

        // Insertion Logic
        Node<T> cur = root;
        while (true){
            if (comparer.compare(element, cur.data, cur.depth % dimension) <= 0) {
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

    /**
     * Find all the elements contained in the query region.
     * @param min - the minimum point of the query region
     * @param max - the maximum point of the query region
     * @return list of elements contained in the search region
     */
    @Override
    public List<T> find(final T min, final T max){
        // Preventive Programming
        if (min == null || max == null)
            throw new IllegalArgumentException("Min and max must not be null");

        KDAABB<T> searchRegion = new KDAABB<>(min, max, dimension, comparer);

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
            if (comparer.compare(searchRegion.getMax(), cur.data, cur.depth % dimension) < 0) {
                if (cur.left != null){
                    queue.add(cur.left);
                }
            } else
            // if search region in ENTIRELY right -> search only in right tree
            if (comparer.compare(searchRegion.getMin(), cur.data, cur.depth % dimension) > 0) {
                if (cur.right != null){
                    queue.add(cur.right);
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
