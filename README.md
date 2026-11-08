# Data Structures and Algorithms 2 - Final project

## Personal implementation
See package [MultiDimTrees.MyTrees](src/main/java/antofruch/final_project/MultiDimTrees/MyTrees)
Both of the KdTree and the QuadTree implements the same interface.

## External implementations

All external classes are adapted to [MultiDimTree](src/main/java/antofruch/final_project/MultiDimTrees/MultiDimTree.java) 
interface via an Adapter class.

See packages : 
 * [kd1](src/main/java/antofruch/final_project/MultiDimTrees/kd1)
 * [kd2](src/main/java/antofruch/final_project/MultiDimTrees/kd2)
 * [kd3](src/main/java/antofruch/final_project/MultiDimTrees/kd3)
 * [quad1](src/main/java/antofruch/final_project/MultiDimTrees/quad1)
 * [quad2](src/main/java/antofruch/final_project/MultiDimTrees/quad2)
 * [quad3](src/main/java/antofruch/final_project/MultiDimTrees/quad3)

## Test Class

See [GlobalTests.java](src/test/java/MultiDimTrees/GlobalTests.java).

Tests methods run once per class provided by the ``provideTreeImplementation`` method. 
This works thanks to the common interface [MultiDimTree](src/main/java/antofruch/final_project/MultiDimTrees/MultiDimTree.java)


## Test Results