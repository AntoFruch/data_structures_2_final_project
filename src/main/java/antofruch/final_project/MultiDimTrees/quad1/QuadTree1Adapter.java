package antofruch.final_project.MultiDimTrees.quad1;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.quad1.quadtree.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuadTree1Adapter<T> implements MultiDimTree<T> {
    private final QuadTree<T> tree;
    private final Function<T, double[]> keyExtractor;

    public QuadTree1Adapter(T min,T max, Function<T, double[]> keyExtractor){
        this.keyExtractor = Objects.requireNonNull(keyExtractor, "Key extractor cannot be null");
        double[] minCoo = keyExtractor.apply(min);
        double[] maxCoo = keyExtractor.apply(max);
        this.tree = new QuadTree<>(minCoo[0], minCoo[1], maxCoo[0], maxCoo[1]);
    }

    @Override
    public boolean insert(T element) {
        double[] coo = keyExtractor.apply(element);
        try {
            tree.set(coo[0], coo[1], element);
        } catch (QuadTreeException e){
            return false;
        }
        return true;
    }

    @Override
    public List<T> find(T min, T max) {
        double[] minCoo = keyExtractor.apply(min);
        double[] maxCoo = keyExtractor.apply(max);
        return Stream.of(tree.searchWithin(minCoo[0], minCoo[1], maxCoo[0], maxCoo[1]))
                .map(Point::getValue)
                .collect(Collectors.toList());
    }
}
