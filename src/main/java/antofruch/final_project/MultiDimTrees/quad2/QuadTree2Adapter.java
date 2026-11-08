package antofruch.final_project.MultiDimTrees.quad2;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.quad2.quadtree.QuadTree;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuadTree2Adapter<T> implements MultiDimTree<T> {
    private final QuadTree<T> tree;
    private final Function<T, double[]> keyExtractor;

    public QuadTree2Adapter(Function<T, double[]> keyExtractor){
        this.keyExtractor = Objects.requireNonNull(keyExtractor, "keyExtractor must not be null");
        this.tree = new QuadTree<>();
    }

    @Override
    public boolean insert(T element) {
        // no error handling in the external class
        double[] coo = keyExtractor.apply(element);
        tree.place(coo[0], coo[1], element);
        return true; // we assume it works
    }

    @Override
    public List<T> find(T min, T max) {
        double[] minCoo = keyExtractor.apply(min);
        double[] maxCoo = keyExtractor.apply(max);
        return tree.findAll(minCoo[0], minCoo[1], maxCoo[0], maxCoo[1])
                .stream()
                .map(h -> h.o)
                .collect(Collectors.toList());
    }
}
