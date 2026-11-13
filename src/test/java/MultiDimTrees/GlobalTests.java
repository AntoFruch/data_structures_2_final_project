package MultiDimTrees;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.MyTrees.*;
import antofruch.final_project.MultiDimTrees.kd1.KdTree1Adapter;
import antofruch.final_project.MultiDimTrees.kd2.KdTree2Adapter;
import antofruch.final_project.MultiDimTrees.kd3.Kd3TreeAdapter;
import antofruch.final_project.MultiDimTrees.quad1.QuadTree1Adapter;
import antofruch.final_project.MultiDimTrees.quad2.QuadTree2Adapter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalTests {

    public static class Point{
        long id;
        public double lat;
        public double lon;
        public Point(long id, double lat, double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }
        @Override
        public String toString() {
            return "Point{lat=" + lat + ", lon=" + lon + "}";
        }
    }
    static private int compare(Point a, Point b, int axis) {
        switch (axis) {
            case 0:
                return Double.compare(a.lat, b.lat);
            case 1:
                return Double.compare(a.lon, b.lon);
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }
    static private Point findMidPoint(Point a, Point b, int axis) {
        switch (axis) {
            case 0:
                return new Point(-1, (a.lat + b.lat) / 2.0, a.lon);
            case 1:
                return new Point(-1, a.lat, (a.lon + b.lon) / 2.0);
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }
    static private double[] extractKeys(Point p){
        double[] coordinates = new double[2];
        coordinates[0] = p.lat;
        coordinates[1] = p.lon;
        return coordinates;
    }

    static List<Point> points;
    static Point min;
    static Point max;
    private static void loadDataSet(){
        if (points == null) {
            points = new ArrayList<>();
        } else {
            points.clear();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(GlobalTests.class.getResourceAsStream("/slovakia_nodes.csv"),
                        "file /slovakia_nodes.csv does not exist in src/main/resources"),
                StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.isEmpty()) continue;

                String[] columns = line.split(";");
                if (columns.length == 3) {
                    long id = Long.parseLong(columns[0].trim());

                    double lat = Double.parseDouble(columns[1].trim().replace(',', '.'));
                    double lon = Double.parseDouble(columns[2].trim().replace(',', '.'));

                    points.add(new Point(id, lat, lon));
                }
            }

            if (points.isEmpty()) {
                System.err.println("Attention : aucun point n'a été chargé.");
                return;
            }

            double minLat = points.get(0).lat, maxLat = points.get(0).lat;
            double minLon = points.get(0).lon, maxLon = points.get(0).lon;

            for (Point p : points) {
                if (p.lat < minLat) minLat = p.lat;
                if (p.lat > maxLat) maxLat = p.lat;

                if (p.lon < minLon) minLon = p.lon;
                if (p.lon > maxLon) maxLon = p.lon;
            }

            min = new Point(-1, minLat - 1e-7, minLon - 1e-7);
            max = new Point(-1, maxLat + 1e-7, maxLon + 1e-7);
            System.out.println(
                    "size: " + points.size() +
                    "\nmin: " + min +
                    "\nmax: " + max);

        } catch (IOException e) {
            throw new RuntimeException("Error while reading points dataset.", e);
        }
    }

    private static class Range {
        long id;
        public Point min;
        public Point max;
        public Range(long id, Point min, Point max) {
            this.id = id;
            this.min = min;
            this.max = max;
        }
    }
    static List<Range> ranges;
    private static void loadRanges(){
        if (ranges == null) {
            ranges = new ArrayList<>();
        } else {
            ranges.clear();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(GlobalTests.class.getResourceAsStream("/testovacie_oblasti.csv"),
                        "file /testovacie_oblasti.csv does not exist in src/main/resources"),
                StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.isEmpty()) continue;

                String[] columns = line.split(";");
                if (columns.length == 5) {
                    long id = Long.parseLong(columns[0].trim());

                    double minlat = Double.parseDouble(columns[1].trim().replace(',', '.'));
                    double minlon = Double.parseDouble(columns[2].trim().replace(',', '.'));
                    double maxlat = Double.parseDouble(columns[3].trim().replace(',', '.'));
                    double maxlon = Double.parseDouble(columns[4].trim().replace(',', '.'));

                    ranges.add(
                        new Range(
                            id,
                            new Point(-1, minlat, minlon),
                            new Point(-1, maxlat, maxlon)
                        )
                    );
                }
            }

            if (ranges.isEmpty()) {
                System.err.println("Warning : no range as been loaded.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error while reading ranges dataset.", e);
        }
    }


    static Stream<Arguments> provideTreeImplementations() {
        return Stream.of(
                Arguments.of("QuadTree", (Supplier<MultiDimTree<Point>>) () -> new QuadTree<>(min, max, 10, GlobalTests::compare, GlobalTests::findMidPoint)),
                Arguments.of("KDimTree", (Supplier<MultiDimTree<Point>>) () -> new KDimTree<>(2, GlobalTests::compare)),
                Arguments.of("QuadTreeKdTreeMix", (Supplier<MultiDimTree<Point>>) () -> new QuadTreeKdTreeMix<>(min, max, 10, GlobalTests::compare, GlobalTests::findMidPoint)),
                Arguments.of("ext_KdTree1", (Supplier<MultiDimTree<Point>>) () -> new KdTree1Adapter<>(2, GlobalTests::extractKeys)),
                Arguments.of("ext_KdTree2", (Supplier<MultiDimTree<Point>>) () -> new KdTree2Adapter<>(2, GlobalTests::extractKeys)),
                Arguments.of("ext_KdTree3", (Supplier<MultiDimTree<Point>>) () -> new Kd3TreeAdapter<>(2, GlobalTests::extractKeys)),
                Arguments.of("ext_QuadTree1", (Supplier<MultiDimTree<Point>>) () -> new QuadTree1Adapter<>(min, max, GlobalTests::extractKeys)),
                Arguments.of("ext_QuadTree2", (Supplier<MultiDimTree<Point>>) () -> new QuadTree2Adapter<>(GlobalTests::extractKeys))
        );
    }

    @BeforeAll
    public static void setup(){
        loadDataSet();
        loadRanges();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideTreeImplementations")
    @DisplayName("Successful insertion test")
    public void insertionTest(String treeName, Supplier<MultiDimTree<Point>> treeSupplier) {
        MultiDimTree<Point> tree = treeSupplier.get();
        Assertions.assertDoesNotThrow(() -> {
            for(Point p : points){
                Assertions.assertTrue(tree.insert(p));
            }
        });

        List<Point> result = tree.find(min, max);
        Assertions.assertEquals(points.size(), result.size());
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideTreeImplementations")
    @DisplayName("No False Positives")
    public void noFalsePositiveTest(String treeName, Supplier<MultiDimTree<Point>> treeSupplier) {
        MultiDimTree<Point> tree = treeSupplier.get();
        for(Point p : points){
            tree.insert(p);
        }

        for (Range r : ranges) {
            List<Point> found = tree.find(r.min, r.max);
            for (Point p : found) {
                assertTrue(p.lat >= r.min.lat && p.lat <= r.max.lat, "Point out latitude");
                assertTrue(p.lon >= r.min.lon && p.lon <= r.max.lon, "Point out longitude");
            }
        }
    }
}

