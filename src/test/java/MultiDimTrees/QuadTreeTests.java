package MultiDimTrees;


import antofruch.final_project.MultiDimTrees.MyTrees.MultiDimTree;
import antofruch.final_project.MultiDimTrees.MyTrees.QuadTreeKdTreeMix;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuadTreeTests {

    static class TestItem {
        private final double x;
        private final double y;
        private final String name;

        public TestItem(double x, double y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }

        public String toString() {
            return "TestItem{name='" + name + "', x=" + x + ", y=" + y + "}";
        }
    }

    private MultiDimTree<TestItem> quadTree;

    private int compareTestItem(TestItem a, TestItem b, int axis) {
        switch (axis) {
            case 0:
                return Double.compare(a.x, b.x);
            case 1:
                return Double.compare(a.y, b.y);
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
    }

    @BeforeEach
    void setUp() {
        TestItem min = new TestItem(0, 0, "Min");
        TestItem max = new TestItem(100, 100, "Max");
        quadTree = new QuadTreeKdTreeMix<>(
                min,
                max,
                6,
                this::compareTestItem,
                (a, b, axis) -> {
                    switch (axis) {
                        case 0:
                            return new TestItem((a.x + b.x) / 2.0, a.y, "");
                        case 1:
                            return new TestItem(a.x, (a.y + b.y) / 2.0, "");
                        default:
                            throw new IllegalArgumentException("Invalid axis");
                    }
                }
        );
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
    @DisplayName("Insert same key twice")
    void testInsertSameTwice(){
        TestItem item1 = new TestItem(25, 25, "A");
        TestItem item2 = new TestItem(25, 25, "B");
        quadTree.insert(item1);
        boolean inserted = quadTree.insert(item2);
        assertTrue(inserted, "The item should be inserted twice with no problem.");
    }

    @Test
    @DisplayName("Should find an item within a query region")
    void testFindItem() {
        TestItem item = new TestItem(25, 25, "A");
        quadTree.insert(item);

        TestItem min = new TestItem(20, 20, "min");
        TestItem max = new TestItem(30, 30, "max");
        List<TestItem> found = quadTree.find(min, max);

        assertFalse(found.isEmpty(), "The item should be found within the query region.");
    }

    @Test
    @DisplayName("Should find multiple items within a query region")
    void testFindMultipleItems() {
        TestItem item1 = new TestItem(25, 25, "A");
        TestItem item2 = new TestItem(30, 30, "B");
        quadTree.insert(item1);
        quadTree.insert(item2);

        TestItem min = new TestItem(20, 20, "min");
        TestItem max = new TestItem(31, 31, "max");
        List<TestItem> found = quadTree.find(min, max);

        assertEquals(2, found.size(), "Both items should be found within the query region.");
    }

    @Test
    @DisplayName("Should find multiple same items within a query region")
    void testFindMultipleSameItems() {
        TestItem item1 = new TestItem(25, 25, "A");
        TestItem item2 = new TestItem(25, 25, "B");
        quadTree.insert(item1);
        quadTree.insert(item2);

        TestItem min = new TestItem(20, 20, "min");
        TestItem max = new TestItem(31, 31, "max");
        List<TestItem> found = quadTree.find(min, max);

        assertEquals(2, found.size(), "Both items should be found within the query region.");
    }
}

