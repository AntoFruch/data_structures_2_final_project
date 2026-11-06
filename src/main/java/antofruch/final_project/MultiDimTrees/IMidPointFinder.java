package antofruch.final_project.MultiDimTrees;

@FunctionalInterface
public interface IMidPointFinder<T> {
    T midpoint(final T first, final T second, int coordinate);
}
