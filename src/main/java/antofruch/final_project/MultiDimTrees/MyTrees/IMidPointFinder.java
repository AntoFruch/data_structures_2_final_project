package antofruch.final_project.MultiDimTrees.MyTrees;

@FunctionalInterface
public interface IMidPointFinder<T> {
    T midpoint(final T first, final T second, int coordinate);
}
