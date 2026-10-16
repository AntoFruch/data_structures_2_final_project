package antofruch.final_project.MultiDimTrees;

import antofruch.final_project.MultiDimTrees.utils.IPositionable;
import antofruch.final_project.MultiDimTrees.utils.KDAABB;

import java.util.*;

/**
 * A quadtree data structure for storing and querying two-dimensional spatial data.
 * @param <T> T must implement Positionable
 */
public class QuadTree<T extends IPositionable> implements MultiDimTree<T> {

    /** max depth of subdivision*/
    private final int maxSubdivisionDepth;

    /**
     * Internal Node class
     * @param <T>
     */
    static class Node<T extends IPositionable> {
        /** stored data */
        private T data;

        /** boundary of the node */
        private final KDAABB boundary;

        /** true: the node is subdivided into 4 other nodes */
        private boolean subdivided = false;

        /** depth of the node in the tree */
        private final int subdivisionDepth;

        /** north-west child node */
        private final Node<T>[] children = new Node[4];


        public Node(final double minX, final double minY, final double maxX, final double maxY, final int subdivision_depth){
            this.boundary = new KDAABB(Arrays.asList(minX, minY), Arrays.asList(maxX, maxY));
            this.subdivisionDepth = subdivision_depth;
        }

        public Node(final Node node){
            this(
                node.boundary.getMin().get(0),
                node.boundary.getMin().get(1),
                node.boundary.getMax().get(0),
                node.boundary.getMax().get(1),
                node.subdivisionDepth
            );
        }

        public boolean contains(IPositionable element) {
            return boundary.contains(element);
        }

        /**
         * Subdivides the box and puts the data in the right child
         * */
        public void subdivide(){
            // creation of child nodes
            double halfW = boundary.getWidth(0) / 2;
            double halfH = boundary.getWidth(1) / 2;
            double minX  = boundary.getMin().get(0);
            double minY  = boundary.getMin().get(1);

            children[0] = new Node<>(minX, minY, minX + halfW, minY + halfH, subdivisionDepth + 1);
            children[1] = new Node<>(minX + halfW, minY, minX + halfW * 2, minY + halfH, subdivisionDepth + 1);
            children[2] = new Node<>(minX, minY + halfH, minX + halfW, minY + halfH * 2, subdivisionDepth + 1);
            children[3] = new Node<>(minX + halfW, minY + halfH, minX + halfW * 2, minY + halfH * 2, subdivisionDepth + 1);

            // move the data in adequate child
            children[calculateQuadrant(data)].data = data;
            data = null;

            // set the subdivision state to true
            subdivided = true;
        }

        private int calculateQuadrant(T element){
            // each condition on x and y turn a bit to 0 or 1 making the final index (0b00, 0b01, 0b10, 0b11)
            int bitX = (element.getCoordinate(0) >= boundary.getMin().get(0) + boundary.getWidth(0) / 2) ? 1 : 0;
            int bitY = (element.getCoordinate(1) >= boundary.getMin().get(1) + boundary.getWidth(1) / 2) ? 1 : 0;
            return bitX | (bitY << 1);
        }

    }

    /** the root node of the quadtree */
    Node<T> root;

    /**
     * Constructor for the QuadTree
     * @param minX : min x bound of the bounding box
     * @param minY : min y bound of the bounding box
     * @param maxX : max x bound of the bounding box
     * @param maxY : max y bound of the bounding box
     * @param max_subdivision_depth : maximum depth of subdivision
     */
    public QuadTree(final double minX, final double minY, final double maxX, final double maxY, final int max_subdivision_depth){
        root = new Node<>(minX, minY, maxX, maxY, 0);
        maxSubdivisionDepth = max_subdivision_depth;
    }

    /**
     * Find all elements within a given rectangle
     * @param searchRegion - region where to search
     * @return List of elements within the query rectangle
     */
    @Override
    public List<T> find(KDAABB searchRegion){
        // Preventive programming
        if (searchRegion.getDimension() > 2)
            throw new IllegalArgumentException("Search region must be 2-dimensional");
        if (!searchRegion.intersects(root.boundary))
            throw new IllegalArgumentException("Query rectangle is outside the quadtree boundaries");

        List<T> results = new ArrayList<>();
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()){
            Node<T> cur = queue.poll();
            if (!cur.boundary.intersects(searchRegion)) {
                continue;
            }

            if (cur.data != null && searchRegion.contains(cur.data)){
                results.add(cur.data);
            }

            if (cur.subdivided) {
                queue.addAll(Arrays.asList(cur.children));
            } else if (cur.children[0] != null) { // the max subdivision case with linked list at the end
                queue.add(cur.children[0]);
            }
        }
        return results;
    }

    /**
     * Insert an element into the quadtree
     * @param element : the element to insert
     * @return true if the element was inserted, false otherwise
     */
    @Override
    public boolean insert(final T element){
        // Preventive programming
        if (element == null)
            throw new IllegalArgumentException("Element must not be null");
        if (element.getDimension() != 2)
            throw new IllegalArgumentException("Element must be 2-dimensional");

        // if the element is not in the quadtree boundaries, we throw an exception
        if (!root.contains(element))
            throw new IllegalArgumentException("Element is outside the quadtree boundaries");

        Node<T> cur = root;
        while (true) {
            if (cur.subdivided) { // node is subdivided, we go down in the right quadrant
                cur = cur.children[cur.calculateQuadrant(element)];

            } else { // The node is a leaf
                if (cur.data == null) {
                    // The leaf is empty, we place the element there
                    cur.data = element;
                    return true;
                }

                // The node already contains an element, we check the maximum depth before subdividing.
                if (cur.subdivisionDepth >= maxSubdivisionDepth) {
                    cur.children[0] = new Node<>(cur);
                    cur.children[0].data = element;
                    return true;
                }

                // we subdivide the node
                cur.subdivide();
            }
        }
    }
}