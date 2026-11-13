# Data Structures and Algorithms 2 - Final project

## Personal implementation
See package [MultiDimTrees.MyTrees](src/main/java/antofruch/final_project/MultiDimTrees/MyTrees)
Both of the KdTree and the QuadTree implements the same interface.

### KdTree rangeSearch implementation detail
See [KDimTree.java](src/main/java/antofruch/final_project/MultiDimTrees/MyTrees/KDimTree.java).

In the ``find()``method, we work with a ``KDAABB`` object (k-dimensional axis-aligned bounding box).
This class exposes methods such as ``getMax()``, ``getMin()`` and ``contains(T data)``

#### Pseudo code:

```text
results = empty List of data
queue = empty Queue of Node
add root node to queue

WHILE queue is not empty, DO 
    cur = pop first element from queue
                
    IF data is within searchRegion, THEN
        add data to results
    ENDIF
    
    work_axis = (depth of cur in the tree) % (dimension of the tree)
    
    IF searchRegion is entirely left to cur on work_axis, THEN
        add left child node to queue
        
    ELSE IF searchRegion is entirely right to cur on work_axis, THEN
        add right child node to queue
        
    ELSE
        add both child nodes to queue
        
    ENDIF
ENDWHILE

RETURN results
```

#### Source code:
```java
 /**
     * Find all the elements contained in the query region.
     * @param min - the minimum point of the query region
     * @param max - the maximum point of the query region
     * @return list of elements contained in the search region
     */
    @Override
    public List<T> find(final T min, final T max){
        // Preventive Programming
        if (min == null || max == null)
            throw new IllegalArgumentException("Min and max must not be null");

        KDAABB<T> searchRegion = new KDAABB<>(min, max, dimension, comparer);

        if (root == null)
            return new ArrayList<>();

        // Searching Logic
        List<T> results = new ArrayList<>();
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T> cur = queue.poll();

            // We add the data if it is contained in the search region
            if (searchRegion.contains(cur.data)) {
                results.add(cur.data);
            }

            // if search region in ENTIRELY left -> search only in left tree
            if (comparer.compare(searchRegion.getMax(), cur.data, cur.depth % dimension) < 0) {
                if (cur.left != null){
                    queue.add(cur.left);
                }
            } else
            // if search region in ENTIRELY right -> search only in right tree
            if (comparer.compare(searchRegion.getMin(), cur.data, cur.depth % dimension) > 0) {
                if (cur.right != null){
                    queue.add(cur.right);
                }
            } else
            // if it overlaps -> search in both
            {
                if (cur.left != null)
                    queue.add(cur.left);
                if (cur.right != null)
                    queue.add(cur.right);
            }
        }
        return results;
    }

```
#### Efficiency:
The algorithm has a time complexity of O(log n) for each query, where n is the number of nodes in the tree. This is because at each step, we eliminate half of the remaining nodes based on the comparison with the current node.


## External implementations

All external classes are adapted to [MultiDimTree](src/main/java/antofruch/final_project/MultiDimTrees/MultiDimTree.java) 
interface via an Adapter class.

See packages : 
 * [kd1](src/main/java/antofruch/final_project/MultiDimTrees/kd1) - [ext link](https://github.com/wrex1k/sp_aus2/tree/bc43737e7de6c45e2c243ac9a2f83abff2f1abb9/external/implementations/kd-tree/tinspin-indexes/src/main/java/org/tinspin/index)
 * [kd2](src/main/java/antofruch/final_project/MultiDimTrees/kd2) - [ext link](https://github.com/WeaveTeam/Weave/tree/db83f8a54c66c1e6a62f843ec7eb44649c62e2f0/GeometryStreamConverter/src/weave/utils)
 * [kd3](src/main/java/antofruch/final_project/MultiDimTrees/kd3)
 * [quad1](src/main/java/antofruch/final_project/MultiDimTrees/quad1) - [ext link]()
 * [quad2](src/main/java/antofruch/final_project/MultiDimTrees/quad2)
 * [quad3](src/main/java/antofruch/final_project/MultiDimTrees/quad3)

## Test Class

See [GlobalTests.java](src/test/java/MultiDimTrees/GlobalTests.java).

Tests methods run once per class provided by the ``provideTreeImplementation`` method. 
This works thanks to the common interface [MultiDimTree](src/main/java/antofruch/final_project/MultiDimTrees/MultiDimTree.java)


## Test Results
| Structure de données | No False Positives | Successful insertion test | Temps Total |
| :--- | :---: | :---: | :---: |
| QuadTree | 2,760 s | 0,535 s | **3,295 s** |
| KDimTree | 9,862 s | 1,389 s | **11,251 s** |
| QuadTreeKdTreeMix | 3,750 s | 0,920 s | **4,670 s** |
| ext_KdTree1 | 6,915 s | 1,239 s | **8,154 s** |
| ext_KdTree2 | 6,838 s | 1,330 s | **8,168 s** |
| ext_KdTree3 | 7,504 s | 3,043 s | **10,547 s** |
| ext_QuadTree1 | 4,906 s | 1,363 s | **6,269 s** |
| ext_QuadTree2 | 2,907 s | 0,979 s | **3,886 s** |

