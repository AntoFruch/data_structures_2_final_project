package antofruch.final_project.MultiDimTrees.MyTrees;

import java.util.*;

/**
 * A quadtree data structure for storing and querying two-dimensional spatial data.
 * @param <T> T must implement Positionable
 */
public class QuadTreeKdTreeMix<T> implements MultiDimTree<T> {

    /** max depth of subdivision*/
    private final int maxSubdivisionDepth;

    private final IComparer<T> comparer;
    private final IMidPointFinder<T> midPointFinder;

    /**
     * Internal Node class
     * @param <T>
     */
    static class Node<T> {
        /** stored data */
        private KDimTree<T> data;
        private final IComparer<T> comparer;
        private final IMidPointFinder<T> midPointFinder;

        /** boundary of the node */
        private final KDAABB<T> boundary;

        /** true: the node is subdivided into 4 other nodes */
        private boolean subdivided = false;

        /** depth of the node in the tree */
        private final int subdivisionDepth;

        /** north-west child node */
        private final Node<T>[] children;


        public Node(final T min, final T max, final int subdivision_depth, IComparer<T> comparer, IMidPointFinder<T> midPointFinder){
            data = new KDimTree<T>(2, comparer);
            children = new Node[4];
            this.comparer = comparer;
            this.boundary = new KDAABB<T>(min, max, 2, comparer);
            this.subdivisionDepth = subdivision_depth;
            this.midPointFinder = midPointFinder;
        }

        public Node(final Node<T> node){
            this(
                node.boundary.getMin(),
                node.boundary.getMax(),
                node.subdivisionDepth,
                node.comparer,
                node.midPointFinder
            );
        }

        public boolean contains(T element) {
            return boundary.contains(element);
        }

        /**
         * Subdivides the box and puts the data in the right child
         * */
        public void subdivide(){
            // creation of child nodes
            T min = boundary.getMin();
            T max = boundary.getMax();

            T midX = midPointFinder.midpoint(min, max, 0);
            T midY = midPointFinder.midpoint(min, max, 1);

            children[0] = new Node<>(
                    min,
                    midPointFinder.midpoint(midX, max, 1),
                    subdivisionDepth + 1, comparer, midPointFinder);
            children[1] = new Node<>(
                    midX,
                    midPointFinder.midpoint(max, midX, 1),
                    subdivisionDepth + 1, comparer, midPointFinder);
            children[2] = new Node<>(
                    midY,
                    midPointFinder.midpoint(max, midY, 0),
                    subdivisionDepth + 1, comparer, midPointFinder);
            children[3] = new Node<>(
                    midPointFinder.midpoint(midX, max, 1),
                    max,
                    subdivisionDepth + 1, comparer, midPointFinder);

            // move the data in adequate child
            for (T element : data.find(min, max)) {
                children[calculateQuadrant(element)].data.insert(element);
            }
            data.clear();

            // set the subdivision state to true
            subdivided = true;
        }

        private int calculateQuadrant(T element){
            // each condition on x and y turn a bit to 0 or 1 making the final index (0b00, 0b01, 0b10, 0b11)
            int bitX = (comparer.compare(element, midPointFinder.midpoint(boundary.getMin(), boundary.getMax(), 0), 0) >= 0) ? 1 : 0;
            int bitY = (comparer.compare(element, midPointFinder.midpoint(boundary.getMin(), boundary.getMax(), 1), 1) >= 0) ? 1 : 0;
            return bitX | (bitY << 1);
        }

    }

    /** the root node of the quadtree */
    Node<T> root;

    /**
     * Constructor for the QuadTree
     * @param min -
     * @param max -
     * @param max_subdivision_depth : maximum depth of subdivision
     */
    public QuadTreeKdTreeMix(final T min, final T max, final int max_subdivision_depth, IComparer<T> comparer, IMidPointFinder<T> midPointFinder){
        this.comparer = comparer;
        this.midPointFinder = midPointFinder;
        root = new Node<>(min, max, 0, comparer, midPointFinder);
        maxSubdivisionDepth = max_subdivision_depth;
    }

    /**
     * Find all elements within a given rectangle
     * @param min -
     * @param max -
     * @return List of elements within the query rectangle
     */
    @Override
    public List<T> find(final T min, final T max){
        KDAABB<T> searchRegion = new KDAABB<>(min, max, 2, comparer);

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

            if (cur.subdivided) {
                queue.addAll(Arrays.asList(cur.children));
            } else {
                List<T> foundInKdTree = cur.data.find(min, max);
                if (foundInKdTree != null) {
                    results.addAll(foundInKdTree);
                }
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

        // if the element is not in the quadtree boundaries, we throw an exception
        if (!root.contains(element))
            throw new IllegalArgumentException("Element is outside the quadtree boundaries");

        Node<T> cur = root;
        while (true) {
            if (cur.subdivided) { // node is subdivided, we go down in the right quadrant
                cur = cur.children[cur.calculateQuadrant(element)];

            } else { // The node is a leaf
                if (cur.data.isEmpty()) {
                    // The leaf is empty, we place the element there
                    cur.data.insert(element);
                    return true;
                }

                // The node already contains an element, we check the maximum depth before subdividing.
                if (cur.subdivisionDepth >= maxSubdivisionDepth) {
                    cur.data.insert(element);
                    return true;
                }

                // we subdivide the node
                cur.subdivide();
            }
        }
    }
}