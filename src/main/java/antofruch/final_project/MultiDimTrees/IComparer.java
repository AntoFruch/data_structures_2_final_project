package antofruch.final_project.MultiDimTrees;

/**
 * Interface that describe a space-positionable object.
 */
@FunctionalInterface
public interface IComparer<T> {
    int compare(T first, T second, int dimension);
}
