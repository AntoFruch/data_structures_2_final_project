package MultiDimTrees;

import antofruch.final_project.MultiDimTrees.KDAABB;
import antofruch.final_project.MultiDimTrees.Positionable;
import antofruch.final_project.MultiDimTrees.QuadTree.QuadTree;
import antofruch.final_project.MultiDimTrees.kDimTree.KDimTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDimTreeTest {

    static class TestItem3D implements Positionable {
        private final double x;
        private final double y;
        private final double z;
        private final String name;

        public TestItem3D(double x, double y, double z, String name) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }

        @Override
        public double getCoordinate(int axis) {
            switch (axis) {
                case 0:
                    return x;
                case 1:
                    return y;
                case 2:
                    return z;
                default:
                    throw new IllegalArgumentException("Invalid axis");
            }
        }

        @Override
        public int getDimension() {
            return 3;
        }

        public String toString() {
            return "TestItem3D{name='" + name + "', x=" + x + ", y=" + y + ", z=" + z + "}";
        }
    }
    static class TestItem6D implements Positionable {
        private final double x;
        private final double y;
        private final double z;
        private final double a;
        private final double b;
        private final double c;
        private final String name;

        public TestItem6D(double x, double y, double z, double a, double b, double c, String name) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.a = a;
            this.b = b;
            this.c = c;
            this.name = name;
        }

        @Override
        public double getCoordinate(int axis) {
            switch (axis) {
                case 0:
                    return x;
                case 1:
                    return y;
                case 2:
                    return z;
                case 3:
                    return a;
                case 4:
                    return b;
                case 5:
                    return c;
                default:
                    throw new IllegalArgumentException("Invalid axis");
            }
        }

        @Override
        public int getDimension() {
            return 6;
        }

        public String toString() {
            return "TestItem6D{name='" + name + "', x=" + x + ", y=" + y + ", z=" + z + ", a=" + a + ", b=" + b + ", c=" + c + "}";
        }
    }

    private KDimTree<TestItem3D> tree;

    @BeforeEach
    void setUp() {
        tree = new KDimTree<>(3);
    }

    @Test
    @DisplayName("Should successfully insert multiple items")
    void testInsertMultipleItems() {
        TestItem3D item1 = new TestItem3D(25, 25, 25, "A");
        TestItem3D item2 = new TestItem3D(75, 75, 75, "B");
        TestItem3D item3 = new TestItem3D(50, 50, 50, "C");
        boolean inserted1 = tree.insert(item1);
        boolean inserted2 = tree.insert(item2);
        boolean inserted3 = tree.insert(item3);

        assertTrue(inserted1, "The first item should be successfully inserted.");
        assertTrue(inserted2, "The second item should be successfully inserted.");
        assertTrue(inserted3, "The third item should be successfully inserted.");
    }

    @Test
    @DisplayName("Should find multiple items within a query region")
    void testFindMultipleItems() {
        TestItem3D item1 = new TestItem3D(25, 25, 25, "A");
        TestItem3D item2 = new TestItem3D(30, 30, 30, "B");
        TestItem3D item3 = new TestItem3D(22, 23, 29, "C");
        tree.insert(item1);
        tree.insert(item2);
        tree.insert(item3);

        List<Double> min = Arrays.asList(20.0, 20.0, 20.0);
        List<Double> max = Arrays.asList(30.0, 30.0, 30.0);
        List<TestItem3D> found = tree.find(new KDAABB(min, max));

        assertEquals(3, found.size(), "All three items should be found within the query region.");
    }
}
