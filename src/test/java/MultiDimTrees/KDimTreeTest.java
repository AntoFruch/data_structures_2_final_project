package MultiDimTrees;

import antofruch.final_project.MultiDimTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.KDimTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDimTreeTest {

    static class TestItem3D {
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
        public String toString() {
            return "TestItem3D{name='" + name + "', x=" + x + ", y=" + y + ", z=" + z + "}";
        }
    }

    private MultiDimTree<TestItem3D> tree;

    private int compareTestItem3D(TestItem3D first, TestItem3D second, int dimension) {
        switch (dimension) {
            case 0:
                return Double.compare(first.x, second.x);
            case 1:
                return Double.compare(first.y, second.y);
            case 2:
                return Double.compare(first.z, second.z);
            default:
                throw new IllegalArgumentException("Invalid dimension");
        }
    }

    @BeforeEach
    void setUp() {
        tree = new KDimTree<>(3, this::compareTestItem3D);
    }

    @Test
    @DisplayName("Insert same key twice")
    void testInsertSameTwice(){
        TestItem3D item1 = new TestItem3D(25, 25, 25, "A");
        TestItem3D item2 = new TestItem3D(25, 25, 25, "B");
        tree.insert(item1);
        boolean inserted = tree.insert(item2);
        assertTrue(inserted, "The item should be inserted twice with no problem.");
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

        TestItem3D min = new TestItem3D(20, 20, 20, "Min");
        TestItem3D max = new TestItem3D(31, 31, 31, "Max");
        List<TestItem3D> found = tree.find(min ,max);

        assertEquals(3, found.size(), "All three items should be found within the query region.");
    }

    @Test
    @DisplayName("Should find multiple same items within a query region")
    void testFindMultipleSameItems() {
        TestItem3D item1 = new TestItem3D(25, 25, 25, "A");
        TestItem3D item2 = new TestItem3D(25, 25, 25, "B");
        TestItem3D item3 = new TestItem3D(25, 25, 25, "C");
        tree.insert(item1);
        tree.insert(item2);
        tree.insert(item3);

        TestItem3D min = new TestItem3D(20, 20, 20, "Min");
        TestItem3D max = new TestItem3D(30, 30, 30, "Max");
        List<TestItem3D> found = tree.find(min, max);

        assertEquals(3, found.size(), "All three items should be found within the query region.");
    }
}
