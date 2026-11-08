package antofruch.final_project.MultiDimTrees.kd3;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.kd3.bak.KDTree;
import antofruch.final_project.MultiDimTrees.kd3.bak.KeyDuplicateException;
import antofruch.final_project.MultiDimTrees.kd3.bak.KeySizeException;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Kd3TreeAdapter<T> implements MultiDimTree<T> {
    private final KDTree<T> kdTree;
    private final int dimensions;
    private final Function<T, double[]> keyExtractor;

    public Kd3TreeAdapter(int dimensions, Function<T, double[]> keyExtractor) {
        if (dimensions <= 0) {
            throw new IllegalArgumentException("Dimension must be positive");
        }
        this.dimensions = dimensions;
        this.kdTree = new KDTree<>(dimensions);
        this.keyExtractor = Objects.requireNonNull(keyExtractor, "Key extractor cannot be null");
    }

    @Override
    public boolean insert(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot insert null element");
        }

        double[] key = keyExtractor.apply(element);
        validateKey(key);

        try {
            kdTree.insert(key, element);
            return true;
        } catch (KeyDuplicateException e) {
            return false;
        } catch (KeySizeException e) {
            throw new IllegalArgumentException("Key dimension does not match tree dimension", e);
        }
    }

    @Override
    public List<T> find(T min, T max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Bounds min and max cannot be null");
        }
        double[] low = keyExtractor.apply(min);
        double[] upp = keyExtractor.apply(max);
        validateKey(low);
        validateKey(upp);

        try {
            return kdTree.range(low, upp);
        } catch (KeySizeException e) {
            throw new IllegalArgumentException("Bounding keys dimension does not match", e);
        }
    }

    private void validateKey(double[] key) {
        if (key == null || key.length != dimensions) {
            throw new IllegalArgumentException(
                    "Invalid key size: expected " + dimensions + " but got " + (key == null ? 0 : key.length)
            );
        }
    }
}