package antofruch.final_project.MultiDimTrees.QuadTree;

import antofruch.final_project.MultiDimTrees.KDAABB;
import antofruch.final_project.MultiDimTrees.IPositionable;

import java.util.*;

/**
 * A quadtree data structure for storing and querying two-dimensional spatial data.
 * @param <T> T must implement Positionable
 */
public class QuadTree<T extends IPositionable> {

    private final int maxSubdivisionDepth;
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
        private Node<T>[] children;


        public Node(final double x, final double y, final double width, final double height, final int subdivision_depth){
            this.boundary = new KDAABB(Arrays.asList(x, y), Arrays.asList(x + width, y + height));
            this.subdivisionDepth = subdivision_depth;
        }

        public boolean contains(IPositionable element) {
            return boundary.contains(element);
        }

        /**
         * Subdivides the box and puts the data in the right child
         * */
        public void subdivide(){
            // creation of child nodes
            children = new Node[4];
            double halfW = boundary.getWidth(0) / 2;
            double halfH = boundary.getWidth(1) / 2;
            double minX  = boundary.getMin().get(0);
            double minY  = boundary.getMin().get(1);

            children[0] = new Node<>(minX, minY, halfW, halfH, subdivisionDepth + 1);
            children[1] = new Node<>(minX + halfW, minY, halfW, halfH, subdivisionDepth + 1);
            children[2] = new Node<>(minX, minY + halfH, halfW, halfH, subdivisionDepth + 1);
            children[3] = new Node<>(minX + halfW, minY + halfH, halfW, halfH, subdivisionDepth + 1);

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

    public QuadTree(final double initial_width, final double initial_height, final int max_subdivision_depth){
        // FIXME: At the moment we assume the data coordinates will be positive
        root = new Node<>(0, 0, initial_width, initial_height, 0);
        maxSubdivisionDepth = max_subdivision_depth;
    }

    public List<T> find(final double minx, final double miny, final double maxX, final double maxY ){
        KDAABB region = new KDAABB(Arrays.asList(minx, miny), Arrays.asList(maxX, maxY));
        if (!region.intersects(root.boundary))
            throw new IllegalArgumentException("Query rectangle is outside the quadtree boundaries");

        List<T> results = new ArrayList<>();
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()){
            Node<T> cur = queue.poll();
            if (!cur.boundary.intersects(region)) {
                continue;
            }

            if (cur.data != null && region.contains(cur.data)){
                results.add(cur.data);
            }

            if (cur.subdivided) {
                queue.addAll(Arrays.asList(cur.children));
            }
        }
        return results;
    }

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
            if (cur.subdivided) {
                // node is subdivided, we go down in the right quadrant
                cur = cur.children[cur.calculateQuadrant(element)];
            } else {
                // The node is a leaf
                if (cur.data == null) {
                    // The leaf is empty, we place the element there
                    cur.data = element;
                    return true;
                }

                // The node already contains an element, we check the maximum depth before subdividing.
                if (cur.subdivisionDepth >= maxSubdivisionDepth) {
                    // FIXME: linked list when max subdivision is reached
                    throw new UnsupportedOperationException("Maximum subdivision depth reached, unable to insert element");
                }

                // we subdivide the node
                cur.subdivide();
            }
        }
    }
}