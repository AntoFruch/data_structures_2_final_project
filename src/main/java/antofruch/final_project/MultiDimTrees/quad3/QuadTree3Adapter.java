package antofruch.final_project.MultiDimTrees.quad3;

import antofruch.final_project.MultiDimTrees.MultiDimTree;

import java.util.List;

public class QuadTree3Adapter<T> implements MultiDimTree<T> {

    @Override
    public boolean insert(T element) {
        return false;
    }

    @Override
    public List<T> find(T min, T max) {
        return List.of();
    }
}
