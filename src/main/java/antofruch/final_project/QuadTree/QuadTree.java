package antofruch.final_project.QuadTree;

import java.util.*;

public class QuadTree<T extends Positionable> {

    private final int maxSubdivisionDepth;
    static class Node<T extends Positionable> {
        /** stored data */
        private T data;

        /** boundary of the node */
        private final AABB boundary;

        /** true: the node is subdivided into 4 other nodes */
        private boolean subdivided = false;

        /** depth of the node in the tree */
        private final int subdivisionDepth;

        /** north-west child node */
        private Node<T>[] children;


        public Node(final double x, final double y, final double width, final double height, final int subdivision_depth){
            this.boundary = new AABB(x, y, width, height);
            this.subdivisionDepth = subdivision_depth;
        }

        public boolean contains(T element) {
            return boundary.contains(element.getX(), element.getY());
        }

        /**
         * Subdivides the box and puts the data in the right child
         * */
        public void subdivide(){
            // creation of child nodes
            children = new Node[4];
            children[0] = new Node<>(boundary.x, boundary.y, boundary.width / 2, boundary.height / 2, subdivisionDepth +1);
            children[1] = new Node<>(boundary.x + boundary.width / 2, boundary.y, boundary.width / 2, boundary.height / 2, subdivisionDepth +1);
            children[2] = new Node<>(boundary.x, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2, subdivisionDepth +1);
            children[3] = new Node<>(boundary.x + boundary.width / 2, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2, subdivisionDepth +1);

            // move the data in adequate child
            children[calculateQuadrant(data)].data = data;
            data = null;

            // set the subdivision state to true
            subdivided = true;
        }

        private int calculateQuadrant(T element){
            // each condition on x and y turn a bit to 0 or 1 making the final index (0b00, 0b01, 0b10, 0b11)
            int bitX = (element.getX() >= boundary.x + boundary.width / 2) ? 1 : 0;
            int bitY = (element.getY() >= boundary.y + boundary.height / 2) ? 1 : 0;
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

    public List<T> find(final double x, final double y, final double width, final double height ){
        AABB region = new AABB(x, y, width, height);
        if (region.intersects(root.boundary))
            throw new IllegalArgumentException("Query rectangle is outside the quadtree boundaries");

        List<T> results = new ArrayList<>();
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()){
            Node<T> cur = queue.poll();
            if (cur.boundary.intersects(region)) {
                continue;
            }

            if (cur.data != null && region.contains(cur.data.getX(), cur.data.getY())){
                results.add(cur.data);
            }

            if (cur.subdivided) {
                queue.addAll(Arrays.asList(cur.children));
            }
        }
        return results;
    }

    public boolean insert(final T element){
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