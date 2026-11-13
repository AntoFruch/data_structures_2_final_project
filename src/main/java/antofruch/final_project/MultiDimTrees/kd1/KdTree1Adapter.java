package antofruch.final_project.MultiDimTrees.kd1;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.kd1.index.kdtree.KDIterator;
import antofruch.final_project.MultiDimTrees.kd1.index.kdtree.KDTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KdTree1Adapter<T> implements MultiDimTree<T> {

    final KDTree<T> kdTree;
    final int dimension;
    private final Function<T, double[]> keyExtractor;

    public KdTree1Adapter(int dim, Function<T, double[]> keyExtractor){
        this.kdTree = new KDTree<>(dim, false);
        this.dimension = dim;
        this.keyExtractor = keyExtractor;
    }

    @Override
    public boolean insert(T element) {
        double [] coo = keyExtractor.apply(element);
        kdTree.insert(coo, element);
        return true;
    }

    @Override
    public List<T> find(T min, T max) {
        double[] minCoo = keyExtractor.apply(min);
        double[] maxCoo = keyExtractor.apply(max);

        ArrayList<T> list = new ArrayList<>();

        KDIterator<T> iterator = kdTree.query(minCoo, maxCoo);
        while (iterator.hasNext()) {
            list.add(iterator.next().value());
        }
        return list;
    }
}
