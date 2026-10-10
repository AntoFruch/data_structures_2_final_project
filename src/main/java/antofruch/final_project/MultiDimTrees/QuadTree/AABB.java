package antofruch.final_project.MultiDimTrees.QuadTree;

/**
 * Axis-aligned bounded box representation
 * Bounded box is closed left and open right for x and y coordinates
 */
class AABB {
    /** up-left corner */
    double x;
    /** up-left corner */
    double y;
    /** width of the box */
    double width;
    /** height of the box */
    double height;

    public AABB(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(final double x, final double y){
        return (x > this.x && x <= this.x + this.width) && (y > this.y && y <= this.y + this.height);
    }

    public boolean intersects(final AABB other) {
        if (other == null)
            throw new IllegalArgumentException("Other AABB cannot be null");
        return x + width < other.x || other.x + other.width < x || y + height < other.y || other.y + other.height < y;
    }
}

