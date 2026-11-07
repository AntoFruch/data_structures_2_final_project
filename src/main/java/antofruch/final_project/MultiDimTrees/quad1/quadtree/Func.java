package antofruch.final_project.MultiDimTrees.quad1.quadtree;

public interface Func<T> {
    public void call(QuadTree<T> quadTree, Node<T> node);
}
