package antofruch.final_project.MultiDimTrees.utils;

import java.util.List;

public class KDAABB {

    private final int dimension;

    List<Double> min;
    List<Double> max;

    public KDAABB(final List<Double> min, final List<Double> max){
        if (min == null || max == null)
            throw new IllegalArgumentException("Min and max must not be null");
        if (min.size() != max.size())
            throw new IllegalArgumentException("Min and max must have the same size");

        this.dimension = min.size();
        this.min = min;
        this.max = max;
    }

    public boolean contains(IPositionable point){
        if (point == null)
            throw new IllegalArgumentException("Point must not be null");
        if (point.getDimension() != dimension)
            throw new IllegalArgumentException("Point must be " + dimension + "-dimensional");

        for (int i = 0; i < dimension; i++) {
            double coord = point.getCoordinate(i);
            double minCoord = min.get(i);
            double maxCoord = max.get(i);

            if (coord < minCoord || coord > maxCoord) {
                return false;
            }
        }
        return true;
    }

    public boolean intersects(final KDAABB other){
        if (other == null)
            throw new IllegalArgumentException("Other AABB must not be null");
        if (other.dimension != dimension)
            throw new IllegalArgumentException("AABBs must have the same dimension");

        for (int i = 0; i < dimension; i++) {
            double thisMin = min.get(i);
            double thisMax = max.get(i);
            double otherMin = other.min.get(i);
            double otherMax = other.max.get(i);

            if (thisMin > otherMax || otherMin > thisMax) {
                return false;
            }
        }
        return true;
    }

    public int getDimension() {
        return dimension;
    }

    public List<Double> getMin() {
        return min;
    }

    public List<Double> getMax() {
        return max;
    }

    public double getWidth(int axis){
        return max.get(axis) - min.get(axis);
    }
}
