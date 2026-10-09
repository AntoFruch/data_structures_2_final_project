package antofruch.final_project.QuadTree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class QuadTree<T extends Positionable> {

    private final int MAX_SUBDIVISION_DEPTH;
    static class Node<T extends Positionable> {
        /** stored data */
        private T data;

        /** boundary of the node */
        private final AABB boundary;

        /** true: the node is subdivided into 4 other nodes */
        private boolean subdivided = false;

        /** depth of the node in the tree */
        private final int subdivision_depth;

        /** north-west child node */
        private Node<T>[] children;


        public Node(final double x, final double y, final double width, final double height, final int subdivision_depth){
            this.boundary = new AABB(x, y, width, height);
            this.subdivision_depth = subdivision_depth;
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
            children[0] = new Node<>(boundary.x, boundary.y, boundary.width / 2, boundary.height / 2, subdivision_depth+1);
            children[1] = new Node<>(boundary.x + boundary.width / 2, boundary.y, boundary.width / 2, boundary.height / 2, subdivision_depth+1);
            children[2] = new Node<>(boundary.x, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2, subdivision_depth+1);
            children[3] = new Node<>(boundary.x + boundary.width / 2, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2, subdivision_depth+1);

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
        MAX_SUBDIVISION_DEPTH = max_subdivision_depth;
    }

    public T find(final T element){
        // if the element is not in the quadtree boundaries, we throw an exception
        if (!root.contains(element))
            throw new IllegalArgumentException("Element is outside the quadtree boundaries");

        // else we search for the element
        Node<T> cur = root;
        while (cur.subdivided){
            cur = cur.children[cur.calculateQuadrant(element)];
        }

        // in the final box, either the box is empty, return null, either data is here so we return it
        if (cur.data != null)
            return cur.data;

        return null;
    }

    public boolean insert(final T element){
        // if the element is not in the quadtree boundaries, we throw an exception
        if (!root.contains(element))
            throw new IllegalArgumentException("Element is outside the quadtree boundaries");

        Node<T> cur = root;
        while (cur.subdivided){
            cur = cur.children[cur.calculateQuadrant(element)];
        }

        // if the current node is empty, we can insert the element here
        if (cur.data == null){
            cur.data = element;
            return true;
        }

        // if not we have to subdivide until we can
        while (cur.data != null){
            if (cur.subdivision_depth >= MAX_SUBDIVISION_DEPTH)
                throw new UnsupportedOperationException("Maximum subdivision depth reached, unable to insert element");
            System.out.println("subdivision done");
            cur.subdivide();
            cur = cur.children[cur.calculateQuadrant(element)];
        }
        cur.data = element;

        return true;
    }
}