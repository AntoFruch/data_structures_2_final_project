package antofruch.final_project.MultiDimTrees;

import java.util.List;

public interface MultiDimTree<T> {
    boolean insert(final T element);
    List<T> find(final T min, final T max);
}
