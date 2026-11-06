package antofruch.final_project.MultiDimTrees;

public class KDAABB<T> {

    int dimension;
    T min;
    T max;
    IComparer<T> comparer;


    public KDAABB(final T min, final T max, int dim, IComparer<T> comparer){
        if (min == null || max == null)
            throw new IllegalArgumentException("Min and max must not be null");
        this.dimension = dim;
        this.min = min;
        this.max = max;
        this.comparer = comparer;
    }

    public boolean contains(T point){
        if (point == null)
            throw new IllegalArgumentException("Point must not be null");

        for (int i=0; i < dimension; ++i){
            if (comparer.compare(point, min, i) < 0 || comparer.compare(point, max, i) >= 0)
                return false;
        }
        return true;
    }

    public boolean intersects(final KDAABB<T> other){
        if (other == null)
            throw new IllegalArgumentException("Other AABB must not be null");

        for (int i=0; i< dimension; i++){
            if (comparer.compare(other.getMin(), max, i) >= 0 || comparer.compare(other.getMax(), min, i) <= 0)
                return false;
        }

        return true;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
