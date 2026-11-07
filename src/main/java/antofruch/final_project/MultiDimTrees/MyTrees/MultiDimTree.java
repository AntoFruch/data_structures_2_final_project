package antofruch.final_project.MultiDimTrees.MyTrees;

import java.util.List;

public interface MultiDimTree<T> {
    boolean insert(final T element);
    List<T> find(final T min, final T max);
}
