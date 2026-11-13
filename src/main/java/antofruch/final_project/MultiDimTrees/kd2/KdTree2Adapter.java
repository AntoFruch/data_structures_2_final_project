package antofruch.final_project.MultiDimTrees.kd2;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.kd2.utils.KDTree;
import antofruch.final_project.MultiDimTrees.kd3.bak.KDException;

import java.util.List;
import java.util.function.Function;

public class KdTree2Adapter<T> implements MultiDimTree<T> {

    KDTree<T> kdTree;
    int dimension;

    private final Function<T, double[]> keyExtractor;


    public KdTree2Adapter(int dim, Function<T, double[]> keyExtractor) {
        this.dimension = dim;
        this.kdTree = new KDTree<>(dim);
        this.keyExtractor = keyExtractor;
    }

    @Override
    public boolean insert(T element) {
        double[] coo = keyExtractor.apply(element);
        return kdTree.insert(coo, element) != null;
    }

    @Override
    public List<T> find(T min, T max) {
        double[] minCoo = keyExtractor.apply(min);
        double[] maxCoo = keyExtractor.apply(max);

        return kdTree.queryRange(minCoo, maxCoo);
    }
}
