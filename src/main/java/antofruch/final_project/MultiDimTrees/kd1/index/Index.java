/*
 * Copyright 2016-2017 Tilmann Zaeschke
 *
 * This file is part of TinSpin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package antofruch.final_project.MultiDimTrees.kd1.index;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/** Common interface for all index implementations. */
public interface Index {

  /**
   * Number of key dimensions.
   *
   * @return the number of dimensions
   */
  int getDims();

  /**
   * Number of entries.
   *
   * @return the number of entries
   */
  int size();

  /** Clear all entries. */
  void clear();

  /**
   * Index statistics.
   *
   * @return Collect and return some index statistics. Note that indexes are not required to fill
   *     all fields. Also, individual indexes may use subclasses with additional fields. Many
   *     indexes also perform consistency checks while gathering stats.
   */
  Stats getStats();

  /**
   * The number of nodes in the tree.
   *
   * @return The number of nodes.
   */
  int getNodeCount();

  /**
   * The maximum depth of the tree in terms of nodes.
   *
   * @return maximum depth.
   */
  int getDepth();

  /**
   * String representation of the index.
   *
   * @return a full string output of the tree structure with all entries
   */
  String toStringTree();

  /**
   * Common interface for query result iterators for extents queries, point queries and window
   * queries.
   *
   * @param <T> Entry type.
   */
  interface QueryIterator<T> extends Iterator<T> {
    /**
     * This method resets an iterator. The arguments determine new iterator properties:<br>
     * - For Extent iterators, see e.g. {@link PointMap#iterator()}, both arguments must be `null`.
     * <br>
     * - For point query iterators, see e.g. {@link PointMultimap#queryExactPoint(double[])}, the
     * first argument is the new query point and the second argument must be `null`.<br>
     * - For window queries, see e.g. {@link PointMap#query(double[], double[])}, the arguments are
     * the min/max corners of the new query window.<br>
     *
     * @param point1 point or `null`
     * @param point2 point or `null`
     * @return this iterator after reset.
     */
    QueryIterator<T> reset(double[] point1, double[] point2);
  }

  /**
   * Iterator over Point entries.
   *
   * @param <T> value type
   */
  interface PointIterator<T> extends QueryIterator<PointEntry<T>> {}

  /**
   * Common interface for query result iterators for k nearest neighbor queries.
   *
   * @param <T> Entry type.
   */
  interface QueryIteratorKnn<T> extends Iterator<T> {
    /**
     * Reset the iterator (allows object reuse).
     *
     * @param center new center point
     * @param k new k
     * @return fresh iterator
     */
    QueryIteratorKnn<T> reset(double[] center, int k);
  }

  /**
   * Iterator over Point entries.
   *
   * @param <T> value type
   */
  interface PointIteratorKnn<T> extends QueryIteratorKnn<PointEntryKnn<T>> {}

  /**
   * Entry for point indexes.
   *
   * @param <T> value type
   */
  class PointEntry<T> {

    private double[] point;
    private T value;

    /**
     * Create entry.
     *
     * @param point point
     * @param value value
     */
    public PointEntry(double[] point, T value) {
      this.point = point;
      this.value = value;
    }

    /**
     * Point.
     *
     * @return The coordinates of the entry.
     */
    public double[] point() {
      return point;
    }

    /**
     * Value.
     *
     * @return The value associated with the box or point.
     */
    public T value() {
      return value;
    }

    @Override
    public String toString() {
      return Arrays.toString(point) + ";v=" + value;
    }

    /**
     * Update entry.
     *
     * @param point new point
     */
    public void setPoint(double[] point) {
      this.point = point;
    }

    /**
     * Update entry.
     *
     * @param point new point
     * @param value value
     */
    protected void set(double[] point, T value) {
      this.point = point;
      this.value = value;
    }
  }

  /**
   * Entry for point indexes returned by nearest neighbor queries.
   *
   * @param <T> value type
   */
  class PointEntryKnn<T> extends PointEntry<T> {

    private double dist;

    /**
     * Create entry.
     *
     * @param point point
     * @param value value
     * @param dist distance
     */
    public PointEntryKnn(double[] point, T value, double dist) {
      super(point, value);
      this.dist = dist;
    }

    /**
     * Create entry.
     *
     * @param entry other entry
     * @param dist distance
     */
    public PointEntryKnn(PointEntry<T> entry, double dist) {
      super(entry.point(), entry.value());
      this.dist = dist;
    }

    /**
     * An entry with distance property. This is, for example, used as a return value for nearest
     * neighbour queries.
     *
     * @return the distance
     */
    public double dist() {
      return dist;
    }

    /**
     * Update entry.
     *
     * @param point new min
     * @param value new value
     * @param dist new distance
     */
    public void set(double[] point, T value, double dist) {
      super.set(point, value);
      this.dist = dist;
    }

    /**
     * Update entry from other (without distance).
     *
     * @param entry other entry
     * @param dist distance
     */
    public void set(PointEntry<T> entry, double dist) {
      super.set(entry.point(), entry.value);
      this.dist = dist;
    }
  }

  @FunctionalInterface
  interface PointFilterKnn<T> {
    /**
     * Filter entries by distance.
     *
     * @param entry entry
     * @param distance distance
     * @return true if entry is closer than distance
     */
    boolean test(PointEntry<T> entry, double distance);
  }

  /** Distance comparator function for Point kNN query result entries. */
  interface PEComparator extends Comparator<PointEntryKnn<?>> {
    @Override
    int compare(PointEntryKnn<?> o1, PointEntryKnn<?> o2);
  }

  /** Default distance comparator for PointEntryKnn. */
  PEComparator PE_COMP_DEFAULT = (o1, o2) -> Double.compare(o1.dist, o2.dist);
}
