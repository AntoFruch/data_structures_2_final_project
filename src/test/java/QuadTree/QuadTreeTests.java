package QuadTree;

import antofruch.final_project.QuadTree.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuadTreeTests {

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
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        public String toString() {
            return "TestItem{name='" + name + "', x=" + x + ", y=" + y + "}";
        }
    }

    private QuadTree<TestItem> quadTree;

    @BeforeEach
    void setUp() {
        quadTree = new QuadTree<>(100, 100, 4);
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
}

