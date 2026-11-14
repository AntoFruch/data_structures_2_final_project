package antofruch.final_project;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.MyTrees.KDimTree;
import antofruch.final_project.MultiDimTrees.MyTrees.QuadTree;
import antofruch.final_project.MultiDimTrees.MyTrees.QuadTreeKdTreeMix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Facade {

    static final int MIN_LAT = -90;
    static final int MAX_LAT = 90;
    static final int MIN_LNG = -180;
    static final int MAX_LNG = 180;
    //SINGLETON
    private static Facade instance = null;
    public static Facade getInstance(){
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    private Facade(){
        System.out.println("Facade has been instantiated");
    }


    MultiDimTree<POI> tree;


    private int comparePOI(final POI a, final POI b, final int axis){
        switch (axis) {
            case 0:
                return Double.compare(a.getLatitude(), b.getLatitude());
            case 1:
                return Double.compare(a.getLongitude(), b.getLongitude());
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }
    private POI findPOImidpoints(final POI a, final POI b, final int axis){
        switch (axis) {
            case 0:
                return new POI("", (a.getLatitude() + b.getLatitude()) / 2, a.getLongitude(), "");
            case 1:
                return new POI("", a.getLatitude(), (a.getLongitude() + b.getLongitude()) / 2, "");
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }

    public void createTree(final TreeType type,
                           final Double minLat,
                           final double maxLat,
                           final double minLng,
                           final double maxLng,
                           final int max_subdivision_depth) {
        POI min = new POI("", minLat, minLng, "");
        POI max = new POI("", maxLat, maxLng, "");

        switch (type) {
            case QuadTree:
                tree = new QuadTree<>(min, max, max_subdivision_depth, this::comparePOI, this::findPOImidpoints);
                break;
            case MixedTree:
                tree = new QuadTreeKdTreeMix<>(min, max, max_subdivision_depth, this::comparePOI, this::findPOImidpoints);
                break;
            case KdTree:
                tree = new KDimTree<>(2, this::comparePOI);
                break;
            default:
                throw new IllegalArgumentException("Invalid tree type");
        }
    }

    public void generateRandomData(final int N){
        Random random = new Random();

        for (int i=0; i<N; ++i){
            tree.insert(
                new POI(
                "Generated",
                random.nextDouble() * (MAX_LAT - MIN_LAT) + MIN_LAT,
                random.nextDouble() * (MAX_LNG - MIN_LNG) + MIN_LNG,
                "This is a description")
            );
        }
    }

    // Import - Export
    public void importCsv(final String filepath){}
    public void exportCsv(){}

    // ===== USE CASES =====
    public List<POI> findPOIRange(
            final double minLat,
            final double maxLat,
            final double minLng,
            final double maxLng){
        POI min = new POI("", minLat, minLng, "");
        POI max = new POI("", maxLat, maxLng, "");

        return tree.find(min, max);
    }

    public List<POI> findAllPOI(){
        return findPOIRange(MIN_LAT, MAX_LAT, MIN_LNG, MAX_LNG);
    }

    public List<POI> findNearPOI(final POI point, final double radius){
        List<POI> range = findPOIRange(
                point.getLatitude() - radius,
                point.getLatitude() + radius,
                point.getLongitude() - radius,
                point.getLongitude() + radius
        );


        List<POI> result = new ArrayList<>();
        for (final POI p : range) {
            if (point.getDistance(p) <= radius) {
                result.add(p);
            }
        }
        return result;
    }

    public void createNewPOI(final String name, final double lat, final double lng, final String description){
        POI poi = new POI(name, lat, lng, description);
        tree.insert(poi);
    }

    public void editPOI(final POI point,
                        final String name,
                        final String description){
        point.setName(name);
        point.setDescription(description);
    }

    public List<POI> findPOIbyName(final String name,
                                   final double minLat,
                                   final double maxLat,
                                   final double minLng,
                                   final double maxLng){
        List<POI> list = findPOIRange(minLat, maxLat, minLng, maxLng);
        List<POI> result = new ArrayList<>();
        for (final POI p : list) {
            if (p.getName().equals(name)) {
                result.add(p);
            }
        }
        return result;
    }
}
