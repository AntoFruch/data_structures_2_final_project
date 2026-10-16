package antofruch.final_project.MultiDimTrees;

import antofruch.final_project.MultiDimTrees.utils.IPositionable;
import antofruch.final_project.MultiDimTrees.utils.KDAABB;

import java.util.List;

public interface MultiDimTree<T extends IPositionable> {
    boolean insert(final T element);
    List<T> find(final KDAABB searchRegion);
}
