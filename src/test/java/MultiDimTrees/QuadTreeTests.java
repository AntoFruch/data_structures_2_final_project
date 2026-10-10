package MultiDimTrees;

import antofruch.final_project.MultiDimTrees.QuadTree.QuadTree;
import antofruch.final_project.MultiDimTrees.Positionable;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuadTreeTests {

    static class TestItem implements Positionable {
        private final double x;
        private final double y;
        private final String name;

        public TestItem(double x, double y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }

        @Override
        public double getCoordinate(int axis) {
            switch (axis) {
                case 0:
                    return x;
                case 1:
                    return y;
                default:
                    throw new IllegalArgumentException("Invalid axis");
            }
        }

        @Override
        public int getDimension() {
            return 2;
        }

        public String toString() {
            return "TestItem{name='" + name + "', x=" + x + ", y=" + y + "}";
        }
    }

    private QuadTree<TestItem> quadTree;

    @BeforeEach
    void setUp() {
        quadTree = new QuadTree<>(100, 100, 6);
    }

    @Test
    @DisplayName("Should successfully insert a single item within the boundary")
    void testInsertSingleItem() {
        TestItem item = new TestItem(25, 25, "A");
        boolean inserted = quadTree.insert(item);

        assertTrue(inserted, "The item should be successfully inserted.");
    }

    @Test
    @DisplayName("Should throw when inserting an item outside the root boundary")
    void testInsertOutOfBounds() {
        TestItem outItem = new TestItem(150, 150, "OutOfBounds");

        assertThrows(IllegalArgumentException.class, () -> quadTree.insert(outItem), "The item outside the boundary should be rejected.");
    }

    @Test
    @DisplayName("Should find an item within a query region")
    void testFindItem() {
        TestItem item = new TestItem(25, 25, "A");
        quadTree.insert(item);

        List<TestItem> found = quadTree.find(20, 20, 10, 10);

        assertFalse(found.isEmpty(), "The item should be found within the query region.");
    }

    @Test
    @DisplayName("Should find multiple items within a query region")
    void testFindMultipleItems() {
        TestItem item1 = new TestItem(25, 25, "A");
        TestItem item2 = new TestItem(30, 30, "B");
        quadTree.insert(item1);
        quadTree.insert(item2);

        List<TestItem> found = quadTree.find(20, 20, 10, 10);

        assertEquals(2, found.size(), "Both items should be found within the query region.");
    }
}

